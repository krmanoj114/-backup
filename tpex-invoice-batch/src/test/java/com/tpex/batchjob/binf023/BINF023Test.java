package com.tpex.batchjob.binf023;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.sql.DataSource;

import com.tpex.batchjob.common.TpexSendJobExecutionListener;
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

import com.tpex.batchjob.common.configuration.model.SendBatchConfig;

@ExtendWith(MockitoExtension.class)
class BINF023Test {
	
	@InjectMocks
	private Binf023 binf023;
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
    FlatFileItemWriter binf023ItemWriter;

	private String sourceTest = "src/test/resources/BINF023_configuration.json";

	@BeforeEach
	void setVal() {
		ReflectionTestUtils.setField(binf023, "configFileName", sourceTest, String.class);

	}

	@Test
	void getConfigurationTest() {
		SendBatchConfig configuration = binf023.getConfiguration("configuration.json");
		assertNull(configuration);
	}

	@Test
	void executionListenerTest() {
		TpexSendJobExecutionListener<SendBatchConfig> executionListener = binf023.executionListener();
		assertNotNull(executionListener);
	}

	@Test
	void runTest() throws Exception {
		binf023.job();
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("mstrVanMonth", "202308");
		paramsBuilder.addString("cbCd","KTL");
		assertNotNull(paramsBuilder);
	}

}
