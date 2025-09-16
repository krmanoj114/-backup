package com.tpex.batchjob.bins011;

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
import com.tpex.util.ConstantUtils;
import com.tpex.util.ConstantUtils.Binf011;
import com.tpex.util.GlobalConstants;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class Bins011 {

	private static final String[] COLUMN_NAMES = { "recId", "invNo", "invDate", "aicoFlg", "invAmt", "grossWt",
			"partNo", "netWt", "noCases", "qty", "fob", "cfCd", "series", "pckMth", "coCd" };
	
	private static final String SQL_QUERY2 = "SELECT * FROM (\r\n"
			+ "SELECT \r\n"
			+ "'A' REC_ID,\r\n"
			+ "RPAD(INV_H.INV_NO, 10, ' ') AS INV_NO,\r\n"
			+ "DATE_FORMAT(INV_H.INV_DT, '%Y%m%d') AS INV_DT,\r\n"
			+ "SEP.AICO_FLG AS AICO_FLG,\r\n"
			+ "LPAD(SUBSTRING(REPLACE(REPLACE(FORMAT(SEP.INV_AMT, 2), ',', ''), '.', ''), 1, 12),12,0) AS INV_AMT,\r\n"
			+ "LPAD(REPLACE(REPLACE(FORMAT(SEP.GROSS_WT, 3), ',', ''), '.', ''), 9,0) AS GROSS_WT,\r\n"
			+ "'' AS PART_NO,\r\n"
			+ "LPAD(REPLACE(REPLACE(FORMAT(SEP.NET_WT, 3), ',' , ''), '.', ''),9, 0) AS NET_WT,\r\n"
			+ "LPAD(FORMAT(SEP.NO_CASES, 0), '7', '0') AS NO_CASES,\r\n"
			+ "''AS QTY,\r\n"
			+ "'' AS FOB,\r\n"
			+ "''AS CF_CD,\r\n"
			+ "'' AS SERIES,\r\n"
			+ "PRT.PCK_MTH AS PCK_MTH,\r\n"
			+ "'' AS CO_CD\r\n"
			+ "FROM TB_R_INV_INVOICE_H INV_H\r\n"
			+ "JOIN TB_R_INS_SEP_INV_D SEP ON INV_H.INV_NO = SEP.INV_NO\r\n"
			+ "JOIN (SELECT MAX(PCK_MTH) PCK_MTH \r\n"
			+ "FROM TB_R_INV_PART_D \r\n"
			+ "WHERE INV_NO = ':invoiceNo') PRT\r\n"
			+ "WHERE SEP.INV_NO = ':invoiceNo'\r\n"
			+ "UNION \r\n"
			+ "SELECT \r\n"
			+ "'B' REC_ID,\r\n"
			+ "RPAD(INP.INV_NO, 10, ' ') AS INV_NO,\r\n"
			+ "DATE_FORMAT(INP.INV_DT, '%Y%m%d') AS INV_DT,\r\n"
			+ "INP.COE_AICO_FLG,\r\n"
			+ "'' AS INV_AMT,\r\n"
			+ "'' AS GROSS_WT,\r\n"
			+ "LPAD(INP.PART_NO, 12, ' ') AS PART_NO,\r\n"
			+ "LPAD(REPLACE(REPLACE(FORMAT(INP.NET_WT, 5), ',', ''), '.' ,''), '10', '0') AS NET_WT,\r\n"
			+ "'' AS NO_CASES,\r\n"
			+ "LPAD(REPLACE(FORMAT(SUM(INP.UNIT_PER_BOX), 0),',', ''), '8', '0') AS QTY,\r\n"
			+ "LPAD(SUBSTRING(REPLACE(FORMAT(INP.PRC, 2), '.', ''), 2, 12), '12', '0') AS FOB,\r\n"
			+ "RPAD(INP.CF_CD, 4, ' ') AS CF_CD,\r\n"
			+ "RPAD(INP.SERIES, 20, ' ') AS SERIES,\r\n"
			+ "MAX(INP.PCK_MTH) AS PCK_MTH,\r\n"
			+ "(SELECT \r\n"
			+ "CASE \r\n"
			+ "WHEN PARA_VAL = 'Y' THEN RPAD(IFNULL(INP.CO_CD, '   '), 3, ' ')  \r\n"
			+ "WHEN PARA_VAL = 'N' THEN ''  \r\n"
			+ "ELSE ''\r\n"
			+ "END AS PARA_VAL\r\n"
			+ "FROM TB_M_PARAMETER \r\n"
			+ "WHERE PARA_CD = 'CNTRY_ORG') \r\n"
			+ "FROM TB_R_INV_PART_D INP \r\n"
			+ "JOIN TB_R_INS_SEP_INV_D SEP ON INP.INV_NO = SEP.INV_NO "
			+ "WHERE INP.INV_NO = ':invoiceNo'\r\n"
			+ "AND INP.COE_AICO_FLG = SEP.AICO_FLG\r\n"
			+ "GROUP BY INP.INV_NO,\r\n"
			+ "INP.INV_DT, \r\n"
			+ "INP.COE_AICO_FLG,\r\n"
			+ "INP.PART_NO,\r\n"
			+ "INP.CF_CD, \r\n"
			+ "INP.NET_WT,\r\n"
			+ "INP.PRC, \r\n"
			+ "INP.SERIES, \r\n"
			+ "INP.CO_CD) AS NAV\r\n"
			+ "ORDER BY\r\n"
			+ "AICO_FLG ,\r\n"
			+ "REC_ID,\r\n"
			+ "INV_NO,\r\n"
			+ "PART_NO;";
	

	SimpleDateFormat df = new SimpleDateFormat(ConstantUtils.YYYYMMDDHHMMSS);

	SimpleDateFormat dbDf = new SimpleDateFormat(GlobalConstants.DATETIME_FORMAT);

	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Bins011.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${bins011.configuration.file}")
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

	// @Scheduled(cron = "0 */1 * ? * *")
	@Async
	public void bins011JobRunTMT(JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
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
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference<SendBatchConfig>() {
			});
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
	@Bean(name = "bins011BatchContext")
	public TpexBatchContext<SendBatchConfig> prepareContext() {
		return new TpexBatchContext<>(getConfiguration(configFileName));
	}

	@Bean(name = "bins011ExecutionListener")
	public TpexSendJobExecutionListener<SendBatchConfig> executionListener() {
		return new TpexSendJobExecutionListener<> (prepareContext());
	}

	@Bean(name = "bins011Job")
	public Job job() {
		
    	return new JobBuilder("bins011Job")
    			.incrementer(new RunIdIncrementer())
    			.listener(executionListener())
    			.repository(jobRepository)
    			.start(prepareFile2(jobRepository, transactionManager)).on(ConstantUtils.FAILED).to(finish(jobRepository, transactionManager))
    			.from(prepareFile2(jobRepository, transactionManager))
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

	@Bean(name = "bins011Sftp")
	protected Step sftpFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("bins011Sftp").repository(jobRepository).transactionManager(transactionManager)
				.tasklet(new Tasklet() {

					@Override
					public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
							throws Exception {
						sftpUploadGateway.sendToSftp(new FileSystemResource(
								chunkContext.getStepContext().getJobExecutionContext().get(ConstantUtils.FILE_PATH).toString())
								.getFile());
						return RepeatStatus.FINISHED;
					}
				}).build();
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
	@ServiceActivator(inputChannel = "bins011SftpChannel")
	public MessageHandler handlerBins011() {

		SftpConfiguration sftpConfiguration = new SftpConfiguration();
		SendBatchConfig sendBatchConfig = getConfiguration(configFileName);
		if (sendBatchConfig != null) {
			SendBatchFTPConfig sendFTPConfig = sendBatchConfig.getFileInfo().getOutputFileNames().get(0).getFtp()
					.get(0);
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
		@Gateway(requestChannel = "bins011SftpChannel")
		void sendToSftp(File file);
	}

    @Bean(name = "bins011ArchiveFile")
    Tasklet archiveFile() {
        return new ArchiveFile();
    }
    
    @Bean(name = "binf011ArchiveFile")
    protected Step archiveFile(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("binf011ArchiveFile")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(archiveFile())
				.build();
    }
    
	@Bean(name = "bins011Finish")
	protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("bins011Finish").repository(jobRepository).transactionManager(transactionManager)
				.tasklet(finishProcess()).build();
	}

	@Bean(name = "bins011FinishProcess")
	Tasklet finishProcess() {
		return new FinishProcess();
	}

	FlatFileItemWriter<Bins011OutputDto> writer;

	@Autowired
	@Qualifier("bins011ItemWriter")
	public void setFlatFileItemWriter(FlatFileItemWriter<Bins011OutputDto> writer) {
		this.writer = writer;
	}

	@Bean(name = "bins011PrepareFile")
	protected Step prepareFile2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("bins011PrepareFile").repository(jobRepository).transactionManager(transactionManager)
				.allowStartIfComplete(true).<Bins011OutputDto, Bins011OutputDto>chunk(5).reader(itemReaderBins011(null, null))
				.processor(itemProcessorBins011(null)).writer(writer).listener(new Bins011StepExecutionListener()).build();
	}

	@Bean
	@StepScope
	public JdbcCursorItemReader<Bins011OutputDto> itemReaderBins011(
			@Value("#{jobParameters[companyCode]}") String companyCode,
			@Value("#{jobParameters[invoiceNo]} ") String invoiceNo) {
		String query = invoiceNo != null ? SQL_QUERY2.replace(":invoiceNo", invoiceNo.trim()) : SQL_QUERY2;
		return new JdbcCursorItemReaderBuilder<Bins011OutputDto>().dataSource(this.dataSource).name("bins011ItemReader")
				.sql(query).rowMapper(new Bins011RowMapper()).build();

	}

	@Bean
	@StepScope
	public ItemProcessor<Bins011OutputDto, Bins011OutputDto> itemProcessorBins011(
			@Value("#{stepExecution}") StepExecution context) {
		return new ItemProcessor<Bins011OutputDto, Bins011OutputDto>() {

			@Override
			public Bins011OutputDto process(Bins011OutputDto item) throws Exception {
				return item;
			}
		};
	}

	

	@Bean("bins011ItemWriter")
	@StepScope
	public FlatFileItemWriter<Bins011OutputDto> itemWriter1(@Value("#{stepExecution}") StepExecution context) {
		SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
		if (sendBatchConfig != null) {
			Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo()
					.getOutputFileNames().stream()
					.filter(m -> m.getCompany().equals(context.getJobParameters().getString(ConstantUtils.COMPANY_CODE))).findFirst();
			if (sendBatchFileConfigOptional.isPresent()) {
				SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
				StringBuilder filePath = new StringBuilder(sendBatchFileConfig.getOutputFolder()).append("/")
						.append(sendBatchFileConfig.getFileName().replace(ConstantUtils.YYYYMMDDHHMISS,
								df.format(context.getJobExecution().getStartTime())));
				context.getJobExecution().getExecutionContext().put(ConstantUtils.FILE_PATH, filePath.toString());
				return new FlatFileItemWriterBuilder<Bins011OutputDto>().name("bins011ItemWriter")
						.resource(new FileSystemResource(filePath.toString())).delimited()
						.delimiter(sendBatchConfig.getLineSeparator().getData()).names(COLUMN_NAMES)
						.headerCallback(headerCallbackBins011(context)).footerCallback(footerCallbackBins011(context)).build();
			}
		}
		return null;
	}

	@Bean
	@StepScope
	public FlatFileFooterCallback footerCallbackBins011(@Value("#{stepExecution}") StepExecution context) {
		return new FlatFileFooterCallback() {

			@Override
			public void writeFooter(Writer writer) throws IOException {
				SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
				String separator = sendBatchConfig == null || sendBatchConfig.getLineSeparator().getTrailer() == null
						? Binf011.GATEWAY_TAG
						: sendBatchConfig.getLineSeparator().getTrailer();
				StringBuilder sb = new StringBuilder(separator).append(Binf011.FOOT).append(separator)
						.append(String.format("%07d", context.getWriteCount() + 2)).append(separator);
				writer.write(sb.toString());
			}
		};
	}

	public FlatFileHeaderCallback headerCallbackBins011(@Value("#{stepExecution}") StepExecution context) {
		return new FlatFileHeaderCallback() {

			@Override
			public void writeHeader(Writer writer) throws IOException {
				SendBatchConfig sendBatchConfig = getSendBatchConfig(context.getJobExecution().getExecutionContext());
				if (sendBatchConfig != null) {
					String fileName = getFileName(sendBatchConfig, context.getJobParameters().getString(ConstantUtils.COMPANY_CODE),
							context.getJobExecution().getStartTime());
					String separator = sendBatchConfig.getLineSeparator().getHeader() == null ? Binf011.GATEWAY_TAG
							: sendBatchConfig.getLineSeparator().getHeader();

					StringBuilder sb = new StringBuilder(separator).append(Binf011.HEAD).append(separator);
					sb.append(ConstantUtils.OEM).append(separator); // From system
					sb.append(ConstantUtils.IXO).append(separator); // To system
					sb.append(fileName).append(separator); // fileName
					sb.append(ConstantUtils.DA90030).append(separator); // interface identifier
					sb.append(ConstantUtils.RECORD_LEN_OF_DATA_00063).append(separator); // record length of data

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
		Optional<SendBatchFileConfig> sendBatchFileConfigOptional = sendBatchConfig.getFileInfo().getOutputFileNames()
				.stream().filter(m -> m.getCompany().equals(companyCode)).findFirst();
		if (sendBatchFileConfigOptional.isPresent()) {
			SendBatchFileConfig sendBatchFileConfig = sendBatchFileConfigOptional.get();
			fileName = sendBatchFileConfig.getFileName().replace(Binf011.DATE_FORMAT, df.format(startTime));
		}
		return fileName;
	}


}
