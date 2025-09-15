package com.tpex.batchjob.binf005;

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
import com.tpex.batchjob.binf005.chunks.Binf005LineProcessor;
import com.tpex.batchjob.binf005.chunks.Binf005LinesWriter;
import com.tpex.batchjob.binf005.configuration.model.Binf005Config;
import com.tpex.batchjob.binf005.tasklets.Binf005ConfigurationReader;
import com.tpex.batchjob.binf005.tasklets.Binf005DeleteStagingData;
import com.tpex.batchjob.binf005.tasklets.Binf005ProcessMain;
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
public class Binf005 {

	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Binf005.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${binf005.configuration.file}")
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
	public Binf005Config getConfiguration(String configFileName) {
	
		logger.info("getConfiguration called for 2 :: {}", configFileName);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference <Binf005Config> () {});
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
	@Bean(name="BINF005BatchContext")
	TpexBatchContext <Binf005Config> prepareContext() {
		logger.info("getConfiguration called for 1 :: {}", configFileName);
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}
	
	@Bean(name = "BINF005ExecutionListener")
	TpexJobExecutionListener <Binf005Config> executionListener(){
		return new TpexJobExecutionListener <> (prepareContext());
	}
	
    @Bean(name = "BINF005Job")
    Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {	

    	return new JobBuilder("BINF005Job")
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
	
    
    @Bean(name = "BINF005ConfigurationReader")
    protected Step readerBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF005ConfigurationReader")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(new Binf005ConfigurationReader())
    			.build();
    }
    
    
    @Bean(name = "BINF005GetDataFileToProcess")
    protected Step getDataFileToProcess(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF005GetDataFileToProcess")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(new InputFileNameResolver())
            .build();
    }
    
    @Bean(name = "BINF005DeleteStagingData")
    protected Step deleteStagingData(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF005DeleteStagingData")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(binf005DeleteStagingData())
            .build();
    }
    
    @Bean(name = "BINF005DeleteStagingDataAfterError")
    protected Step deleteAfterError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF005DeleteStagingDataAfterError")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(binf005DeleteStagingData())
    			.build();
    }

    @Bean(name = "BINF005ProcessFileToStagging")
    protected Step processFileToStagging(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF005ProcessFileToStagging")    	
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.<LineData, LineData> chunk(1)
				.reader(readFileData())
				.processor(performFileValidations())
				.writer(writeFileDataToStagging())
				.build();
    }

    @Bean(name = "BINF005StaggingToMain")
    protected Step processStaggingToMain(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF005StaggingToMain")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.tasklet(binf005ProcessMain())
				.build();
    }    
    
    @Bean(name = "BINF005Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF005Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(finishProcess())
				.build();
    }
    
    
    
    @Bean
    Tasklet binf005DeleteStagingData() {
    	return new Binf005DeleteStagingData();
    }
    
    @Bean
    Tasklet binf005ProcessMain() {
    	return new Binf005ProcessMain();
    }
    
    @Bean
    ItemReader <LineData> readFileData() {
        return new FlatInterfaceFileLineReader();
    }

    @Bean
    ItemProcessor<LineData, LineData> performFileValidations() {
        return new Binf005LineProcessor();
    }

    @Bean
    ItemWriter <LineData> writeFileDataToStagging() {
        return new Binf005LinesWriter();
    }	

    @Bean
    Tasklet finishProcess() {
        return new FinishProcess();
    }	    
    
	@Scheduled(cron = "*/5 * * * * *") 
	public void run() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters param = new JobParametersBuilder()
				.addLong(ConstantUtils.JOB_P_START_AT, System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, "Upload Shipping Result from GPAC")
				.addString(ConstantUtils.JOB_P_USER_ID, ConstantUtils.UPD_BY_SCHEDULE)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINF005)
				.toJobParameters();

		jobLauncher.run(job(jobRepository, transactionManager), param);
	}
	
}
