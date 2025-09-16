package com.tpex.batchjob.module;

import lombok.Data;

@Data
public class JsonFileReader {
	private String columnName;
	private Integer minRange;
	private Integer maxRange;
}
