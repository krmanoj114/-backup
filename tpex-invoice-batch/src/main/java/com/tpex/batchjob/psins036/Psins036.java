package com.tpex.batchjob.psins036;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import com.tpex.batchjob.common.TpexBatchContext;
import com.tpex.batchjob.common.TpexSendJobExecutionListener;
import com.tpex.batchjob.common.configuration.model.SendBatchConfig;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class Psins036 {


	

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

	@Bean(name = "psins036BatchContext")
	public TpexBatchContext<SendBatchConfig> prepareContext() {
		return new TpexBatchContext<>();
	}

	@Bean(name = "psins036ExecutionListener")
	public TpexSendJobExecutionListener<SendBatchConfig> executionListener() {
		return new TpexSendJobExecutionListener<> (prepareContext());
	}
	
	@Autowired
	private JobLauncher jobLauncher;

	@Bean(name = "psins036Job")
	public Job job() {
		return new JobBuilder("psins036Job").repository(jobRepository).listener(executionListener())
				.start(processDataToDB(jobRepository, transactionManager))
				.build();
	}

	@Bean(name = "PSINS036DataToDB")
	protected Step processDataToDB(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("PSINS036DataToDB").repository(jobRepository).transactionManager(transactionManager)
				.tasklet(psins036ProcessMain()).build();
	}

	@Bean
	Tasklet psins036ProcessMain() {
		return new Psins036ProcessMain();
	}

	@Bean(name = "Psins036ConfigurationReader")
	protected Step readerBatchConfiguration(JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		return new StepBuilder("Psins036ConfigurationReader").repository(jobRepository)
				.transactionManager(transactionManager).tasklet(new Psins036ConfigurationReader()).build();
	}

	public void psins036JobRunTMT(JobParameters jobParameters) throws JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		jobLauncher.run(job(), jobParameters);
	}
}

