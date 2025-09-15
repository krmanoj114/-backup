package com.tpex.batchjob.pxpprice;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

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

import com.tpex.batchjob.ExcelSheetItemReader;
import com.tpex.batchjob.JobCompletionNotificationListener;
import com.tpex.dto.BatchJobDTO;
import com.tpex.dto.PxpPriceMasterUploadBatchDto;
import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;
import com.tpex.repository.PartPriceMasterRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.DateUtil;
import com.tpex.util.TpexConfigurationUtil;

/**
 * The Class PxpPriceMasterUploadBatchConfig.
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class PxpPriceMasterUploadBatchConfig {

	/** The tpex configuration util. */
	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;
	
	/** The batch util. */
	private BatchUtil batchUtil;
	
	/**
	 * Sets the batch util.
	 *
	 * @param batchUtil the new batch util
	 */
	@Autowired
	public void setBatchUtil(BatchUtil batchUtil) {
	    this.batchUtil = batchUtil;
	}
	
	/** The job builder factory. */
	private JobBuilderFactory jobBuilderFactory;
	
	/**
	 * Sets the job builder factory.
	 *
	 * @param jobBuilderFactory the new job builder factory
	 */
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
	    this.jobBuilderFactory = jobBuilderFactory;
	}
	
    /** The step builder factory. */
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    /** The validate pxp price master upload. */
    private PxpPriceMasterUploadValidator validatePxpPriceMasterUpload;
    
    /**
     * Sets the validate lot part price upload batch file tasklet.
     *
     * @param validatePxpPriceMasterUpload the new validate lot part price upload batch file tasklet
     */
    @Autowired
	public void setValidateLotPartPriceUploadBatchFileTasklet(PxpPriceMasterUploadValidator validatePxpPriceMasterUpload) {
	    this.validatePxpPriceMasterUpload = validatePxpPriceMasterUpload;
	}
    
    /** The part price master repository. */
    private PartPriceMasterRepository partPriceMasterRepository;
    
    /**
     * Sets the part price master repository.
     *
     * @param partPriceMasterRepository the new part price master repository
     */
    @Autowired
	public void setPartPriceMasterRepository(PartPriceMasterRepository partPriceMasterRepository) {
	    this.partPriceMasterRepository = partPriceMasterRepository;
	}
    
    /** The job completion notification listener. */
    private JobCompletionNotificationListener jobCompletionNotificationListener;
    
    /**
     * Sets the job completion notification listener.
     *
     * @param jobCompletionNotificationListener the new job completion notification listener
     */
    @Autowired
   	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
   	    this.jobCompletionNotificationListener = jobCompletionNotificationListener;
   	}
    
	/**
	 * Pxp price master upload job.
	 *
	 * @return the job
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Bean
    @Qualifier(value = "pxpPriceMasterUploadJob")
    public Job pxpPriceMasterUploadJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
    		InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		
        return this.jobBuilderFactory.get("pxpPriceMasterUploadJob")
        		.incrementer(new RunIdIncrementer())
        		.listener(jobCompletionNotificationListener)
        		.start(validatePxpPriceMasterUpload())
        		.on("PROCESS").to(processPxpPriceMasterUploadFile())
				.end()
        		.build();
    }
	
	/**
	 * Validate pxp price master upload.
	 *
	 * @return the step
	 */
	@Bean
	protected Step validatePxpPriceMasterUpload() {
		return stepBuilderFactory.get("validatePxpPriceMasterUpload").tasklet(validatePxpPriceMasterUpload).build();
	}
	
	/**
	 * Process pxp price master upload file.
	 *
	 * @return the step
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Step processPxpPriceMasterUploadFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
    	SimpleStepBuilder<PxpPriceMasterUploadBatchDto, PartPriceMasterEntity> builder = this.stepBuilderFactory.get("processPxpPriceMasterUploadFile")
    			.<PxpPriceMasterUploadBatchDto, PartPriceMasterEntity>chunk(5)
    			.reader(pxpPriceMasterExcelItemReader(null, null))
    	        .processor(pxpPriceMasterUploadProcessor(null, null, null, null))
    	        .writer(pxpPriceMasterUploadWriter());
    	return builder.build();
    }
	
	/**
	 * Pxp price master excel item reader.
	 *
	 * @param batchName the batch name
	 * @param fileName the file name
	 * @return the excel sheet item reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public ExcelSheetItemReader<PxpPriceMasterUploadBatchDto> pxpPriceMasterExcelItemReader(
			@Value("#{jobParameters[batchName]}") String batchName,
			@Value("#{jobParameters[fileName]}") String fileName
			) throws IOException, InstantiationException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, 
				SecurityException, ClassNotFoundException {
		BatchJobDTO batchJobDTO = batchUtil.initProcessBatch(batchName);
		ExcelSheetItemReader reader = new ExcelSheetItemReader();
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource(ResourceUtils.getFile(batchJobDTO.getBatchJobInputFilePath() + fileName)));
        reader.setRowMapper((RowMapper) Class.forName(batchJobDTO.getBatchJobRowMapper()).getConstructor().newInstance());
        return reader;
    }
	
    /**
     * Pxp price master upload processor.
     *
     * @param companyCode the company code
     * @param userId the user id
     * @param effectiveFromDate the effective from date
     * @param effectiveToDate the effective to date
     * @return the item processor
     */
    @Bean
    @StepScope
    public ItemProcessor<PxpPriceMasterUploadBatchDto, PartPriceMasterEntity> pxpPriceMasterUploadProcessor(
    		@Value("#{jobParameters[companyCode]}") String companyCode,
			@Value("#{jobParameters[userId]}") String userId,
			@Value("#{jobParameters[effectiveFromDate]}") String effectiveFromDate,
			@Value("#{jobParameters[effectiveToDate]}") String effectiveToDate) {
        return new ItemProcessor<PxpPriceMasterUploadBatchDto, PartPriceMasterEntity>() {

			@Override
			public PartPriceMasterEntity process(PxpPriceMasterUploadBatchDto item) throws Exception {
				final PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
				
				partPriceMasterEntity.setId(new PartPriceMasterIdEntity(item.getCfCode(), item.getDestCode(), item.getPartNo(), effectiveFromDate.replace("/", "")));
				partPriceMasterEntity.setCurrencyCode(item.getCurrencyCode());
				partPriceMasterEntity.setPartName(item.getPartName());
				partPriceMasterEntity.setPartPrice(Double.valueOf(item.getPartPrice()));
				partPriceMasterEntity.setEffToMonth(effectiveToDate.replace("/", ""));
				partPriceMasterEntity.setCmpCd(companyCode);
				partPriceMasterEntity.setUpdateBy(userId);
				partPriceMasterEntity.setUpdateDate(DateUtil.convertToSqlDate(LocalDate.now()).toString());
				return partPriceMasterEntity;
			}
        };
    }
    
    /**
     * Pxp price master upload writer.
     *
     * @return the pxp price master upload writer
     */
    @Bean
    @StepScope
    public PxpPriceMasterUploadWriter pxpPriceMasterUploadWriter() {
    	return new PxpPriceMasterUploadWriter(partPriceMasterRepositoryWriter());
    }
    
    /**
     * Part price master repository writer.
     *
     * @return the repository item writer
     */
    @Bean
    @StepScope
    public RepositoryItemWriter<PartPriceMasterEntity> partPriceMasterRepositoryWriter() {
		RepositoryItemWriter<PartPriceMasterEntity> repositoryItemWriter = new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(partPriceMasterRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}
    
}
