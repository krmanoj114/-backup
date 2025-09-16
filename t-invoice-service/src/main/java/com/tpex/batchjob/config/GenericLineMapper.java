package com.tpex.batchjob.config;

import org.springframework.stereotype.Component;

@Component
public class GenericLineMapper {

	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) public DefaultLineMapper
	 * defaultLineMapper(String mappingFileName, Class targetType) throws
	 * StreamReadException, IOException { DefaultLineMapper lineMapper = new
	 * DefaultLineMapper()
	 * lineMapper.setLineTokenizer(fixedLengthTokenizer(mappingFileName)) 
	 * BeanWrapperFieldSetMapper beanWrapperFieldSetMapper=new
	 * BeanWrapperFieldSetMapper()
	 * beanWrapperFieldSetMapper.setTargetType(targetType)
	 * lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper)
	 * 
	 * lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper() 
	 * setTargetType(targetType) } }) return lineMapper 
	 */

	/*
	 * public FixedLengthTokenizer fixedLengthTokenizer(String mappingFileName)
	 * throws StreamReadException, IOException 
	 * 
	 * FixedLengthTokenizer tokenizer = new FixedLengthTokenizer(); ObjectMapper
	 * objectMapper = new ObjectMapper()
	 * 
	 * File file = new File(mappingFileName); List<JsonFileReader>
	 * listOfJsonNamesAndRanges = objectMapper.readValue(file, new
	 * TypeReference<List<JsonFileReader>>(){})
	 * 
	 * List<String> listOfColumnNames = listOfJsonNamesAndRanges.stream().map(s ->
	 * s.getColumnName()).collect(Collectors.toList()); String[] arrayOfColumnNames
	 * = listOfColumnNames.stream().toArray(String[]::new)
	 * tokenizer.setNames(arrayOfColumnNames); Range[] arr = new
	 * Range[listOfJsonNamesAndRanges.size()]
	 * 
	 * for int i=0; i < listOfJsonNamesAndRanges.size() i++
	 * Range(listOfJsonNamesAndRanges.get(i).getMinRange(),
	 * listOfJsonNamesAndRanges.get(i).getMaxRange()); } tokenizer.setColumns(arr)
	 * 
	 * return tokenizer
	 */
}
