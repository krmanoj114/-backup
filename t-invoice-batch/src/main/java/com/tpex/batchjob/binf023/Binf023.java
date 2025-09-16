package com.tpex.batchjob.binf023;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.tpex.batchjob.common.TpexBatchContext;
import com.tpex.batchjob.common.TpexSendJobExecutionListener;
import com.tpex.batchjob.common.configuration.model.SendBatchConfig;
import com.tpex.batchjob.common.configuration.model.SendBatchFTPConfig;
import com.tpex.batchjob.common.configuration.model.SendBatchFileConfig;
import com.tpex.batchjob.common.configuration.model.SftpConfiguration;
import com.tpex.batchjob.common.receive.tasklets.FinishProcess;
import com.tpex.entity.InfErrorLogEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.Binf023CustomRepository;
import com.tpex.repository.InfErrorLogRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class Binf023 {

    private static final String[] COLUMN_NAMES = { "recordId", "packingMth", "requireDt", "containerSize",
    "kCST2DelryDt", "insDt", "etd1", "shpCmpnyCd", "shpCmpnyNm", "bookNo", "vesselNm1", "renbanCd", "destCd", "destName", "vanningPlant", "remark"};
    private static final String SQL_QUERY ="SELECT CAST((NVMC.PLN_VAN_END_DT + ((CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),1,2) AS FLOAT)*60) + \n" +
            "            CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),3,2) AS FLOAT))/(60*24) - ( SELECT CAST(PARA_VAL AS FLOAT) FROM TB_M_PARAMETER WHERE PARA_CD = 'REQ_LEAD_DAYS') )  AS CHAR(20)) REQ_DT,\n" +
            "            CASE NVMC.CONT_SIZE  WHEN '20' THEN  '1' WHEN '40' THEN '2' END CONT_TYP, \n" +
            "            CAST((NVMC.PLN_VAN_END_DT + (((CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),1,2) AS FLOAT)*60) + \n" +
            "            (CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),3,2) AS FLOAT)) + ( SELECT CAST(PARA_VAL AS FLOAT) FROM TB_M_PARAMETER WHERE PARA_CD = 'KCST_LEAD_TIME')*60)/(60*24) ) - ( SELECT CAST(PARA_VAL AS FLOAT) FROM TB_M_PARAMETER WHERE PARA_CD = 'REQ_LEAD_DAYS')) AS CHAR(25)) KCST_DT, \n" +
            "NRBM.ETD1 tETD,\n" +
            "RPAD(OSCM.SHIPNAME, 30,' ')    SHP_COMP_NM,\n" +
            "RPAD(NRBM.BOOK_NO, 15, ' ') BOOKING_NO,\n" +
            "RPAD(NRBM.VESSEL1, 30, ' ') VESSEL,\n" +
            "NRBD.CONT_GRP_CD RENBAN_CD,\n" +
            "RPAD(NVMC.CONT_DST_CD,4,' ')            DEST_CD,\n" +
            "RPAD(COALESCE(OFDM.DST_NM,' '), 20, ' ') DESTINATION,\n" +
            "NVMC.VAN_PLNT_CD                        VAN_PLNT,\n" +
            "RPAD(COALESCE('',' '), 50, ' ') REMARK,\n" +
            "\n" +
            "CAST(NRBM.ETD1 - ( SELECT CAST(PARA_VAL AS FLOAT) FROM TB_M_PARAMETER WHERE PARA_CD = 'INSP_LEAD_DAYS' ) AS CHAR(20)) INSPEC_DT,\n" +
            "RPAD(NRBM.SHP_NM_1,4,' ')               SHP_COMP_CD,  \n" +
            "NVMC.PKG_MTH PKG_MTH   \n" +
            "\n" +
            "\n" +
            "FROM\n" +
            "\n" +
            "(SELECT CNTR.PKG_MTH, CNTR.CONT_DST_CD, CNTR.ETD_1, CNTR.CONT_GRP_CD, CNTR.SHP_NM_1, CNTR.PLN_VAN_END_DT, \n" +
            "   COALESCE(CNTR.PLN_VAN_END_TM, '0000') AS PLN_VAN_END_TM, CNTR.CONT_SIZE, CNTR.VAN_PLNT_CD, CNTR.CMP_CD\n" +
            "   FROM   TB_R_VPR_MTH_CONTAINER CNTR\t\t\t\n" +
            "   WHERE  CNTR.PKG_MTH    =  ':mstrVanMonth'\n" +
            "   AND    CNTR.CONT_TYP   = 'F'\n" +
            "   AND   NOT EXISTS \n" +
            "   (SELECT 1 FROM   TB_R_ETD_CONTAINER\tETDC\t\t   \n" +
            "   WHERE  CNTR.PKG_MTH= ETDC.PKG_MTH\n" +
            "   AND  CNTR.CONT_DST_CD= ETDC.CONT_DST_CD   \n" +
            "   AND  CNTR.ETD_1= ETDC.OLD_ETD_1 \n" +
            "   AND ETDC.ETD_1       <> ETDC.OLD_ETD_1   \n" +
            "   AND  CNTR.CONT_GRP_CD = ETDC.CONT_GRP_CD   \n" +
            "   AND  CNTR.SHP_NM_1 = ETDC.SHP_NM_1\n" +
            "     )\n" +
            "   UNION ALL               \n" +
            "  SELECT PKG_MTH, CONT_DST_CD, ETD_1, CONT_GRP_CD, SHP_NM_1, PLN_VAN_END_DT, \n" +
            "   COALESCE(PLN_VAN_END_TM,'0000'), CONT_SIZE, VAN_PLNT_CD , CMP_CD              \n" +
            "   FROM   TB_R_VPR_MTH_KEIHEN_CONTAINER\t\t\t A               \n" +
            "   WHERE  PKG_MTH    =  ':mstrVanMonth'                \n" +
            "   AND    CONT_TYP   = 'F'   \n" +
            "   AND    CONCAT(COALESCE(KEIHEN_MTH, '') , COALESCE(KEIHEN_REV_NO, ''))=\n" +
            "   (SELECT CONCAT(COALESCE(MAX(KEIHEN_MTH), '') , COALESCE(MAX(KEIHEN_REV_NO), ''))\n" +
            "   FROM  TB_R_VPR_MTH_KEIHEN_CONTAINER\t\t\t B\n" +
            "   WHERE PKG_MTH =  ':mstrVanMonth' \n" +
            "   AND   \n" +
            "   KEIHEN_MTH = (SELECT MAX(KEIHEN_MTH) \n" +
            "   FROM TB_R_VPR_MTH_KEIHEN_CONTAINER\t\t\t \n" +
            "   WHERE PKG_MTH =  ':mstrVanMonth'  )\n" +
            "   AND   B.CONT_DST_CD = A.CONT_DST_CD )\n" +
            "   AND    NOT EXISTS  \n" +
            "   (SELECT 1  FROM   TB_R_VPR_MTH_CONTAINER\t\t\t WHERE    PKG_MTH      = A.PKG_MTH \n" +
            "   AND      CONT_DST_CD  = A.CONT_DST_CD                              \n" +
            "   AND      ETD_1        = A.ETD_1                              \n" +
            "   AND      CONT_GRP_CD  = A.CONT_GRP_CD                              \n" +
            "   AND      SHP_NM_1     = A.SHP_NM_1                              \n" +
            "   AND      CONT_TYP     = 'F')               \n" +
            "   AND    NOT EXISTS (SELECT 1 FROM   TB_R_ETD_CONTAINER\t\t\t\n" +
            "   WHERE  A.PKG_MTH      = PKG_MTH                                  \n" +
            "   AND    A.CONT_DST_CD  = CONT_DST_CD                                  \n" +
            "   AND    A.ETD_1        = OLD_ETD_1                                 \n" +
            "   AND    ETD_1        <> OLD_ETD_1                                  \n" +
            "   AND    A.CONT_GRP_CD  = CONT_GRP_CD                                  \n" +
            "   AND    A.SHP_NM_1     = SHP_NM_1)\n" +
            "   UNION ALL\n" +
            "SELECT PKG_MTH, CONT_DST_CD, ETD_1, CONT_GRP_CD, SHP_NM_1, PLN_VAN_END_DT, \n" +
            "COALESCE(PLN_VAN_END_TM,'0000'), CONT_SIZE,VAN_PLNT_CD, CMP_CD\n" +
            "FROM   TB_R_DLY_VPR_CONTAINER\tA\t\t\n" +
            "WHERE  PKG_MTH    =  ':mstrVanMonth'\n" +
            "AND    CONT_TYP   = 'F' \n" +
            "AND   NOT EXISTS (SELECT 1 FROM   TB_R_VPR_MTH_CONTAINER\t\t\t WHERE  PKG_MTH     = A.PKG_MTH\n" +
            " AND   CONT_DST_CD= A.CONT_DST_CD\n" +
            " AND   ETD_1= A.ETD_1\n" +
            " AND CONT_GRP_CD = A.CONT_GRP_CD\n" +
            " AND   SHP_NM_1= A.SHP_NM_1\n" +
            " AND    CONT_TYP    = 'F') ) NVMC, \n" +
            " TB_R_MTH_RENBAN_BOOKING_H NRBM,\n" +
            " TB_R_MTH_RENBAN_BOOKING_D NRBD,\n" +
            " TB_M_SHIP_CMP OSCM, \n" +
            " TB_M_FINAL_DESTINATION OFDM\n" +
            " WHERE    NVMC.CONT_DST_CD = OFDM.DST_CD\n" +
            "AND   NRBM.SHP_NM_1 = OSCM.CD\n" +
            "AND   NRBM.GROUP_ID  = NRBD.GROUP_ID\n" +
            "AND   NRBM.SHP_NM_1  = NRBD.SHP_NM_1\n" +
            "AND   NRBM.ETD1  = NRBD.ETD1\n" +
            "AND   NRBM.CONT_DST_CD = NRBD.CONT_DST_CD\n" +
            "AND   NRBM.CONT_VAN_MTH= NRBD.CONT_VAN_MTH\n" +
            "AND   NVMC.PKG_MTH = NRBM.CONT_VAN_MTH\n" +
            "AND   NVMC.ETD_1 = NRBM.ETD1\n" +
            "AND   NVMC.CONT_DST_CD = NRBM.CONT_DST_CD\n" +
            "AND   NVMC.CONT_GRP_CD = NRBD.CONT_GRP_CD\n" +
            "AND   NVMC.SHP_NM_1  = NRBM.SHP_NM_1\n" +
            "AND   NRBM.CB_FLG  = 'N' \n" +
            "AND   NVMC.PKG_MTH =  ':mstrVanMonth' \n" +
            "AND   NVMC.CMP_CD = ':companyCode'   \n" +
            "AND   NRBM.CB_CD = ':cbCd'   \n" +
            "ORDER BY NRBM.CB_CD,BOOKING_NO, -- DESC,                  \n" +
            "   DATE_FORMAT(NVMC.PLN_VAN_END_DT + ((CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),1,2) AS FLOAT)*60) + \n" +
            "   CAST(SUBSTR(COALESCE(NVMC.PLN_VAN_END_TM,'0000'),3,2) AS FLOAT))/(60*24) - CAST( (SELECT PARA_VAL FROM TB_M_PARAMETER\tWHERE PARA_CD = 'REQ_LEAD_DAYS')  AS FLOAT) , '%d%m%Y%H%i%s'),\n" +
            "                    CASE NVMC.CONT_SIZE  WHEN '20' THEN  '1' WHEN '40' THEN '2' END,                 \n" +
            "   DATE_FORMAT(NRBM.ETD1 - CAST( (SELECT PARA_VAL FROM TB_M_PARAMETER\tWHERE PARA_CD = 'REQ_LEAD_DAYS')  AS FLOAT), '%d%m%Y%H%i%s'),                \n" +
            "   DATE_FORMAT(NRBM.ETD1, '%d%m%Y%H%i%s'), RPAD(NRBM.SHP_NM_1,4,' '), NRBD.CONT_GRP_CD;";

    private static final int[] MANDATORY_COLUMNS =
            {
                    0,1,2,3,5,6,7,8,10,11,14
            };
    private static final String VAN_MONTH = "mstrVanMonth";
    private static final String COMPANY_CODE = "companyCode";
    private static final String CB_CODE = "cbCd";
    private static final String FILE_PATH = "filePath";
    private static final String BINF023JOB = "binf023Job";

    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    SimpleDateFormat dbDf = new SimpleDateFormat(GlobalConstants.DATETIME_FORMAT);

    /**
     * Instance of logger, to log information (if any).
     *
     */
    private Logger logger = LoggerFactory.getLogger(Binf023.class);


    @Value("${binf023.configuration.file}")
    private String configFileName;

    @Autowired
    private InfErrorLogRepository infErrorLogRepository;

    @Autowired
    private ProcessLogDtlsRepository processLogDtlsRepository;

    private JobRepository jobRepository;
    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * PlatformTransactionManager instance.
     *
     */
    private PlatformTransactionManager transactionManager;
    @Autowired
    public void setPlatformTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    private ProcessLogDtlsUtil processLogDtlsUtil;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Binf023CustomRepository binf023CustomRepository;

    private DataSource dataSource;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void binf023JobRunTMT() throws  JobParametersInvalidException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobRestartException {

        String vanMonth = binf023CustomRepository.getVanningMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(GlobalConstants.YEAR_MONTH);
        String currDate = formatter.format(LocalDate.now());
        if(vanMonth!=null){
            if(vanMonth.equals(currDate)){
                runBinf023(currDate);
            }else{
                runBinf023(currDate);
                runBinf023((Integer.parseInt(vanMonth) + 1)+"");
            }
        }
    }

    private void runBinf023(String vanMonth) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(job(),getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.KTL));
        jobLauncher.run(job(), getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.NYK));
        jobLauncher.run(job(), getJobParameters(ConstantUtils.TMT,vanMonth,ConstantUtils.MOL));
        jobLauncher.run(job(), getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.KTL));
        jobLauncher.run(job(), getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.NYK));
        jobLauncher.run(job(), getJobParameters(ConstantUtils.STM,vanMonth,ConstantUtils.MOL));
    }

    private JobParameters getJobParameters(String cmpCd, String vanMonth, String cbCd) {
        return new JobParametersBuilder()
                .addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
                .addString(ConstantUtils.JOB_P_BATCH_ID,ConstantUtils.BINF023)
                .addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINF023NAME)
                .addString(COMPANY_CODE, cmpCd)
                .addString(VAN_MONTH, vanMonth)
                .addString(CB_CODE,cbCd)
                .toJobParameters();
    }

    public SendBatchConfig getConfiguration(String configFileName) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference<SendBatchConfig>() {});
        } catch (IOException ioe) {
            logger.error("IOException exception occurred.");
        }

        return null;
    }

    @Bean(name="binf023BatchContext")
    public TpexBatchContext<SendBatchConfig> prepareContext() {
        return new TpexBatchContext <> (getConfiguration(configFileName));
    }

    @Bean(name = "binf023ExecutionListener")
    public TpexSendJobExecutionListener<SendBatchConfig> executionListener(){
        return new TpexSendJobExecutionListener<>(prepareContext());
    }

    @Bean(name = "binf023Job")
    public Job job() {
        return new JobBuilder(BINF023JOB)
                .incrementer(new RunIdIncrementer())
                .listener(executionListener())
                .repository(jobRepository)
                .start(prepareFile(jobRepository, transactionManager)).on("FAILED").to(finish(jobRepository, transactionManager))
                .from(prepareFile(jobRepository, transactionManager))
                .next(sftpFile(jobRepository, transactionManager))
                .next(archiveFile(jobRepository, transactionManager))
                .next(finish(jobRepository, transactionManager))
                .end()
                .build();
    }

    SftpUploadGateway sftpUploadGateway;
    @Autowired
    public void setSftpUploadGateway(SftpUploadGateway sftpUploadGateway) {
        this.sftpUploadGateway = sftpUploadGateway;
    }

    @Bean(name = "binf023Sftp")
    protected Step sftpFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("binf023Sftp")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .tasklet(new Tasklet() {

                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        sftpUploadGateway.sendToSftp(new FileSystemResource(
                                chunkContext.getStepContext().getJobExecutionContext().get(FILE_PATH).toString()).getFile());

                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    private SessionFactory<LsEntry> sftpSessionFactory(SftpConfiguration sftpConfiguration) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(sftpConfiguration.getHost());
        factory.setPort(sftpConfiguration.getPort());
        factory.setUser(sftpConfiguration.getUser());
        if (sftpConfiguration.getPrivateKey() != null) {
            Resource resource = new DefaultResourceLoader().getResource(sftpConfiguration.getPrivateKey());
            factory.setPrivateKey(resource);
            factory.setPrivateKeyPassphrase(sftpConfiguration.getPrivateKeyPassphrase());
        } else {
            factory.setPassword(sftpConfiguration.getPassword());
        }
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    @ServiceActivator(inputChannel = "binf023SftpChannel")
    public MessageHandler handler1() {

        SftpConfiguration sftpConfiguration = new SftpConfiguration();
        SendBatchConfig sendBatchConfig = getConfiguration(configFileName);
        if (sendBatchConfig != null) {
            SendBatchFTPConfig sendFTPConfig = sendBatchConfig.getFileInfo().getOutputFileNames().get(0).getFtp().get(0);
            sftpConfiguration.setHost(sendFTPConfig.getIpaddress());
            sftpConfiguration.setPort(sendFTPConfig.getPort().isBlank() ? 0 : Integer.valueOf(sendFTPConfig.getPort()));
            sftpConfiguration.setUser(sendFTPConfig.getUser());
            sftpConfiguration.setPassword(sendFTPConfig.getPassword());
            sftpConfiguration.setDirectory(sendFTPConfig.getTargetDirectory());
        }
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory(sftpConfiguration));
        handler.setRemoteDirectoryExpression(new LiteralExpression(sftpConfiguration.getDirectory()));
        handler.setFileNameGenerator((Message<?> message) -> {
            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else {
                throw new IllegalArgumentException("File expected as payload.");
            }
        });

        return handler;
    }

    @MessagingGateway
    public interface SftpUploadGateway {
        @Gateway(requestChannel = "binf023SftpChannel")
        void sendToSftp(File file);
    }

    @Bean(name = "binf023ArchiveFile")
    protected Step archiveFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("binf023ArchiveFile")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .tasklet(new Tasklet() {

                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
                                .getStepExecutions().stream().findFirst();
                        if (stepExecution.isPresent()) {
                            SendBatchConfig sendBatchConfig = (SendBatchConfig) stepExecution.get().getJobExecution().getExecutionContext().get(GlobalConstants.IF_CONFIG);
                            if (sendBatchConfig != null) {
                                Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(stepExecution.get().getJobParameters().getString(ConstantUtils.COMPANY_CODE))).filter(m->m.getCbCode().equals(stepExecution.get().getJobParameters().getString(CB_CODE))).findFirst();
                                if (sendBatchFileConfigOptional.isPresent()) {
                                    SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
                                    String filePath = stepExecution.get().getJobExecution().getExecutionContext().getString(ConstantUtils.FILEPATH);
                                    if (FileUtils.getFile(filePath).exists()) {
                                        binf023CustomRepository.updateVesselBookingMaster(FileUtils.getFile(filePath),stepExecution.get().getJobParameters().getString(VAN_MONTH));
                                        FileUtils.moveFile(FileUtils.getFile(filePath),
                                                FileUtils.getFile(filePath.replace(sendBatchFileConfig.getOutputFolder(), sendBatchFileConfig.getArchiveFolder())));
                                    }
                                }
                            }
                        }
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean(name = "binf023Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("binf023Finish")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .tasklet(finishProcess())
                .build();
    }

    @Bean(name = "binf023FinishProcess")
    Tasklet finishProcess() {
        return new FinishProcess();
    }

    FlatFileItemWriter<Binf023OutputDto> writer;

    @Autowired
    @Qualifier("binf023ItemWriter")
    public void setFlatFileItemWriter(FlatFileItemWriter<Binf023OutputDto> writer) {
        this.writer = writer;
    }

    @Bean(name = "binf023PrepareFile")
    protected Step prepareFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("binf023PrepareFile")
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .allowStartIfComplete(true)
                .<Binf023OutputDto, Binf023OutputDto> chunk(5)
                .reader(itemReader1(null, null, null))
                .processor(itemProcessor1(null))
                .writer(writer)
                .listener(stepExecutionListener())
                .build();
    }

    @Bean(name = "binf023StepExecutionListener")
    public Binf023StepExecutionListener stepExecutionListener(){
        return new Binf023StepExecutionListener();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<Binf023OutputDto> itemReader1(@Value("#{jobParameters[companyCode]}") String companyCode,
                                                              @Value("#{jobParameters[mstrVanMonth]}") String mstrVanMonth,
                                                              @Value("#{jobParameters[cbCd]}") String cbCd) {
        String query = SQL_QUERY;
        if(companyCode!=null && mstrVanMonth!=null && cbCd!=null){
            query = SQL_QUERY.replace(":mstrVanMonth", mstrVanMonth).replace(":cbCd",cbCd).replace(":companyCode", companyCode);
        }

        return new JdbcCursorItemReaderBuilder<Binf023OutputDto>()
                .dataSource(this.dataSource)
                .name("binf023ItemReader")
                .sql(query)
                .rowMapper(new Binf023RowMapper())
                .build();

    }
    boolean isError = Boolean.FALSE;
    @Bean
    @StepScope
    public ItemProcessor<Binf023OutputDto, Binf023OutputDto> itemProcessor1(@Value("#{stepExecution}") StepExecution context) {
        return new ItemProcessor<Binf023OutputDto, Binf023OutputDto>() {

            @Override
            public Binf023OutputDto process(Binf023OutputDto item) throws Exception {
                validateRecord(context, item);
                return item;
            }
        };
    }

    protected void validateRecord(StepExecution context, Binf023OutputDto item) {
        List<String> errorMsgList = new ArrayList<>();

        String[] dataColumns = {item.getRecordId(), item.getPackingMth(), item.getRequireDt(), item.getContainerSize(),
                item.getKCST2DelryDt(), item.getInsDt(), item.getEtd1(), item.getShpCmpnyCd(),
                item.getShpCmpnyNm(), item.getBookNo(), item.getDestCd(), item.getDestName(),item.getVesselNm1(), item.getRenbanCd(),
                item.getVanningPlant()};

        int[] mandColumns = MANDATORY_COLUMNS;
        if(dataColumns[9] == null || dataColumns[9].trim().equals("")){
            errorMsgList.add("No. of Containers change, booking not updated. "+ "Destination Code : "+ dataColumns[10]+" Container : " +dataColumns[13]);
        }
        for (int i = 0; i < mandColumns.length; i++) {
            if (dataColumns[mandColumns[i]] == null || dataColumns[mandColumns[i]].trim().equals("") ||  dataColumns[mandColumns[i]].isBlank()) {
                errorMsgList.add(String.format(ConstantUtils.MANDATORY_COLUMNS_BINF023_NOT_AVAIL, COLUMN_NAMES[MANDATORY_COLUMNS[i]]));
            }
        }

        SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
        if (!errorMsgList.isEmpty() && sendBatchConfig != null) {
            saveErrorLogs(context, sendBatchConfig, errorMsgList);
        }

        if (!isError && !errorMsgList.isEmpty()) {
            isError = Boolean.TRUE;
        }
        context.getJobExecution().getExecutionContext().put(GlobalConstants.IS_ERROR, isError);
    }

    private void saveErrorLogs(StepExecution context, SendBatchConfig sendBatchConfig, List<String> errorMsgList) {
        InfErrorLogEntity infErrorLogEntity = new InfErrorLogEntity();
        infErrorLogEntity.setLogId(getNextInfErrorLogId());
        infErrorLogEntity.setProgramId(sendBatchConfig.getBatchId());
        String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(ConstantUtils.COMPANY_CODE), context.getJobParameters().getString(CB_CODE), context.getJobExecution().getStartTime());
        infErrorLogEntity.setFileName(fileName);
        infErrorLogEntity.setErrorType("ERROR");
        infErrorLogEntity.setErrorDate(Timestamp.valueOf(LocalDateTime.now()));
        infErrorLogEntity.setExecuteDate(Timestamp.valueOf(dbDf.format(context.getJobExecution().getStartTime())));
        infErrorLogEntity.setCmpCd(context.getJobParameters().getString(ConstantUtils.COMPANY_CODE));

        ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
        try {
            processLogDtlsEntity.setId(processLogDtlsUtil.getNextProcessLogId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        processLogDtlsEntity.setProcessControlId(context.getJobExecution().getExecutionContext().getInt(ConstantUtils.JOB_P_PROCESS_CTRL_ID));
        processLogDtlsEntity.setProcessId(context.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_ID));
        processLogDtlsEntity.setProcessName(context.getJobParameters().getString(ConstantUtils.JOB_P_BATCH_NAME));
        processLogDtlsEntity.setProcessLogStatus(ConstantUtils.PL_STATUS_ERROR);
        processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(LocalDateTime.now()));
        processLogDtlsEntity.setCorrelationId(processLogDtlsUtil.getCurrentSpanId());

        for (String errorMsg : errorMsgList) {
            infErrorLogEntity.setMessage(errorMsg);
            infErrorLogRepository.save(infErrorLogEntity);

            processLogDtlsEntity.setProcessLogMessage(errorMsg);
            processLogDtlsRepository.save(processLogDtlsEntity);
        }
    }

    @Bean("binf023ItemWriter")
    @StepScope
    public FlatFileItemWriter<Binf023OutputDto> itemWriter(@Value("#{stepExecution}") StepExecution context) {
        SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
        if (sendBatchConfig != null) {
            Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(context.getJobParameters().getString(COMPANY_CODE))).filter(m->m.getCbCode().equals(context.getJobParameters().getString(CB_CODE))).findFirst();
            if (sendBatchFileConfigOptional.isPresent()) {
                SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
                StringBuilder filePath = new StringBuilder(sendBatchFileConfig.getOutputFolder()).append("/").append(sendBatchFileConfig.getFileName().replace("YYYYMMDDHHMISS", df.format(context.getJobExecution().getStartTime())));
                context.getJobExecution().getExecutionContext().put(FILE_PATH, filePath.toString());
                return new FlatFileItemWriterBuilder<Binf023OutputDto>()
                        .name("binf023ItemWriter")
                        .resource(new FileSystemResource(filePath.toString()))
                        .delimited()
                        .delimiter(sendBatchConfig.getLineSeparator().getData())
                        .names(COLUMN_NAMES)
                        .headerCallback(headerCallback(context))
                        .footerCallback(footerCallback1(context))
                        .build();
            }
        }
        return null;
    }

    @Bean
    @StepScope
    public FlatFileFooterCallback footerCallback1(@Value("#{stepExecution}") StepExecution context) {
        return new FlatFileFooterCallback() {

            @Override
            public void writeFooter(Writer writer) throws IOException {
                SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
                String separator = sendBatchConfig == null || sendBatchConfig.getLineSeparator().getTrailer() == null ? "#" : sendBatchConfig.getLineSeparator().getTrailer();
                StringBuilder sb = new StringBuilder(separator).append("T").append(separator).append(String.format("%07d", context.getWriteCount()+2088)).append(separator);
                writer.write(sb.toString());
            }
        };
    }

    public FlatFileHeaderCallback headerCallback(@Value("#{stepExecution}") StepExecution context) {
        return new FlatFileHeaderCallback() {

            @Override
            public void writeHeader(Writer writer) throws IOException {
                SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
                if (sendBatchConfig != null) {
                    String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(COMPANY_CODE),context.getJobParameters().getString(CB_CODE), context.getJobExecution().getStartTime());
                    String separator = sendBatchConfig.getLineSeparator().getHeader() == null ? "#" : sendBatchConfig.getLineSeparator().getHeader();

                    StringBuilder sb = new StringBuilder(separator).append("H").append(separator);
                    sb.append("OEM").append(separator); //From system
                    sb.append("CBS").append(separator); //To system
                    sb.append(fileName).append(separator);	//fileName
                    sb.append("MTH_CNT").append(separator); //interface identifier
                    sb.append("00219").append(separator); //record length of data

                    writer.write(sb.toString());
                }
            }
        };
    }
    private SendBatchConfig getSendBatchConfig(ExecutionContext executionContext) {
        return (SendBatchConfig) executionContext.get(GlobalConstants.IF_CONFIG);
    }

    private String getFileName(SendBatchConfig sendBatchConfig, String companyCode, String cbCd,Date startTime) {
        String fileName = "";
        Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(companyCode)).filter(m->m.getCbCode().equals(cbCd)).findFirst();
        if (sendBatchFileConfigOptional.isPresent()) {
            SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
            fileName = sendBatchFileConfig.getFileName().replace("YYYYMMDDHHMISS", df.format(startTime));
        }
        return fileName;
    }
    private int getNextInfErrorLogId() {
        InfErrorLogEntity infErrorLogEntity = infErrorLogRepository.findTopByOrderByLogIdDesc();
        return (infErrorLogEntity == null) ? 1 : infErrorLogEntity.getLogId() + 1;
    }
}
