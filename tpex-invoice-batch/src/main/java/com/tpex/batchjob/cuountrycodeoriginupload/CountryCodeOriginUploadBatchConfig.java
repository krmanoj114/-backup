package com.tpex.batchjob.cuountrycodeoriginupload;

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
import com.tpex.dto.CountryCodeOriginUploadBatchInputDto;
import com.tpex.entity.CountryOriginMstEntity;
import com.tpex.repository.CountryOriginMstRepository;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class CountryCodeOriginUploadBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
	}

	@Autowired
	private StepBuilderFactory stepBuilderFactory;



	private ValidateCountryCodeOriginUploadBatchFileTasklet batchFileTasklet ;

	@Autowired
	public void setValidateCountryCodeOriginUploadBatchFileTasklet(ValidateCountryCodeOriginUploadBatchFileTasklet batchFileTasklet) {
		this.batchFileTasklet = batchFileTasklet;
	}

	private CountryOriginMstRepository countryOriginMstRepository;
	@Autowired
	public void setinvGenWorkPlanMstRepository(CountryOriginMstRepository countryOriginMstRepository) {
		this.countryOriginMstRepository = countryOriginMstRepository;
	} 

	private JobCompletionNotificationListener jobCompletionNotificationListener;
	@Autowired
	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
		this.jobCompletionNotificationListener = jobCompletionNotificationListener;
	}


	@Bean
	@Qualifier(value = "countryCodeOriginUploadBatchJob")
	public Job countryCodeOriginUploadBatchJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {

		return this.jobBuilderFactory.get("countryCodeOriginUploadBatchJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobCompletionNotificationListener)
				.start(batchFileTasklet())
				.on("PROCESS").to(processCountryCodeOriginUploadBatchFile())
				.on("COMPLETED")
				.end().end().build();

	}

	@Bean
	protected Step batchFileTasklet() {
		return stepBuilderFactory.get("batchFileTasklet").tasklet(batchFileTasklet).build();
	}

	public Step processCountryCodeOriginUploadBatchFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		SimpleStepBuilder<CountryCodeOriginUploadBatchInputDto, CountryOriginMstEntity> builder = this.stepBuilderFactory.get("processCountryCodeOriginUploadBatchFile")
				.<CountryCodeOriginUploadBatchInputDto, CountryOriginMstEntity>chunk(5)
				.reader(countryCodeOriginexcelItemReader(null, null))
				.processor(countryCodeOriginUploadBatchProcessor())
				.writer(countryCodeOriginMasterWriter());
		return builder.build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public ExcelSheetItemReader<CountryCodeOriginUploadBatchInputDto> countryCodeOriginexcelItemReader(
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
			throw new FileNotFoundException("File Not exsit in path = " + filePath);
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
	}

	@Bean
	@StepScope
	public ItemProcessor<CountryCodeOriginUploadBatchInputDto, CountryOriginMstEntity> countryCodeOriginUploadBatchProcessor() {
		return new CountryCodeOriginUploadBatchProcessor();
	}


	@Bean
	@StepScope
	public RepositoryItemWriter<CountryOriginMstEntity> countryCodeOriginMasterWriter() {
		RepositoryItemWriter<CountryOriginMstEntity> repositoryItemWriter=new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(countryOriginMstRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}


}
