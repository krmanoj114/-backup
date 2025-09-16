package com.tpex.admin.batchjob.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.tpex.admin.repository.TpexConfigRepository;
import com.tpex.admin.util.ConstantUtils;

@Component
public class GenericTaskExecutor {

	@Autowired
	TpexConfigRepository  tpexConfigRepository;
	
	@Bean
	@Qualifier(value = "threadPoolExecutor")
	public TaskExecutor threadPoolExecutor(){
    	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    	executor.setCorePoolSize(Integer.parseInt(tpexConfigRepository.findByName(ConstantUtils.BATCH_JOB_SIZE).getValue()));
        executor.setMaxPoolSize(Integer.parseInt(tpexConfigRepository.findByName(ConstantUtils.BATCH_JOB_SIZE).getValue()));
        executor.setQueueCapacity(Integer.parseInt(tpexConfigRepository.findByName(ConstantUtils.BATCH_JOB_SIZE).getValue()));
        executor.setThreadNamePrefix("MultiThreaded-");
        executor.initialize();
        return executor;
    }
}
