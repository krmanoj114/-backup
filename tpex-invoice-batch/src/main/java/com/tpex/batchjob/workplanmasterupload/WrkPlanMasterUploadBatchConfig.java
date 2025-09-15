package com.tpex.batchjob.workplanmasterupload;

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
import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.repository.InvGenWorkPlanMstRepository;
import com.tpex.util.TpexConfigurationUtil;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class WrkPlanMasterUploadBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
	}

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private ValidateWrkPlanMasterUploadBatchFileTasklet validatewrkPlanMasterUploadBatchFile;
	@Autowired
	public void setValidateWrkPlanMasterUploadBatchFileTasklet(ValidateWrkPlanMasterUploadBatchFileTasklet validatewrkPlanMasterUploadBatchFile) {
		this.validatewrkPlanMasterUploadBatchFile = validatewrkPlanMasterUploadBatchFile;
	}

	private InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;
	@Autowired
	public void setinvGenWorkPlanMstRepository(InvGenWorkPlanMstRepository invGenWorkPlanMstRepository) {
		this.invGenWorkPlanMstRepository = invGenWorkPlanMstRepository;
	} 

	private JobCompletionNotificationListener jobCompletionNotificationListener;
	@Autowired
	public void setJobCompletionNotificationListener(JobCompletionNotificationListener jobCompletionNotificationListener) {
		this.jobCompletionNotificationListener = jobCompletionNotificationListener;
	}


	@Bean
	@Qualifier(value = "wrkPlanMasterUploadBatchJob")
	public Job wrkPlanMasterUploadBatchJob() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {

		return this.jobBuilderFactory.get("wrkPlanMasterUploadBatchJob")
				.incrementer(new RunIdIncrementer())
				.listener(jobCompletionNotificationListener)
				.start(validatewrkPlanMasterUploadBatchFile())
				.on("PROCESS").to(processWrkPlanMasterUploadBatchFile())
				.on("COMPLETED")
				.end().end().build();

	}

	@Bean
	protected Step validatewrkPlanMasterUploadBatchFile() {
		return stepBuilderFactory.get("validatewrkPlanMasterUploadBatchFile").tasklet(validatewrkPlanMasterUploadBatchFile).build();
	}

	public Step processWrkPlanMasterUploadBatchFile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException {
		SimpleStepBuilder<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity> builder = this.stepBuilderFactory.get("processWrkPlanMasterUploadBatchFile")
				.<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity>chunk(5)
				.reader(wrkPlanMasterexcelItemReader(null, null))
				.processor(wrkPlanMasterUploadBatchProcessor())
				.writer(wrkPlanMasterWriter());
		return builder.build();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@StepScope
	public PoiItemReader<WrkPlanMasterUploadBatchInputDto> wrkPlanMasterexcelItemReader(
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
	public ItemProcessor<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity> wrkPlanMasterUploadBatchProcessor() {
		return new WrkPlanMasterUploadBatchProcessor();
	}


	@Bean
	@StepScope
	public RepositoryItemWriter<InvGenWorkPlanMstEntity> wrkPlanMasterWriter() {
		RepositoryItemWriter<InvGenWorkPlanMstEntity> repositoryItemWriter=new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(invGenWorkPlanMstRepository);
		repositoryItemWriter.setMethodName("save");
		return repositoryItemWriter;
	}




}
