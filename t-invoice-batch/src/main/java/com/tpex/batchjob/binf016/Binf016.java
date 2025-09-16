package com.tpex.batchjob.binf016;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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
import org.springframework.scheduling.annotation.Async;
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
import com.tpex.util.GlobalConstants;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class Binf016 {

	private static final String[] COLUMN_NAMES_BINF016 = { "recordId", "invNo", "invDate", "vesselNameOcean", "buyerCode",
			"consigneeCode", "tradingTermCode", "currencyCode", "invoiceAmount", "freightAmount", "insuranceAmount",
			"shippingMark1", "shippingMark2", "shippingMark3", "shippingMark4", "shippingMark5", "shippingMark6",
			"shippingMark7", "shippingMark8", "finalDestination", "paymentTerm", "finalETD", "finalETA", "measurement",
			"grossWeight", "netWeight", "orderType", "paymentDueDate", "totalNoOfCase", "transportMode", "productName",
			"nonCommercialFlag", "invoicePrivilegeType", "packingMonth", "portName", "voyageNoOcean",
			"goodsDescription", "vesselNameFeeder", "voyageNoFeeder", "lotPxP", "shippingCompanyOcean",
			"transactionType" };
	
	private static final String SQL_QUERY_BINF016_1 =  "SELECT t1.* FROM "
			+ "  ((SELECT "
			+ "      'A' REC_ID, A.INV_NO, "
			+ "      DATE_FORMAT(A.INV_DT, '%Y%m%d') AS INV_DT, "
			+ "      RPAD(IFNULL(TRIM(A.VESSEL_NAME_OCEAN), 'AIR'), 50, ' ') AS VESSEL_NM_OCEAN, "
			+ "      RPAD(A.CUST, 5, ' ') AS IND_BUYER, "
			+ "      RPAD(A.BUYER, 5, ' ') AS IND_CONSIGNEE, "
			+ "      RPAD(A.TRADE_TRM, 3, ' ') AS TRADE_TRM, "
			+ "      RPAD(A.PAY_CRNCY, 3, ' ') AS PAY_CRNCY, "
			+ "      SUBSTRING(LPAD((A.INV_AMT * 100), 13, '0'), 2, 12) AS INV_AMT, "
			+ "      LPAD((IFNULL(A.FREIGHT, 0) * 100), 12, '0') AS FREIGHT, "
			+ "      LPAD((IFNULL(A.INSURANCE, 0) * 100), 12, '0') AS INSURANCE, "
			+ "      RPAD(SUBSTRING(A.MARK1, 1, 40), 40, ' ') AS MARK1, "
			+ "      RPAD(SUBSTRING(A.MARK2, 1, 40), 40, ' ') AS MARK2, "
			+ "      RPAD(SUBSTRING(A.MARK3, 1, 40), 40, ' ') AS MARK3, "
			+ "      RPAD(IFNULL(A.MARK4, ' '), 300, ' ') AS MARK4, "
			+ "      RPAD(IFNULL(SUBSTRING(A.MARK5, 1, 300), ' '), 300, ' ') AS MARK5, "
			+ "      RPAD(IFNULL(A.MARK6, ' '), 300, ' ') AS MARK6, "
			+ "      RPAD(SUBSTRING(IFNULL(A.MARK7, ' '), 1, 40), 40, ' ') AS MARK7, "
			+ "      RPAD(SUBSTRING(IFNULL(A.MARK8, ' '), 1, 40), 40, ' ') AS MARK8, "
			+ "      RPAD(A.FINAL_DST, 4, ' ') AS FINAL_DST, "
			+ "      RPAD(A.PAY_TERM, 3, ' ') AS PAY_TERM, "
			+ "      DATE_FORMAT(A.ETD, '%Y%m%d') AS ETD, "
			+ "      DATE_FORMAT(A.ETA, '%Y%m%d') AS ETA, "
			+ "      LPAD((A.MEASUREMENT * 1000), 8, '0') AS MEASUREMENT, "
			+ "      LPAD((A.GROSS_WT * 1000), 9, '0') AS GROSS_WT, "
			+ "      LPAD((A.NET_WT * 1000), 9, '0') AS NET_WT, "
			+ "      CASE A.ORD_TYP "
			+ "          WHEN '1' THEN '2' "
			+ "          WHEN '3' THEN '4' "
			+ "          WHEN '5' THEN '6' "
			+ "      END AS ORD_TYP, "
			+ "      RPAD(IFNULL(DATE_FORMAT(A.DUE_DT, '%Y%m%d'), ' '), 8, ' ') AS DUE_DT, "
			+ "      LPAD(A.NO_OF_CASES, 7, '0') AS NO_OF_CASES, "
			+ "      D.LOCAL_TPT_CD, "
			+ "      RPAD(B.PROD_GRP_DESC, 40, ' ') AS PROD_GRP_DESC, "
			+ "      CASE A.ORD_TYP "
			+ "          WHEN 'R' THEN 'N' "
			+ "          ELSE IFNULL(A.NONCOM_FLG, 'N') "
			+ "      END AS NON_COMM_FLAG, "
			+ "      A.INV_TYP AS INV_PRIVILEGE, "
			+ "      A.PKG_MTH, "
			+ "      RPAD(IFNULL(C.NAME, ' '), 50, ' ') AS DST_PORT_NM, "
			+ "      RPAD(IFNULL(TRIM(A.VOYAGE_NO_OCEAN), ' '), 10, ' ') AS VOYAGE_NO_OCEAN, "
			+ "      CONCAT( "
			+ "          RPAD(IFNULL(A.GOODS_DESC1, ' '), 30, ' '), "
			+ "          RPAD(IFNULL(A.GOODS_DESC2, ' '), 30, ' '), "
			+ "          RPAD(IFNULL(A.GOODS_DESC3, ' '), 30, ' '), "
			+ "          RPAD(IFNULL(A.GOODS_DESC4, ' '), 30, ' '), "
			+ "          RPAD(IFNULL(A.GOODS_DESC5, ' '), 30, ' '), "
			+ "          RPAD(IFNULL(A.GOODS_DESC6, ' '), 30, ' ') "
			+ "      ) AS GOODS_DESC, "
			+ "      RPAD(IFNULL(TRIM(A.VESSEL_NAME_FEEDER), ' '), 50, ' ') AS VESSEL_NM_FEEDER, "
			+ "      RPAD(IFNULL(TRIM(A.VOYAGE_NO_FEEDER), ' '), 10, ' ') AS VOYAGE_NO_FEEDER, "
			+ "      A.LOT_PTTRN, "
			+ "      RPAD(IFNULL(A.SHIPPING_COMP, ' '), 4, ' ') AS SHIPPING_COMP, "
			+ "      CASE A.CANCEL_FLG "
			+ "          WHEN 'Y' THEN 'C' "
			+ "          ELSE CASE WHEN A.IXOS_SEND_DT IS NULL THEN 'N' ELSE 'R' END "
			+ "      END AS TRN_TYP "
			+ "  FROM "
			+ "      TB_R_INV_INVOICE_H A "
			+ "      JOIN TB_M_PROD_GRP B ON A.PROD_GRP_CD = B.PROD_GRP_CD "
			+ "      JOIN TB_M_PORT C ON A.DEST_PORT_CD = C.CD "
			+ "      JOIN TB_M_TPT D ON A.TRNSPRT_CD = D.TPT_CD "
			+ "  WHERE "
			+ "      A.IXOS_FLG = 'N' "
			+ "      AND A.PRINT_FLG = 'Y' "
			+ "  ORDER BY "
			+ "      A.CANCEL_FLG DESC, "
			+ "      A.INV_NO) "
			+ "    UNION  "
			+ "     (SELECT   "
			+ "      'B' REC_ID, INV_NO, DATE_FORMAT(INV_DT, '%Y%m%d'),  "
			+ "      CONCAT(CONT_DST, CONT_GRP_CD, RPAD(CONT_SNO, 9, ' '),  "
			+ "     VAN_PLNT_CD, RPAD(ISO_CONT_NO, 11, ' '), RPAD(SEAL_NO, 12, ' '), CONT_TYP, "
			+ "      CONT_SIZE, LPAD(CONT_NET_WT*1000, 8, '0'), LPAD(CONT_TARE_WT, 5, '0'),  "
			+ "     LPAD(CONT_GROSS_WT*1000, 8, '0'), LPAD(NO_OF_MOD, 3, '0'),  "
			+ "     LPAD(SUM_MOD_GROSS*1000, 8, '0'), "
			+ "      DATE_FORMAT(PLN_VAN_DT, '%Y%m%d'),  "
			+ "     DATE_FORMAT(ACT_VAN_DT, '%Y%m%d'),  "
			+ "     RPAD(BOOK_NO, 15, ' ')) as Result, '','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','' "
			+ "  FROM  "
			+ "      TB_R_INV_CONTAINER_D "
			+ "  ORDER BY  "
			+ "      CONT_SNO     "
			+ "      ) "
			+ "      UNION "
			+ "      (SELECT 'C' REC_ID, "
			+ "      A.INV_NO, DATE_FORMAT(A.INV_DT, '%Y%m%d'), "
			+ "      CONCAT( "
			+ "          A.CF_CD, "
			+ "          RPAD(SERIES, 20, ' '), "
			+ "          CASE SUBSTRING(A.LOT_NO, 1, 2) "
			+ "              WHEN '**' THEN '  ' "
			+ "              ELSE SUBSTRING(A.LOT_NO, 1, 2) "
			+ "          END, "
			+ "          LPAD((CASE SUBSTRING(A.LOT_NO, 1, 2) WHEN '**' THEN 0 ELSE LOT_NO_PRC * 100 END), 12, '0'), "
			+ "          CASE SUBSTRING(A.LOT_NO, 1, 2) "
			+ "              WHEN '**' THEN '      ' "
			+ "              ELSE A.LOT_NO "
			+ "          END, "
			+ "          RPAD(A.MOD_NO, 6, ' '), "
			+ "          LPAD((A.NET_WT * 1000), 8, '0'), "
			+ "          LPAD((A.GROSS_WT * 1000), 8, '0'), "
			+ "          LPAD((A.TOT_M3 * 1000), 8, '0'), "
			+ "          A.PCK_MTH, "
			+ "          A.AICO_FLG, "
			+ "          LPAD((CASE SUBSTRING(A.LOT_NO, 1, 2) WHEN '**' THEN 0 ELSE LOT_SIZE END), 5, '0') "
			+ "      ) AS CONCATENATED_DATA,'','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','' "
			+ "  FROM TB_R_INV_MODULE_D A "
			+ "  JOIN ( "
			+ "      SELECT "
			+ "          B.INV_NO, "
			+ "          B.LOT_NO, "
			+ "          MAX(PCK_MTH) AS PCK_MTH, "
			+ "          MAX(B.LOT_SIZE) AS LOT_SIZE, "
			+ "          SERIES, "
			+ "          SUM(B.PRC * B.UNIT_PER_BOX) AS LOT_NO_PRC "
			+ "      FROM TB_R_INV_PART_D B "
			+ "      GROUP BY B.INV_NO, B.LOT_NO, SERIES "
			+ "  ) PARTS_DTL ON A.INV_NO = PARTS_DTL.INV_NO "
			+ "             AND A.LOT_NO = PARTS_DTL.LOT_NO "
			+ " ORDER BY A.LOT_NO, A.MOD_NO) "
			+ " UNION "
			+ " (SELECT 'D' REC_ID, "
			+ "     INV_NO, DATE_FORMAT(INV_DT, '%Y%m%d'), "
			+ "     RPAD(SUBSTR(PART_NO, 1, 12), 12, ' ') , "
			+ "     LPAD(NET_WT * 100000, 10, '0') , "
			+ "     LPAD(SUM(UNIT_PER_BOX), 8, '0') , "
			+ "     SUBSTRING(LPAD(PRC * 100, 13, '0'), 2, 12) , "
			+ "     RPAD(CF_CD, 4, ' ') , "
			+ "     RPAD(SERIES, 20, ' ') ,     "
			+ "     RPAD(PART_NM, 40, ' ') , "
			+ "     MAX(PCK_MTH) , "
			+ "     AICO_FLG, "
			+ "     CASE DG_FLG "
			+ "         WHEN NULL THEN 'N' "
			+ "         WHEN '0' THEN 'N' "
			+ "         WHEN '1' THEN 'Y' "
			+ "     END , "
			+ "     RPAD(IFNULL(CO_CD, '   '), 3, ' ') , "
			+ "     RPAD(IFNULL(HS_CD, ' '), 15, ' ') , "
			+ "     RPAD(IFNULL(ORG_CRITERIA, ' '), 17, ' ') ,'','','','','','','','','','','','','','','','','','','','','','','','','','' "
			+ " FROM TB_R_INV_PART_D "
			+ " GROUP BY "
			+ "     INV_DT, "
			+ "     INV_NO, "
			+ "     RPAD(CF_CD, 4, ' '), "
			+ "     RPAD(SERIES, 20, ' '), "
			+ "     RPAD(SUBSTR(PART_NO, 1, 12), 12, ' '), "
			+ "     LPAD(NET_WT * 100000, 10, '0'), "
			+ "     SUBSTRING(LPAD(PRC * 100, 13, '0'), 2, 12), "
			+ "     RPAD(PART_NM, 40, ' '), "
			+ "     AICO_FLG, "
			+ "     DG_FLG, "
			+ "     CO_CD, "
			+ "     RPAD(IFNULL(HS_CD, ' '), 15, ' '), "
			+ "     RPAD(IFNULL(ORG_CRITERIA, ' '), 17, ' ') "
			+ " ORDER BY "
			+ "     RPAD(SUBSTR(PART_NO, 1, 12), 12, ' '))) AS t1  "
			+ "     inner join  "
			+ "     (SELECT A.INV_NO AS INV_NO FROM TB_R_INV_INVOICE_H A "
			+ "     JOIN TB_M_PROD_GRP B ON A.PROD_GRP_CD = B.PROD_GRP_CD "
			+ "     JOIN TB_M_PORT C ON A.DEST_PORT_CD = C.CD "
			+ "     JOIN TB_M_TPT D ON A.TRNSPRT_CD = D.TPT_CD "
			+ " WHERE "
			+ "     A.IXOS_FLG = 'N' "
			+ "     AND A.PRINT_FLG = 'Y' "
			+ "     AND A.HAISEN_NO = ifnull(:haisenNo, A.HAISEN_NO) "
			+ "     AND A.HAISEN_YR_MTH = ifnull(:haisenYearMonth, A.HAISEN_YR_MTH) "
			+ "     AND A.INV_NO = ifnull(:invNo, A.INV_NO) "
			+ " ORDER BY "
			+ "     A.CANCEL_FLG DESC, "
			+ "     A.INV_NO) AS t2 on t2.INV_NO = t1.INV_NO; ";
		    

	
	private static final String COMPANY_CODE = "companyCode";	
	
	private static final String FILE_PATH = "filePath";
	
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
	SimpleDateFormat dbDf = new SimpleDateFormat(GlobalConstants.DATETIME_FORMAT);
	
	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Binf016.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${binf016.configuration.file}")
	private String configFileName;
	
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
	
	@Async
	public void binf016JobRunTMT(JobParameters jobParameters) throws JobExecutionAlreadyRunningException, 
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

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
	@Bean(name="binf016BatchContext")
	public TpexBatchContext <SendBatchConfig> prepareContext() {
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}	
	
	@Bean(name = "binf016ExecutionListener")
	public TpexSendJobExecutionListener<SendBatchConfig> executionListener(){
		return new TpexSendJobExecutionListener<> (prepareContext());
	}
	
	private ValidateRecordsTasklet validateRecordsTasklet;
    @Autowired
	public void setValidateRecordsTasklet(ValidateRecordsTasklet validateRecordsTasklet) {
	    this.validateRecordsTasklet = validateRecordsTasklet;
	}
    
    @Bean
	protected Step validateRecords(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("binf016Validate")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(validateRecordsTasklet)
    			.build();
	}
	
	@Bean(name = "binf016Job")
    public Job job() {

    	return new JobBuilder("binf016Job")
    			.incrementer(new RunIdIncrementer())
    			.listener(executionListener())
    			.repository(jobRepository)
    			.start(validateRecords(jobRepository, transactionManager)).on("FAILED").to(finish(jobRepository, transactionManager))
    			.from(validateRecords(jobRepository, transactionManager))
    			.next(prepareFileBinf016(jobRepository, transactionManager))
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
    
    @Bean(name = "binf016Sftp")
    protected Step sftpFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("binf016Sftp")
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
    @ServiceActivator(inputChannel = "binf016SftpChannel")
    public MessageHandler handlerBinf016() {
		
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
    	@Gateway(requestChannel = "binf016SftpChannel")
        void sendToSftp(File file);
    }
    
	
	@Bean(name = "bins016ArchiveFile")
	Tasklet archiveFile() {
		return new ArchiveFile();
	}

	@Bean(name = "binf016ArchiveFile")
	protected Step archiveFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("binf016ArchiveFile").repository(jobRepository).transactionManager(transactionManager)
				.tasklet(archiveFile()).build();
	}
	 
    
    @Bean(name = "binf016Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("binf016Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(finishProcess())
				.build();
    }
    
    @Bean(name = "binf016FinishProcess")
    Tasklet finishProcess() {
        return new FinishProcess();
    }
    
    FlatFileItemWriter<Binf016OutputDto> writer;
	@Autowired
	@Qualifier("binf016ItemWriter")
	public void setFlatFileItemWriter(FlatFileItemWriter<Binf016OutputDto> writer) {
	    this.writer = writer;
	}
    
	@Bean(name = "binf016StepItemWriteListener")
	public Binf016StepItemWriteListener binf016StepItemWriteListener(){
		return new Binf016StepItemWriteListener();
	}
	
	 @Bean(name = "binf016PrepareFile")
	    protected Step prepareFileBinf016(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

	    	return new StepBuilder("binf016PrepareFile")    	
	    			.repository(jobRepository)
	    			.transactionManager(transactionManager)
	    			.allowStartIfComplete(true)
					.<Binf016OutputDto, Binf016OutputDto> chunk(5)
					.reader(itemReaderBinf016(null,null, null, null))
					.processor(itemProcessorBinf016(null))
					.writer(writer)
					.listener(binf016StepItemWriteListener())
					.build();
	    }
	 
	 	@Bean
	    @StepScope
	    public JdbcCursorItemReader<Binf016OutputDto> itemReaderBinf016(@Value("#{jobParameters[companyCode]}") String companyCode, 
	    		@Value("#{jobParameters[invNum]} ") String invNum, @Value("#{jobParameters[haisenNo]} ") String haisenNo,
	    		@Value("#{jobParameters[haisenYear]} ") String haisenYear) {
	    	String query = SQL_QUERY_BINF016_1.replace(":invNo", invNum == null || invNum.isBlank() ? "null" : "'" + invNum.trim() + "'")
	    			.replace(":haisenNo", haisenNo == null || haisenNo.isBlank() ? "null" : "'" + haisenNo.trim() + "'")
	    			.replace(":haisenYearMonth", haisenYear == null || haisenYear.isBlank() ? "null" : "'" + haisenYear.trim() + "'");
	    	return new JdbcCursorItemReaderBuilder<Binf016OutputDto>()
	    			.dataSource(this.dataSource)
	    			.name("binf016ItemReader")
	    			.sql(query)
	    			.rowMapper(new Binf016RowMapper())
	    			.build();

	    }
	 	
	    @Bean
	    @StepScope
	    public ItemProcessor<Binf016OutputDto, Binf016OutputDto> itemProcessorBinf016(@Value("#{stepExecution}") StepExecution context) {
	        return new ItemProcessor<Binf016OutputDto, Binf016OutputDto>() {

				@Override
				public Binf016OutputDto process(Binf016OutputDto item) throws Exception {
					return item;
				}
	        };
	    }
	    
	    @Bean("binf016ItemWriter")
	    @StepScope
	    public FlatFileItemWriter<Binf016OutputDto> itemWriterBinf016(@Value("#{stepExecution}") StepExecution context) {
	    	SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
			if (sendBatchConfig != null) {
				Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames().stream().filter(m -> m.getCompany().equals(context.getJobParameters().getString(COMPANY_CODE))).findFirst();
				if (sendBatchFileConfigOptional.isPresent()) {
					SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
					StringBuilder filePath = new StringBuilder(sendBatchFileConfig.getOutputFolder()).append("/").append(sendBatchFileConfig.getFileName().replace("YYYYMMDDHHMISS", df.format(context.getJobExecution().getStartTime())));
					context.getJobExecution().getExecutionContext().put(FILE_PATH, filePath.toString());
					return new FlatFileItemWriterBuilder<Binf016OutputDto>()
		    				.name("binf016ItemWriter")
		    				.resource(new FileSystemResource(filePath.toString()))
		    				.delimited()
		    				.delimiter(sendBatchConfig.getLineSeparator().getData())
		    				.names(COLUMN_NAMES_BINF016)
		    				.headerCallback(headerCallbackBinf016(context))
		    				.footerCallback(footerCallbackBinf016(context))
		    				.build();
				}
			}
			return null;
	    }
	    
	    @Bean
	    @StepScope
	    public FlatFileFooterCallback footerCallbackBinf016(@Value("#{stepExecution}") StepExecution context) {
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
	    
	    public FlatFileHeaderCallback headerCallbackBinf016(@Value("#{stepExecution}") StepExecution context) {
			return new FlatFileHeaderCallback() {
				
				@Override
				public void writeHeader(Writer writer) throws IOException {
					SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
					if (sendBatchConfig != null) {
						String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(COMPANY_CODE), context.getJobExecution().getStartTime());
						String separator = sendBatchConfig.getLineSeparator().getHeader() == null ? "#" : sendBatchConfig.getLineSeparator().getHeader();
						
						StringBuilder sb = new StringBuilder(separator).append("H").append(separator);
						sb.append("OEM").append(separator); //From system
						sb.append("IXO").append(separator); //To system
						sb.append(fileName).append(separator);	//fileName
						sb.append("DA90020").append(separator); //interface identifier
						sb.append("01641").append(separator); //record length of data
		
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
	    
}
