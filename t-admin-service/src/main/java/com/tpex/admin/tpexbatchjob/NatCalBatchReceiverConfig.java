package com.tpex.admin.tpexbatchjob;

import com.tpex.admin.batchjob.config.GenericItemWriter;
import com.tpex.admin.entity.NatCalEntity;
import com.tpex.admin.repository.TpexConfigRepository;
import com.tpex.admin.util.TpexConfigurationUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpConnectTimeoutException;
import java.sql.SQLException;

/**
 * The Class NatCalBatchReceiverConfig.
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class NatCalBatchReceiverConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	JobCompletionNotificationListener jobCompletionNotificationListener;

	@SuppressWarnings("rawtypes")
	@Autowired
	PoiItemReader excelItemReader;

	@Autowired
	GenericItemWriter genericItemWriter;

	@Autowired
	TaskExecutor threadPoolExecutor;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	// Creates an instance of TempProcessor that converts one data form to another.
	// In our case the data form is maintained.

	/**
	 * National calendar master processor.
	 *
	 * @return the national calendar master processor
	 */
	@Bean
	public NationalCalendarMasterProcessor nationalCalendarMasterProcessor() {
		return new NationalCalendarMasterProcessor();

	}

	/**
	 * Temp excel batch step.
	 *
	 * @return the step
	 * @throws Exception the exception
	 */
	@SuppressWarnings("unchecked")

	@Transactional(propagation = Propagation.REQUIRED)
	public synchronized Step tempExcelBatchStep() {

		Boolean nameNotFound = false;
		boolean retry = tpexConfigRepository.findByName("batchJobRetry") != null
				? Boolean.valueOf(tpexConfigRepository.findByName("batchJobRetry").getValue())
				: nameNotFound;

		SimpleStepBuilder<NatCalEntity, NatCalEntity> builder = this.stepBuilderFactory.get("tempExcelBatchStep")
				.<NatCalEntity, NatCalEntity>chunk(366).reader(excelItemReader)
				.processor(nationalCalendarMasterProcessor()).writer(genericItemWriter.jpaWriter());

		if (retry) {
			builder.faultTolerant().retryLimit(3).retry(HttpConnectTimeoutException.class).retry(SQLException.class)
					.retry(DeadlockLoserDataAccessException.class);
		}

		return builder.build();
	}

	/**
	 * Job receiver.
	 *
	 * @return the job
	 * @throws Exception the exception
	 */
	// Executes the job, saving the data from .csv file into the database.
	@Bean
	@Qualifier(value = "natCaljobReceiver")
	@Transactional
	public Job jobReceiver() {

		return this.jobBuilderFactory.get("tempUpdateJob").incrementer(new RunIdIncrementer())
				.listener(this.jobCompletionNotificationListener).start(tempExcelBatchStep()).build();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Bean
	@Qualifier(value = "dummyJob")
	public Job dummyJob() {
		return this.jobBuilderFactory.get("dummyJob").incrementer(new RunIdIncrementer())
				.listener(this.jobCompletionNotificationListener).start(dummyStep()).build();
	}

	/**
	 * @return
	 */
	@Bean
	public Step dummyStep() {
		return this.stepBuilderFactory.get("dummyStep").tasklet(dummyTasklet()).build();
	}

	/**
	 * @return
	 */
	@Bean
	public DummyTasklet dummyTasklet() {
		return new DummyTasklet();
	}

}
