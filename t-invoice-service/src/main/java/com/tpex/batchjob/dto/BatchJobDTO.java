package com.tpex.batchjob.dto;

import java.io.Serializable;

import lombok.Data;
@Data
public class BatchJobDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String batchJobId;
	private String batchJobName;
	private String batchJobFileName;
	private BatchRetry batchRetry;
	private String batchJobInputFilePath;
	private String batchJobOutputFilePath;
	private String batchJobFixedLengthMappingFileName;
	private String batchJobInputTargetType;
	private String batchJobRowMapper;
	private BatchJobParallelProcessing batchJobParallelProcessing;
	private String batchJobArchiveSuccessFilePath;
	private String batchJobArchiveErrorFilePath;

}
