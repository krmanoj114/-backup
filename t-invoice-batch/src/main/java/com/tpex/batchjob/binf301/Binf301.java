package com.tpex.batchjob.binf301;

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
import com.tpex.batchjob.binf301.chunks.Binf301LineProcessor;
import com.tpex.batchjob.binf301.chunks.Binf301LinesWriter;
import com.tpex.batchjob.binf301.configuration.model.Binf301Config;
import com.tpex.batchjob.binf301.tasklets.Binf301ConfigurationReader;
import com.tpex.batchjob.binf301.tasklets.Binf301DeleteStagingData;
import com.tpex.batchjob.binf301.tasklets.Binf301ProcessMain;
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
public class Binf301 {

	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Binf301.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${binf301.configuration.file}")
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
	public Binf301Config getConfiguration(String configFileName) {
	
		logger.info("getConfiguration called for 2 :: {}", configFileName);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference <Binf301Config> () {});
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
	@Bean(name="Binf301BatchContext")
	TpexBatchContext <Binf301Config> prepareContext() {
		logger.info("getConfiguration called for 1 :: {}", configFileName);
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}
	
	@Bean(name = "Binf301ExecutionListener")
	TpexJobExecutionListener <Binf301Config> executionListener(){
		return new TpexJobExecutionListener <> (prepareContext());
	}
	
    @Bean(name = "Binf301Job")
    Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {	

    	return new JobBuilder("Binf301Job")
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
	
    
    @Bean(name = "Binf301ConfigurationReader")
    protected Step readerBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("Binf301ConfigurationReader")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(new Binf301ConfigurationReader())
    			.build();
    }
    
    
    @Bean(name = "Binf301GetDataFileToProcess")
    protected Step getDataFileToProcess(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("Binf301GetDataFileToProcess")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(new InputFileNameResolver())
            .build();
    }
    
    @Bean(name = "Binf301DeleteStagingData")
    protected Step deleteStagingData(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("Binf301DeleteStagingData")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(binf301DeleteStagingData())
            .build();
    }
    
    @Bean(name = "Binf301DeleteStagingDataAfterError")
    protected Step deleteAfterError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("Binf301DeleteStagingDataAfterError")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(binf301DeleteStagingData())
    			.build();
    }

    @Bean(name = "Binf301ProcessFileToStagging")
    protected Step processFileToStagging(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("Binf301ProcessFileToStagging")    	
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.<LineData, LineData> chunk(1)
				.reader(readFileDataBinf301())
				.processor(performFileValidationsBinf301())
				.writer(writeFileDataToStaggingBinf301())
				.build();
    }

    @Bean(name = "Binf301StaggingToMain")
    protected Step processStaggingToMain(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("Binf301StaggingToMain")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.tasklet(binf301ProcessMain())
				.build();
    }    
    
    @Bean(name = "Binf301Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("Binf301Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(finishProcessBinf301())
				.build();
    }
    
    
    
    @Bean
    Tasklet binf301DeleteStagingData() {
    	return new Binf301DeleteStagingData();
    }
    
    @Bean
    Tasklet binf301ProcessMain() {
    	return new Binf301ProcessMain();
    }
    
    @Bean
    ItemReader <LineData> readFileDataBinf301() {
        return new FlatInterfaceFileLineReader();
    }

    @Bean
    ItemProcessor<LineData, LineData> performFileValidationsBinf301() {
        return new Binf301LineProcessor();
    }

    @Bean
    ItemWriter <LineData> writeFileDataToStaggingBinf301() {
        return new Binf301LinesWriter();
    }	

    @Bean
    Tasklet finishProcessBinf301() {
        return new FinishProcess();
    }	    
    
	@Scheduled(cron = "*/60 * * * * *") 
	public void run() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters param = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.addLong("startAt", System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, "Receive Engine Part from UHOST")
				.addString(ConstantUtils.JOB_P_USER_ID, ConstantUtils.UPD_BY_SCHEDULE)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINF301)
				.toJobParameters();

		jobLauncher.run(job(jobRepository, transactionManager), param);
	}
	
}
