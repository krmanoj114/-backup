package com.tpex.batchjob.bins006;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
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
import com.tpex.batchjob.common.send.tasklets.ArchiveFile;
import com.tpex.entity.InfErrorLogEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.repository.InfErrorLogRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;
import com.tpex.util.ProcessLogDtlsUtil;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class Bins006 {

	private static final String[] COLUMN_NAMES = { "recordId", "seqId", "invDate","invNo","docReference",
			"docType","cancelFlag","customerCode","sapCode","movementType","partNo","packingTimestamp",
			"packingPlant","packingRefLoc","packingRefLocType","partQty","currency","destName","destCode",
			"cfCode","series","etd","exchangeRate","freight","insurance","shipModuleNo","unitPrice","totalAmount",
			"sourceDate","exportCreditAmt","commercialFlag","orderType","measurement","partType",
			"dsiTiNo","transportMode"};
	
	private static final String SQL_QUERY = "SELECT DISTINCT "
	         + " date_format(IND.INV_DT, '%Y%m%d') AS INV_DT,"
	         + " RPAD(IND.INV_NO, 10, ' ') AS INV_NO,"
	         + " IFNULL(IND.ICS_FLG, 'N') AS ICS_FLG,"
	         + " RPAD(CNSG.CD, 5, ' ') AS CNSG_CD,"
	         + "   RPAD(IFNULL(TRIM(CNSG.SAP_CD),'-'), 6, ' ') AS SAP_CD,"
	         + "   RPAD(IND.PAY_CRNCY, 5, ' ') AS PAY_CRNCY,"
	         + "   RPAD(FDT.DST_NM, 40, ' ') AS DST_NM,"
	         + "   date_format(IND.ETD, '%Y%m%d') AS ETD,"
	         + "   IND.EXCH_RT,"
	         + "   FORMAT(IFNULL(IND.FREIGHT, '0'), 2) AS FREIGHT,"
	         + " FORMAT(IFNULL(IND.INSURANCE, '0'), 2) AS INSURANCE,"
	         + "   FORMAT(IND.INV_AMT, 2) AS INV_AMT,"
	         + "   FORMAT(IFNULL(IND.EXP_CREDIT, '0'), 2) AS EXP_CREDIT,"
	         + "   RPAD(IND.FINAL_DST, 4, ' ') AS FINAL_DST,"
	         + " IFNULL(IND.NONCOM_FLG, 'N') AS NONCOM_FLG, "
	         + "   case "
	         + "  when IND.ORD_TYP = 'R' then 1"
	         + "  when IND.ORD_TYP = 'C' then 2"
	         + "        when IND.ORD_TYP = 'S' then 3"
	         + " end AS ORD_TYP,"
	         + "   RPAD(IFNULL(DSI_TI_NO, ' '), 10, ' ') AS DSI_TI_NO, "
	         + "    IND.TRNSPRT_CD,"
	         + " PARTD.SHP_MOD_NO,"
	         + "    PARTD.PRICE,"
	         + "    PARTD.PART_NO,"
	         + "    PARTD.ACT_PKG_DT,"
	         + "    PARTD.UNIT_PER_BOX,"
	         + "    PARTD.CF_CD,"
	         + "    PARTD.SERIES,"
	         + "    PARTD.PCK_PLNT,"
	         + "    PARTD.PCK_LN_CD,"
	         + "    PARTD.TOT_M3,"
	         + "    PARTD.PRM_TYP"
	         + " FROM tb_r_inv_invoice_h AS IND, tb_m_cnsg AS CNSG, tb_m_final_destination AS FDT, tb_r_dly_module_d AS DMOD"
	         + " LEFT JOIN (SELECT "
	         + "  INM.INV_NO AS INV_NO,"
	         + "        RPAD(IFNULL(INM.BUN_MOD,INM.LOT_NO),10,' ') AS SHP_MOD_NO,"
	         + "          FORMAT(INP.PRC, 2) AS PRICE,"
	         + "          RPAD(SUBSTR(INP.PART_NO, 1, 12), 12, ' ') AS PART_NO,"
	         + "          date_format(INM.ACT_PKG_DT, '%Y%m%d%H%i%s') AS ACT_PKG_DT,"
	         + "          LPAD(SUM(INP.UNIT_PER_BOX), 6, '0') AS UNIT_PER_BOX,"
	         + "          RPAD(coalesce(INP.CF_CD, ' '), 4, ' ') AS CF_CD,"
	         + "          RPAD(INP.SERIES, 15, ' ') AS SERIES, "
	         + "                       INM.PCK_PLNT, coalesce(INM.PCK_LN_CD, '  ') AS PCK_LN_CD,"
	         + "          FORMAT(INM.TOT_M3, 3) AS TOT_M3,"
	         + "                       case"
	         + "       when OPM.TYP = 1 then '10'"
	         + "                            when OPM.TYP = 3 then '2 '"
	         + "                            when OPM.TYP = 2 then 'I'"
	         + "                            else 'X'"
	         + "      end as PRM_TYP "
	         + "      FROM tb_r_inv_part_d AS INP, tb_r_inv_module_d AS INM, tb_m_part AS OPM"
	         + "       WHERE INP.PART_NO = OPM.PART_NO"
	         + "         AND INP.INV_NO = INM.INV_NO"
	         + "         AND INP.INV_DT = INM.INV_DT"
	         + "         AND INP.LOT_NO = INM.LOT_NO"
	         + "         AND INP.MOD_NO = INM.MOD_NO"
	         + "       GROUP BY INM.INV_NO, RPAD(IFNULL(INM.BUN_MOD,INM.LOT_NO),10,' '),"
	         + "                INP.PRC, INP.PART_NO,"
	         + "             INM.ACT_PKG_DT, "
	         + "          INP.CF_CD, INP.SERIES, PCK_PLNT,coalesce(INM.PCK_LN_CD, '  '),"
	         + "                FORMAT(TOT_M3, 3),"
	         + "                               OPM.TYP"
	         + ") AS PARTD ON PARTD.INV_NO = DMOD.INV_NO"
	         + " WHERE IND.BUYER = CNSG.CD"
	         + " AND IND.CMP_CD = ':companyCode' "
	         + " AND IND.ETD <= sysdate() "
	         + "  AND  (IND.ICS_FLG IS NULL OR IND.ICS_FLG = 'N')"
	         + "  AND  IND.PRINT_FLG = 'Y'"
	         + "  AND  FDT.DST_CD = IND.FINAL_DST"
	         + "  AND DMOD.INV_NO = IND.INV_NO;";
	
	private static final int[] MAND_REG_D =
	{
		0,2,3,5,7,9,10,11,12,15,16,18,20,21,
		25,26,27,31,33,35
	};
	
	private static final int[] MAND_NCS_D =
	{
		0,2,3,5,7,9,10,11,12,15,16,18,20,21,
		25,26,27,31,33,34,35
	};
	
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
	SimpleDateFormat dbDf = new SimpleDateFormat(GlobalConstants.DATETIME_FORMAT);
	
	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Bins006.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${bins006.configuration.file}")
	private String configFileName;
	
	@Autowired
	private InfErrorLogRepository infErrorLogRepository;
	
	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	@Autowired 
	private ProcessLogDtlsUtil processLogDtlsUtil;
	
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
	private JobLauncher jobLauncher;
	
    private DataSource dataSource;
	@Autowired
	public void setDataSource(DataSource dataSource) {
	    this.dataSource = dataSource;
	}
	
	@Scheduled(cron = "0 0 0 * * *") 
	public void bins006JobRunTMT() throws JobExecutionAlreadyRunningException, 
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("startAt", System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINS006)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINS006)
				.addString(ConstantUtils.JOB_P_SYSTEM_NM, "SAP")
				.addString(ConstantUtils.COMPANY_CODE, "TMT")
				.toJobParameters();
		
		jobLauncher.run(job(), jobParameters);
	}
	
	@Scheduled(cron = "0 0 0 * * *") 
	public void bins006JobRunSTM() throws JobExecutionAlreadyRunningException, 
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("startAt", System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINS006)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINS006)
				.addString(ConstantUtils.JOB_P_SYSTEM_NM, "SAP")
				.addString(ConstantUtils.COMPANY_CODE, "STM")
				.toJobParameters();
		jobLauncher.run(job(), jobParameters);
	}
	
	/**
	 * Gets the configuration.
	 *
	 * @param configFileName the config file name
	 * @return the configuration
	 */
	public SendBatchConfig getConfiguration(String configFileName) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference <SendBatchConfig> () {});
		} catch (IOException ioe) {
			logger.error("IOException exception occurred.");
		}
		
		return null;
	}
	
	/**
	 * Method to add information required in various steps in context.
	 * 
	 * @return
	 */
	@Bean(name="bins006BatchContext")
	public TpexBatchContext <SendBatchConfig> prepareContext() {
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}	
	
	@Bean(name = "bins006ExecutionListener")
	public TpexSendJobExecutionListener <SendBatchConfig> executionListener(){
		return new TpexSendJobExecutionListener<> (prepareContext());
	}

    @Bean(name = "bins006Job")
    public Job job() {

    	return new JobBuilder("bins006Job")
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
    
    @Bean(name = "bins006Sftp")
    protected Step sftpFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("bins006Sftp")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(new Tasklet() {
					
					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
						sftpUploadGateway.sendToSftp(new FileSystemResource(
								chunkContext.getStepContext().getJobExecutionContext().get(ConstantUtils.FILEPATH).toString()).getFile());
						
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
    @ServiceActivator(inputChannel = "bins006SftpChannel")
    public MessageHandler handler() {
		
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
    	@Gateway(requestChannel = "bins006SftpChannel")
        void sendToSftp(File file);
    }
    
    @Bean(name = "bins006ArchiveFile")
    Tasklet archiveFile() {
        return new ArchiveFile();
    }
    
    @Bean(name = "binf006ArchiveFile")
    protected Step archiveFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("binf006ArchiveFile")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(archiveFile())
				.build();
    }
    
    @Bean(name = "binf006Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("binf006Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(finishProcess())
				.build();
    }
    
    @Bean(name = "bins006FinishProcess")
    Tasklet finishProcess() {
        return new FinishProcess();
    }
    
    FlatFileItemWriter<Bins006OutputDto> writer;
	@Autowired
	@Qualifier("bins006ItemWriter")
	public void setFlatFileItemWriter(FlatFileItemWriter<Bins006OutputDto> writer) {
	    this.writer = writer;
	}
    
	@Bean(name = "bins006StepExecutionListener")
	public Bins006StepExecutionListener stepExecutionListener(){
		return new Bins006StepExecutionListener();
	}
	
    @Bean(name = "bins006PrepareFile")
    protected Step prepareFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

    	return new StepBuilder("bins006PrepareFile")    	
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.allowStartIfComplete(true)
				.<Bins006OutputDto, Bins006OutputDto> chunk(5)
				.reader(itemReader(null))
				.processor(itemProcessor(null))
				.writer(writer)
				.listener(stepExecutionListener())
				.build();
    }
    
    @Bean
    @StepScope
    public JdbcCursorItemReader<Bins006OutputDto> itemReader(@Value("#{jobParameters[companyCode]}") String companyCode) {
    	String query = companyCode != null ? SQL_QUERY.replace(":companyCode", companyCode) : SQL_QUERY;
    	return new JdbcCursorItemReaderBuilder<Bins006OutputDto>()
    			.dataSource(this.dataSource)
    			.name("bins006ItemReader")
    			.sql(query)
    			.rowMapper(new Bins006RowMapper())
    			.build();

    }

    boolean isError = Boolean.FALSE;
    
    @Bean
    @StepScope
    public ItemProcessor<Bins006OutputDto, Bins006OutputDto> itemProcessor(@Value("#{stepExecution}") StepExecution context) {
        return new ItemProcessor<Bins006OutputDto, Bins006OutputDto>() {

			@Override
			public Bins006OutputDto process(Bins006OutputDto item) throws Exception {
				validateRecord(context, item);
				return item;
			}
        };
    }
    
    protected void validateRecord(StepExecution context, Bins006OutputDto item) {
    	List<String> errorMsgList = new ArrayList<>();
    	String seqId = item.getSeqId();
    	if ("X".equals(item.getPartType())) {
    		errorMsgList.add(String.format(ConstantUtils.PARTNO_NOT_EXIST, seqId, item.getPartNo()));
    	} else {
    		errorMsgList.add(String.format(ConstantUtils.PARTNO_TYPE_IS_IMPORT, seqId, item.getPartNo()));
    	}
    	
    	String[] dataColumns = {item.getRecordId(), item.getSeqId(), item.getInvDate(), item.getInvNo(), 
    			item.getDocReference(), item.getDocType(), item.getCancelFlag(), item.getCustomerCode(),
    			item.getSapCode(), item.getMovementType(), item.getPartNo(), item.getPackingTimestamp(),
    			item.getPackingPlant(), item.getPackingRefLoc(), item.getPackingRefLocType(),
    			item.getPartQty(), item.getCurrency(), item.getDestName(), item.getDestCode(),
    			item.getCfCode(), item.getSeries(), item.getEtd(), item.getExchangeRate(), item.getFreight(), 
    			item.getInsurance(), item.getShipModuleNo(), item.getUnitPrice(),
    			item.getTotalAmount(), item.getSourceDate(), item.getExportCreditAmt(), item.getCommercialFlag(),
    			item.getOrderType(), item.getMeasurement(), item.getPartType(), item.getDsiTiNo(), item.getTransportMode()};
    	
    	int[] mandColumns;
    	if (!"1".equals(item.getOrderType())) {
    		mandColumns = MAND_NCS_D;
    	} else {
    		mandColumns = MAND_REG_D;
    	}
    	
    	for (int i = 0; i < mandColumns.length; i++) {
    		if (dataColumns[mandColumns[i]] == null || dataColumns[mandColumns[i]].trim().isBlank()) {
    			errorMsgList.add(String.format(ConstantUtils.MANDATORY_COLUMNS_NOT_AVAIL, seqId, COLUMN_NAMES[MAND_REG_D[i]]));
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
		String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(ConstantUtils.COMPANY_CODE), context.getJobExecution().getStartTime());
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
    
	@Bean("bins006ItemWriter")
    @StepScope
    public FlatFileItemWriter<Bins006OutputDto> itemWriter(@Value("#{stepExecution}") StepExecution context) {
    	SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
		if (sendBatchConfig != null) {
			Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(context.getJobParameters().getString(ConstantUtils.COMPANY_CODE))).findFirst();
			if (sendBatchFileConfigOptional.isPresent()) {
				SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
				StringBuilder filePath = new StringBuilder(sendBatchFileConfig.getOutputFolder()).append("/").append(sendBatchFileConfig.getFileName().replace("YYYYMMDDHHMISS", df.format(context.getJobExecution().getStartTime())));
				context.getJobExecution().getExecutionContext().put(ConstantUtils.FILEPATH, filePath.toString());
				return new FlatFileItemWriterBuilder<Bins006OutputDto>()
	    				.name("bins006ItemWriter")
	    				.resource(new FileSystemResource(filePath.toString()))
	    				.delimited()
	    				.delimiter(sendBatchConfig.getLineSeparator().getData())
	    				.names(COLUMN_NAMES)
	    				.headerCallback(headerCallback(context))
	    				.footerCallback(footerCallback(context))
	    				.build();
			}
		}
		return null;
    }

    @Bean
    @StepScope
    public FlatFileFooterCallback footerCallback(@Value("#{stepExecution}") StepExecution context) {
    	return new FlatFileFooterCallback() {
			
			@Override
			public void writeFooter(Writer writer) throws IOException {
				SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
				String separator = sendBatchConfig == null || sendBatchConfig.getLineSeparator().getTrailer() == null ? "#" : sendBatchConfig.getLineSeparator().getTrailer();
				StringBuilder sb = new StringBuilder(separator).append("T").append(separator).append(String.format("%07d", context.getWriteCount()+2)).append(separator);
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
					String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(ConstantUtils.COMPANY_CODE), context.getJobExecution().getStartTime());
					String separator = sendBatchConfig.getLineSeparator().getHeader() == null ? "#" : sendBatchConfig.getLineSeparator().getHeader();
					
					StringBuilder sb = new StringBuilder(separator).append("H").append(separator);
					sb.append("OEM").append(separator); //From system
					sb.append("FA0").append(separator); //To system
					sb.append(fileName).append(separator);	//fileName
					sb.append("OEM_DOC").append(separator); //interface identifier
					sb.append("00331").append(separator); //record length of data
	
					writer.write(sb.toString());
				}
			}
		};
	} 
    
    private SendBatchConfig getSendBatchConfig(ExecutionContext executionContext) {
    	return (SendBatchConfig) executionContext.get(GlobalConstants.IF_CONFIG);
    }
    
    private String getFileName(SendBatchConfig sendBatchConfig, String companyCode, Date startTime) {
    	String fileName = "";
    	Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(companyCode)).findFirst();
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