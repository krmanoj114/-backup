package com.tpex.batchjob.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class BatchJobParallelProcessing implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Boolean isParallelprocessingRequired = false;
	private Integer corePoolSize;
	private Integer maxPoolSize;
	private Integer queueCapacity;
	private String threadNamePrefix;
}
