package com.tpex.batchjob.config;

import java.io.File;

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
import com.tpex.batchjob.dto.BatchJobDTO;
import com.tpex.util.TpexConfigurationUtil;

@Component
public class GenericItemReader {

	@Autowired
    private GenericLineMapper genericLineMapper;
	
    @Autowired
	TpexConfigurationUtil tpexConfigurationUtil;
	
/*	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	@Qualifier(value = "flatFileItemReader")
	@StepScope
	public FlatFileItemReader flatFileItemReader(@Value("#{jobParameters[batchName]}") String batchName) throws Exception {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
        return new FlatFileItemReaderBuilder()
        	.name(batchName)
	        .resource(new FileSystemResource(batchJobDTO.getBatchJobInputFilePath()))
	        .linesToSkip(1)
	        .lineMapper(genericLineMapper.defaultLineMapper(batchJobDTO.getBatchJobFixedLengthMappingFileName(), Class.forName(batchJobDTO.getBatchJobInputTargetType())))
	        .build();
    }
	
	private BatchJobDTO initProcessBatch(String batchName) throws Exception {
			String filePath = tpexConfigurationUtil.getFilePath(batchName);
			File file = ResourceUtils.getFile(filePath);
			if (file == null)
				throw new Exception("File Not exsit in path = " + filePath);
			ObjectMapper objMapper = new ObjectMapper();
			objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			System.out.println(file.getName());
			return objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@Qualifier(value = "excelItemReader")
	@StepScope
	public PoiItemReader excelItemReader(@Value("#{jobParameters[batchName]}") String batchName, @Value("#{jobParameters[fileName]}") String fileName) throws Exception {
		BatchJobDTO batchJobDTO = initProcessBatch(batchName);
        PoiItemReader reader = new PoiItemReader();
        reader.setLinesToSkip(1);
        reader.setResource(new FileSystemResource(new File(batchJobDTO.getBatchJobInputFilePath() + "/" + fileName)));
        reader.setRowMapper((RowMapper) Class.forName(batchJobDTO.getBatchJobRowMapper()).getConstructor().newInstance());
        return reader;
    }*/
 
}
