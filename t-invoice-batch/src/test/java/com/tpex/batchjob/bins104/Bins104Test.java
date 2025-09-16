package com.tpex.batchjob.bins104;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;


import com.tpex.batchjob.bins104.configuration.model.Bins104Config;
import com.tpex.batchjob.common.TpexJobExecutionListener;

@ExtendWith(MockitoExtension.class)
class Bins104Test {

	@InjectMocks
	private Bins104 bins104;
	@Mock
	private JobRepository jobRepository;
	@Mock
	private PlatformTransactionManager transactionManager;
	@Mock
	private JobLauncher jobLauncher;

	private String sourceTest = "src/test/resources/BINS104_configuration.json";

	@BeforeEach
	void setVal() {
		ReflectionTestUtils.setField(bins104, "configFileName", sourceTest, String.class);
	}

	@Test
	void getConfigurationTest() {
		Bins104Config configuration = bins104.getConfiguration("configuration.json");
		assertNull(configuration);
	}

	@Test
	void executionListenerTest() {
		TpexJobExecutionListener<Bins104Config> executionListener = bins104.executionListener();
		assertNotNull(executionListener);
	}

	@Test
	void runTest() throws Exception {
		bins104.run();
		verify(jobLauncher, times(1)).run(any(Job.class), any(JobParameters.class));
	}

}
