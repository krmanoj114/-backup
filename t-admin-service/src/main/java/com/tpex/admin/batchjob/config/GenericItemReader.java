package com.tpex.admin.batchjob.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.tpex.admin.batchjob.dto.BatchJobDTO;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.TpexConfigurationUtil;

@Component
public class GenericItemReader {

	@Autowired
	private GenericLineMapper genericLineMapper;

	@Autowired
	TpexConfigurationUtil tpexConfigurationUtil;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	@Qualifier(value = "flatFileItemReader")
	@StepScope
	public FlatFileItemReader flatFileItemReader(@Value("#{jobParameters[batchName]}") String batchName)
			throws IOException {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
		return new FlatFileItemReaderBuilder().name(batchName)
				.resource(new FileSystemResource(batchJobDTO.getBatchJobInputFilePath())).linesToSkip(1)
				.lineMapper(genericLineMapper.defaultLineMapper(batchJobDTO.getBatchJobFixedLengthMappingFileName()))
				.build();
	}

	private BatchJobDTO initProcessBatch(String batchName) throws IOException {
		String filePath = tpexConfigurationUtil.getFilePath(batchName);
		File file = ResourceUtils.getFile(filePath);
		if (!file.exists())
			throw new FileNotFoundException("File Not exsit in path = " + filePath);
		ObjectMapper objMapper = new ObjectMapper();
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@Qualifier(value = "excelItemReader")
	@StepScope
	public PoiItemReader excelItemReader(@Value("#{jobParameters[batchName]}") String batchName,
			@Value("#{jobParameters[fileName]}") String fileName)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
		PoiItemReader reader = new PoiItemReader();
		reader.setLinesToSkip(1);
		reader.setResource(new FileSystemResource(
				new File(batchJobDTO.getBatchJobInputFilePath() + ConstantUtils.DELIMITER + fileName)));
		reader.setRowMapper(
				(RowMapper) Class.forName(batchJobDTO.getBatchJobRowMapper()).getConstructor().newInstance());
		return reader;
	}

}
