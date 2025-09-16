package com.tpex.batchjob.bins011;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import com.tpex.batchjob.common.TpexSendJobExecutionListener;
import com.tpex.batchjob.common.configuration.model.SendBatchConfig;

@ExtendWith(MockitoExtension.class)
class BINF011Test {
	
	@InjectMocks
	private Bins011 bins011;
	@Mock
	private JobRepository jobRepository;
	@Mock
	private PlatformTransactionManager transactionManager;
	@Mock
	private JobLauncher jobLauncher;
	
	@Mock
	private DataSource dataSource;
	
	@Mock
    StepContext stepContext;

    @Mock
    StepExecution stepExecution;

    @Mock
    JobExecution jobExecution;

    @Mock
    ExecutionContext executionContext;
    
    @Mock
    FlatFileItemWriter bins011ItemWriter;

	private String sourceTest = "src/test/resources/BINS011_configuration.json";

	@BeforeEach
	void setVal() {
		ReflectionTestUtils.setField(bins011, "configFileName", sourceTest, String.class);

	}

	@Test
	void getConfigurationTest() {
		SendBatchConfig configuration = bins011.getConfiguration("configuration.json");
		assertNull(configuration);
	}

	@Test
	void executionListenerTest() {
		TpexSendJobExecutionListener<SendBatchConfig> executionListener = bins011.executionListener();
		assertNotNull(executionListener);
	}

	@Test
	void runTest() throws Exception {
		bins011.job();
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("invNum", "K123456789");
		assertNotNull(paramsBuilder);
	}

}
