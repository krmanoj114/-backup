package com.tpex.batchjob.lotpartpriceupload;

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
import com.tpex.dto.BatchJobDTO;
import com.tpex.dto.LotPartPriceUploadBatchDto;
import com.tpex.dto.LotPartPriceUploadBatchInputDto;
import com.tpex.entity.OemLotPartPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntity;
import com.tpex.repository.LotPartPriceMasterRepository;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class LotPartPriceUploadBatchConfig {
	
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
	    this.jobBuilderFactory = jobBuilderFactory;
	}
	
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    private ValidateLotPartPriceUploadBatchFileTasklet validateLotPartPriceUploadBatchFile;
    @Autowired
	public void setValidateLotPartPriceUploadBatchFileTasklet(ValidateLotPartPriceUploadBatchFileTasklet validateLotPartPriceUploadBatchFile) {
	    this.validateLotPartPriceUploadBatchFile = validateLotPartPriceUploadBatchFile;
	}
    
    @Autowired
    private CalculateLotPriceTasklet calculateLotPriceTasklet;
    
    private LotPartPriceMasterRepository lotPartPriceMasterRepository;
    @Autowired
	public void setLotPartPriceMasterRepository(LotPartPriceMasterRepository lotPartPriceMasterRepository) {
	    this.lotPartPriceMasterRepository = lotPartPriceMasterRepository;
	}
    
    private LotPriceMasterRepository lotPriceMasterRepository;
    @Autowired
   	public void setLotPriceMasterRepository(LotPriceMasterRepository lotPriceMasterRepository) {
   	    this.lotPriceMasterRepository = lotPriceMasterRepository;
   	}
    
    private JobCompletionNotificationListener jobCompletionNotificationListener;
    @Autowired
   	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
   	    this.jobCompletionNotificationListener = jobCompletionNotificationListener;
   	}
    
	@Bean
    @Qualifier(value = "lotPartPriceUploadBatchJob")
    public Job lotPartPriceUploadBatchJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		
        return this.jobBuilderFactory.get("lotPartPriceUploadBatchJob")
        		.incrementer(new RunIdIncrementer())
        		.listener(jobCompletionNotificationListener)
        		.start(validateLotPartPriceUploadBatchFile())
        		.on("PROCESS").to(processLotPartPriceUploadBatchFile())
        		.on("COMPLETED").to(calculateLotPrice()).from(processLotPartPriceUploadBatchFile())
				.end()
        		.build();
    }
	
	@Bean
	protected Step validateLotPartPriceUploadBatchFile() {
		return stepBuilderFactory.get("validateLotPartPriceUploadBatchFile").tasklet(validateLotPartPriceUploadBatchFile).build();
	}
	
	public Step processLotPartPriceUploadBatchFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
    	SimpleStepBuilder<LotPartPriceUploadBatchInputDto, LotPartPriceUploadBatchDto> builder = this.stepBuilderFactory.get("processLotPartPriceUploadBatchFile")
    			.<LotPartPriceUploadBatchInputDto, LotPartPriceUploadBatchDto>chunk(5)
    			.reader(excelItemReader(null, null))
    	        .processor(lotPartPriceUploadBatchProcessor())
    	        .writer(lotPartPriceUploadBatchWriter());
    	return builder.build();
    }
	
	protected Step calculateLotPrice() {
		return stepBuilderFactory.get("calculateLotPrice").tasklet(calculateLotPriceTasklet).build();
	}
	 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public PoiItemReader<LotPartPriceUploadBatchInputDto> excelItemReader(
			@Value("#{jobParameters[batchName]}") String batchName,
			@Value("#{jobParameters[fileName]}") String fileName
			) throws IOException, InstantiationException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
				SecurityException, ClassNotFoundException {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
        PoiItemReader reader = new PoiItemReader();
        reader.setLinesToSkip(1);
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
			throw new FileNotFoundException("File Not exsit in path = " + filePath);
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
	}
	
    @Bean
    @StepScope
    public ItemProcessor<LotPartPriceUploadBatchInputDto, LotPartPriceUploadBatchDto> lotPartPriceUploadBatchProcessor() {
        return new LotPartPriceUploadBatchProcessor();
    }
    
    @Bean
    @StepScope
    public LotPartPriceUploadBatchWriter lotPartPriceUploadBatchWriter() {
    	return new LotPartPriceUploadBatchWriter(lotPartPriceMasterWriter(), lotPriceMasterWriter());
    }
    
    @Bean
    @StepScope
    public RepositoryItemWriter<OemLotPartPrcMstEntity> lotPartPriceMasterWriter() {
		RepositoryItemWriter<OemLotPartPrcMstEntity> repositoryItemWriter=new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(lotPartPriceMasterRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}
    
    @Bean
    @StepScope
    public RepositoryItemWriter<OemLotPrcMstEntity> lotPriceMasterWriter() {
		RepositoryItemWriter<OemLotPrcMstEntity> repositoryItemWriter=new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(lotPriceMasterRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}


}
