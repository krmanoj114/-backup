package com.tpex.admin.batchjob.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.tpex.admin.batchjob.module.JsonFileReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GenericLineMapper {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultLineMapper defaultLineMapper(String mappingFileName) throws IOException {
		DefaultLineMapper lineMapper = new DefaultLineMapper();
		lineMapper.setLineTokenizer(fixedLengthTokenizer(mappingFileName));

		return lineMapper;
	}

	public FixedLengthTokenizer fixedLengthTokenizer(String mappingFileName) throws IOException {

		FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
		ObjectMapper objectMapper = new ObjectMapper();

		File file = new File(mappingFileName);
		List<JsonFileReader> listOfJsonNamesAndRanges = objectMapper.readValue(file,
				new TypeReference<List<JsonFileReader>>() {
				});

		List<String> listOfColumnNames = listOfJsonNamesAndRanges.stream().map(s -> s.getColumnName())
				.collect(Collectors.toList());
		String[] arrayOfColumnNames = listOfColumnNames.stream().toArray(String[]::new);
		tokenizer.setNames(arrayOfColumnNames);
		Range[] arr = new Range[listOfJsonNamesAndRanges.size()];

		for (int i = 0; i < listOfJsonNamesAndRanges.size(); i++) {
			arr[i] = new Range(listOfJsonNamesAndRanges.get(i).getMinRange(),
					listOfJsonNamesAndRanges.get(i).getMaxRange());
		}
		tokenizer.setColumns(arr);

		return tokenizer;
	}
}
