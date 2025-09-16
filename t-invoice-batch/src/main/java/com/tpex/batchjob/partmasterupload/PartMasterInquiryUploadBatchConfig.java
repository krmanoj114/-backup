package com.tpex.batchjob.partmasterupload;

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
import com.tpex.dto.PartMasterUploadBatchDto;
import com.tpex.entity.PartMasterEntity;
import com.tpex.repository.PartMasterRespository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class PartMasterInquiryUploadBatchConfig {
	
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
	}

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private PartMasterUploadValidator validatePartMasterUpload;
	@Autowired
	public void setValidateLotPartPriceUploadBatchFileTasklet(PartMasterUploadValidator validatePartMasterUpload) {
		this.validatePartMasterUpload = validatePartMasterUpload;
	}

	private PartMasterRespository partMasterRespository;
	@Autowired
	public void setPartMasterRespository(PartMasterRespository partMasterRespository) {
		this.partMasterRespository = partMasterRespository;
	}

	private JobCompletionNotificationListener jobCompletionNotificationListener;
	@Autowired
	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
		this.jobCompletionNotificationListener = jobCompletionNotificationListener;
	}

	@Bean
	@Qualifier(value = "partMasterUploadJob")
	public Job partMasterUploadJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {

		return this.jobBuilderFactory.get("partMasterUploadJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobCompletionNotificationListener)
				.start(validatePartMasterUpload())
				.on(ConstantUtils.BATCHSTATUS_PROCESS).to(processPartMasterUploadFile())
				.end()
				.build();
	}

	@Bean
	protected Step validatePartMasterUpload() {
		return stepBuilderFactory.get("validatePartMasterUpload").tasklet(validatePartMasterUpload).build();
	}

	public Step processPartMasterUploadFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, 
	InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		SimpleStepBuilder<PartMasterUploadBatchDto, PartMasterEntity> builder = this.stepBuilderFactory.get("processPartMasterUploadFile")
				.<PartMasterUploadBatchDto, PartMasterEntity>chunk(5)
				.reader(partMasterExcelItemReader(null, null))
				.processor(partMasterUploadProcessor())
				.writer(partMasterUploadWriter());
		return builder.build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public ExcelSheetItemReader<PartMasterUploadBatchDto> partMasterExcelItemReader(
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
	public ItemProcessor<PartMasterUploadBatchDto, PartMasterEntity> partMasterUploadProcessor() {
		return new PartMasterUploadProcessor();
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<PartMasterEntity> partMasterUploadWriter() {
		RepositoryItemWriter<PartMasterEntity> repositoryItemWriter = new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(partMasterRespository);
		repositoryItemWriter.setMethodName(ConstantUtils.SAVE);
		return repositoryItemWriter;
	}


}
