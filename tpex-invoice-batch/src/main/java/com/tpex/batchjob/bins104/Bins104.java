package com.tpex.batchjob.bins104;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.batchjob.bins104.chunks.Bins104LineProcessor;
import com.tpex.batchjob.bins104.chunks.Bins104LinesWriter;
import com.tpex.batchjob.bins104.configuration.model.Bins104Config;
import com.tpex.batchjob.bins104.tasklets.Bins104ConfigurationReader;
import com.tpex.batchjob.bins104.tasklets.Bins104DeleteStagingData;
import com.tpex.batchjob.bins104.tasklets.Bins104ProcessMain;
import com.tpex.batchjob.common.TpexBatchContext;
import com.tpex.batchjob.common.TpexJobExecutionListener;
import com.tpex.batchjob.common.configuration.model.LineData;
import com.tpex.batchjob.common.receive.FlatInterfaceFileLineReader;
import com.tpex.batchjob.common.receive.tasklets.FinishProcess;
import com.tpex.batchjob.common.receive.tasklets.InputFileNameResolver;
import com.tpex.util.ConstantUtils;
import com.tpex.util.GlobalConstants;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class Bins104 {
	
	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Bins104.class);
	
	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${bins104.configuration.file}")
	private String configFileName;
	
	/**
	 * JobRepository instance.
	 * 
	 */
	@Autowired
	private JobRepository jobRepository;
	
	/**
	 * PlatformTransactionManager instance.
	 * 
	 */
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	
	@Autowired
	private JobLauncher jobLauncher;
	
	/**
	 * read batch configuration data and set in job context.
	 * 
	 * @param configFileName
	 * @return
	 * @throws DatabindException
	 * @throws FileNotFoundException
	 * @throws StreamReadException
	 * @throws IOException
	 */
	public Bins104Config getConfiguration(String configFileName) {
	
		logger.info("getConfiguration called for 2 :: {}", configFileName);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference <Bins104Config> () {});
		} catch (DatabindException dbe) {
			logger.error("DatabindException exception occurred.");
		} catch (FileNotFoundException fnf) {
			logger.error("FileNotFoundException exception occurred.");
		} catch (StreamReadException sre) {
			logger.error("StreamReadException exception occurred.");
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
	@Bean(name="BINS104BatchContext")
	TpexBatchContext <Bins104Config> prepareContext() {
		logger.info("getConfiguration called for 1 :: {}", configFileName);
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}
	
	@Bean(name = "BINS104ExecutionListener")
	TpexJobExecutionListener <Bins104Config> executionListener(){
		return new TpexJobExecutionListener <> (prepareContext());
	}
	
	@Bean(name = "BINS104Job")
	Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		
		return new JobBuilder("BINS104Job")
				.repository(jobRepository)
    			.listener(executionListener())
    			.start(readerBatchConfiguration(jobRepository, transactionManager))
    			.next(getDataFileToProcess(jobRepository, transactionManager)).on(GlobalConstants.FILE_NOT_FOUND).to(finish(jobRepository, transactionManager))
    			.from(getDataFileToProcess(jobRepository, transactionManager))
    			.next(deleteStagingData(jobRepository, transactionManager))
    			.next(processFileToStagging(jobRepository, transactionManager)).on(GlobalConstants.DEL_TEMP).to(deleteAfterError(jobRepository, transactionManager))
    			.from(processFileToStagging(jobRepository, transactionManager))
    			.next(processStaggingToMain(jobRepository, transactionManager))
    			.build().build();
	}
	
	
	@Bean(name = "BINS104ConfigurationReader")
	protected Step readerBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINS104ConfigurationReader")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(new Bins104ConfigurationReader())
    			.build();
    }	
	
	
	@Bean(name = "BINS104GetDataFileToProcess")
    protected Step getDataFileToProcess(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINS104GetDataFileToProcess")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(new InputFileNameResolver())
            .build();
    }
	
	@Bean(name = "BINS104DeleteStagingData")
    protected Step deleteStagingData(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINS104DeleteStagingData")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(bins104DeleteStagingData())
            .build();
    }
	
	@Bean(name = "BINS104DeleteStagingDataAfterError")
    protected Step deleteAfterError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINS104DeleteStagingDataAfterError")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(bins104DeleteStagingData())
    			.build();
    }
	
	@Bean(name = "BINS104ProcessFileToStagging")
    protected Step processFileToStagging(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINS104ProcessFileToStagging")    	
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.<LineData, LineData> chunk(1)
				.reader(bins104ReadFileData())
				.processor(bins104PerformFileValidations())
				.writer(bins104WriteFileDataToStagging())
				.build();
    }
	
	@Bean(name = "BINS104StaggingToMain")
    protected Step processStaggingToMain(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINS104StaggingToMain")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.tasklet(bins104ProcessMain())
				.build();
    }
	
	@Bean(name = "BINS104Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINS104Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(bins104FinishProcess())
				.build();
    }
	
	
	
	@Bean
    Tasklet bins104DeleteStagingData() {
    	return new Bins104DeleteStagingData();
	}
	
	@Bean
    Tasklet bins104ProcessMain() {
    	return new Bins104ProcessMain();
    }
	
	@Bean
    ItemReader <LineData> bins104ReadFileData() {
        return new FlatInterfaceFileLineReader();
    }
	
	@Bean
    ItemProcessor<LineData, LineData> bins104PerformFileValidations() {
        return new Bins104LineProcessor();
    }
	
	@Bean
    ItemWriter <LineData> bins104WriteFileDataToStagging() {
        return new Bins104LinesWriter();
    }	

    @Bean
    Tasklet bins104FinishProcess() {
        return new FinishProcess();
    }
    
    @Scheduled(cron = "*/60 * * * * *") 
	public void run() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters param = new JobParametersBuilder()				
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, ConstantUtils.BINS104_UPLOAD_MESSAGE)
				.addString(ConstantUtils.JOB_P_USER_ID, ConstantUtils.UPD_BY_SCHEDULE)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINS104)
				.toJobParameters();

		jobLauncher.run(job(jobRepository, transactionManager), param);
	}
	
}
