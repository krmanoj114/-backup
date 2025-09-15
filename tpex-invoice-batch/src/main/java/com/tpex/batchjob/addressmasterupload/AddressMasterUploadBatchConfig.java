package com.tpex.batchjob.addressmasterupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.batchjob.JobCompletionNotificationListener;
import com.tpex.dto.AddressMasterUploadBatchDto;
import com.tpex.dto.BatchJobDTO;
import com.tpex.entity.AddressMasterEntity;
import com.tpex.repository.AddressMasterRepository;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class AddressMasterUploadBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
	    this.jobBuilderFactory = jobBuilderFactory;
	}
	
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    private ValidateAddressMasterUploadFileTasklet validateAddressMasterUploadTasklet;
    @Autowired
	public void setValidateLotPartPriceUploadBatchFileTasklet(ValidateAddressMasterUploadFileTasklet validateAddressMasterUploadTasklet) {
	    this.validateAddressMasterUploadTasklet = validateAddressMasterUploadTasklet;
	}
    
    private AddressMasterRepository addressMasterRepository;
    @Autowired
	public void setAddressMasterRepository(AddressMasterRepository addressMasterRepository) {
	    this.addressMasterRepository = addressMasterRepository;
	}
    
    private JobCompletionNotificationListener jobCompletionNotificationListener;
    @Autowired
   	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
   	    this.jobCompletionNotificationListener = jobCompletionNotificationListener;
   	}
    
	@Bean
    @Qualifier(value = "addressMasterUploadJob")
    public Job addressMasterUploadJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		
        return this.jobBuilderFactory.get("addressMasterUploadJob")
        		.incrementer(new RunIdIncrementer())
        		.listener(jobCompletionNotificationListener)
        		.start(validateAddressMasterUploadTasklet())
        		.on("PROCESS").to(processAddressMasterUploadFile())
        		.on("COMPLETED").end()
				.end()
        		.build();
    }
	
	@Bean
	protected Step validateAddressMasterUploadTasklet() {
		return stepBuilderFactory.get("validateAddressMasterUploadTasklet").tasklet(validateAddressMasterUploadTasklet).build();
	}
	
	public Step processAddressMasterUploadFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
    	SimpleStepBuilder<AddressMasterUploadBatchDto, AddressMasterEntity> builder = this.stepBuilderFactory.get("processAddressMasterUploadFile")
    			.<AddressMasterUploadBatchDto, AddressMasterEntity>chunk(5)
    			.reader(addressMasterExcelItemReader(null, null))
    	        .processor(addressMasterUploadProcessor())
    	        .writer(addressMasterUploadWriter());
    	return builder.build();
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public PoiItemReader<AddressMasterUploadBatchDto> addressMasterExcelItemReader(
			@Value("#{jobParameters[batchName]}") String batchName,
			@Value("#{jobParameters[fileName]}") String fileName
			) throws IOException, InstantiationException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
				SecurityException, ClassNotFoundException {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
        PoiItemReader reader = new PoiItemReader();
        reader.setLinesToSkip(2);
        reader.setResource(new FileSystemResource(ResourceUtils.getFile(batchJobDTO.getBatchJobInputFilePath() + fileName)));
        reader.setRowMapper((RowMapper) Class.forName(batchJobDTO.getBatchJobRowMapper()).getConstructor().newInstance());
        return reader;
    }
	
	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;
	
	private BatchJobDTO initProcessBatch(String batchName) throws IOException {
		String filePath = tpexConfigurationUtil.getFilePath(batchName);
		File file = ResourceUtils.getFile(filePath);
		if (!file.exists())
			throw new FileNotFoundException("File Not exist in path = " + filePath);
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
	}
	
    @Bean
    @StepScope
    public ItemProcessor<AddressMasterUploadBatchDto, AddressMasterEntity> addressMasterUploadProcessor() {
        return new AddressMasterUploadProcessor();
    }
    
    @Bean
    @StepScope
    public RepositoryItemWriter<AddressMasterEntity> addressMasterUploadWriter() {
		RepositoryItemWriter<AddressMasterEntity> repositoryItemWriter = new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(addressMasterRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}
    
}
