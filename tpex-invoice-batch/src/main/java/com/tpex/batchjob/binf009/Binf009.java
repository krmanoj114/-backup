package com.tpex.batchjob.binf009;

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
import com.tpex.batchjob.binf009.chunks.Binf009LineProcessor;
import com.tpex.batchjob.binf009.chunks.Binf009LinesWriter;
import com.tpex.batchjob.binf009.configuration.model.Binf009Config;
import com.tpex.batchjob.binf009.tasklets.Binf009ConfigurationReader;
import com.tpex.batchjob.binf009.tasklets.Binf009DeleteStagingData;
import com.tpex.batchjob.binf009.tasklets.Binf009ProcessMain;
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
public class Binf009 {

	/**
	 * Instance of logger, to log information (if any).
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(Binf009.class);

	/**
	 * Reference to batch configuration file.
	 * 
	 */
	@Value("${binf009.configuration.file}")
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
	public Binf009Config getConfiguration(String configFileName) {
	
		logger.info("getConfiguration called for 2 :: {}", configFileName);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(new FileReader(new File(configFileName)), new TypeReference <Binf009Config> () {});
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
	@Bean(name="BINF009BatchContext")
	TpexBatchContext <Binf009Config> prepareContext() {
		logger.info("getConfiguration called for 1 :: {}", configFileName);
		return new TpexBatchContext <> (getConfiguration(configFileName));
	}
	
	@Bean(name = "BINF009ExecutionListener")
	TpexJobExecutionListener <Binf009Config> executionListener(){
		return new TpexJobExecutionListener <> (prepareContext());
	}
	
    @Bean(name = "BINF009Job")
    Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {	

    	return new JobBuilder("BINF009Job")
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
	
    
    @Bean(name = "BINF009ConfigurationReader")
    protected Step readerBatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF009ConfigurationReader")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(new Binf009ConfigurationReader())
    			.build();
    }
    
    
    @Bean(name = "BINF009GetDataFileToProcess")
    protected Step getDataFileToProcess(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF009GetDataFileToProcess")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(new InputFileNameResolver())
            .build();
    }
    
    @Bean(name = "BINF009DeleteStagingData")
    protected Step deleteStagingData(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF009DeleteStagingData")
   			.repository(jobRepository)
   			.transactionManager(transactionManager)
    		.tasklet(binf009DeleteStagingData())
            .build();
    }
    
    @Bean(name = "BINF009DeleteStagingDataAfterError")
    protected Step deleteAfterError(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    	return new StepBuilder("BINF009DeleteStagingDataAfterError")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
    			.tasklet(binf009DeleteStagingData())
    			.build();
    }

    @Bean(name = "BINF009ProcessFileToStagging")
    protected Step processFileToStagging(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF009ProcessFileToStagging")    	
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.<LineData, LineData> chunk(1)
				.reader(readFileDataBinf009())
				.processor(performFileValidationsBinf009())
				.writer(writeFileDataToStaggingBinf009())
				.build();
    }

    @Bean(name = "BINF009StaggingToMain")
    protected Step processStaggingToMain(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF009StaggingToMain")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
				.tasklet(binf009ProcessMain())
				.build();
    }    
    
    @Bean(name = "BINF009Finish")
    protected Step finish(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("BINF009Finish")
    			.repository(jobRepository)
    			.transactionManager(transactionManager)
                .tasklet(finishProcessBinf009())
				.build();
    }
    
    
    
    @Bean
    Tasklet binf009DeleteStagingData() {
    	return new Binf009DeleteStagingData();
    }
    
    @Bean
    Tasklet binf009ProcessMain() {
    	return new Binf009ProcessMain();
    }
    
    @Bean
    ItemReader <LineData> readFileDataBinf009() {
        return new FlatInterfaceFileLineReader();
    }

    @Bean
    ItemProcessor<LineData, LineData> performFileValidationsBinf009() {
        return new Binf009LineProcessor();
    }

    @Bean
    ItemWriter <LineData> writeFileDataToStaggingBinf009() {
        return new Binf009LinesWriter();
    }	

    @Bean
    Tasklet finishProcessBinf009() {
        return new FinishProcess();
    }	    
    
	@Scheduled(cron = "*/60 * * * * *") 
	public void run() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		JobParameters param = new JobParametersBuilder()
				.addString("JobID", String.valueOf(System.currentTimeMillis()))
				.addLong("startAt", System.currentTimeMillis())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, "Upload Shipping Result from GPAC")
				.addString(ConstantUtils.JOB_P_USER_ID, ConstantUtils.UPD_BY_SCHEDULE)
				.addString(ConstantUtils.JOB_P_BATCH_ID, ConstantUtils.BINF009)
				.toJobParameters();

		jobLauncher.run(job(jobRepository, transactionManager), param);
	}
	
}
