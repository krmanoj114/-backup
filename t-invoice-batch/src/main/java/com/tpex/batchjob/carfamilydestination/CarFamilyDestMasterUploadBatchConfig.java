package com.tpex.batchjob.carfamilydestination;

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
import com.tpex.batchjob.ExcelSheetItemReader;
import com.tpex.batchjob.JobCompletionNotificationListener;
import com.tpex.dto.BatchJobDTO;
import com.tpex.dto.CarFamilyDstMasterUploadBatchDto;
import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class CarFamilyDestMasterUploadBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
	    this.jobBuilderFactory = jobBuilderFactory;
	}
	
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    private CarFamilyDestMasterUploadValidator validatecarFamilyDestMasterUpload;
    @Autowired
	public void setValidateLotPartPriceUploadBatchFileTasklet(CarFamilyDestMasterUploadValidator validatecarFamilyDestMasterUpload) {
	    this.validatecarFamilyDestMasterUpload = validatecarFamilyDestMasterUpload;
	}
    
    private CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;
    @Autowired
	public void setCarFamilyMastRepository(CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository) {
	    this.carFamilyDestinationMasterRepository = carFamilyDestinationMasterRepository;
	}
    
    private JobCompletionNotificationListener jobCompletionNotificationListener;
    @Autowired
   	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
   	    this.jobCompletionNotificationListener = jobCompletionNotificationListener;
   	}
    
	@Bean
    @Qualifier(value = "carFamilyDstMasterUploadJob")
    public Job carFamilyDstMasterUploadJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
    		InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		
        return this.jobBuilderFactory.get("carFamilyDstMasterUploadJob")
        		.incrementer(new RunIdIncrementer())
        		.listener(jobCompletionNotificationListener)
        		.start(validatecarFamilyDestMasterUpload())
        		.on("PROCESS").to(processCarFamilyDestMasterUploadFile())
				.end()
        		.build();
    }
	
	@Bean
	protected Step validatecarFamilyDestMasterUpload() {
		return stepBuilderFactory.get("validatecarFamilyDestMasterUpload").tasklet(validatecarFamilyDestMasterUpload).build();
	}
	
	public Step processCarFamilyDestMasterUploadFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
    	SimpleStepBuilder<CarFamilyDstMasterUploadBatchDto, CarFamilyDestinationMasterEntity> builder = this.stepBuilderFactory.get("processCarFamilyDestMasterUploadFile")
    			.<CarFamilyDstMasterUploadBatchDto, CarFamilyDestinationMasterEntity>chunk(5)
    			.reader(carFamilyDestMasterExcelItemReader(null, null))
    	        .processor(carFamilyDestMasterUploadProcessor())
    	        .writer(carFamilyDestMasterUploadWriter());
    	return builder.build();
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public ExcelSheetItemReader<CarFamilyDstMasterUploadBatchDto> carFamilyDestMasterExcelItemReader(
			@Value("#{jobParameters[batchName]}") String batchName,
			@Value("#{jobParameters[fileName]}") String fileName
			) throws IOException, InstantiationException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
				SecurityException, ClassNotFoundException {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
		ExcelSheetItemReader reader = new ExcelSheetItemReader();
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
    public ItemProcessor<CarFamilyDstMasterUploadBatchDto, CarFamilyDestinationMasterEntity> carFamilyDestMasterUploadProcessor() {
        return new CarFamilyDestMasterUploadProcessor();
    }
    
    @Bean
    @StepScope
    public RepositoryItemWriter<CarFamilyDestinationMasterEntity> carFamilyDestMasterUploadWriter() {
		RepositoryItemWriter<CarFamilyDestinationMasterEntity> repositoryItemWriter = new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(carFamilyDestinationMasterRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}
    
}
