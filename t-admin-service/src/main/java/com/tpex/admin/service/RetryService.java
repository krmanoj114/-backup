package com.tpex.admin.service;

import java.util.Map;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.tpex.admin.exception.InvalidInputParametersException;

public interface RetryService {

	@Retryable(value = {InvalidInputParametersException.class}, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, Object> getBatchStatus(String userId,String batchId) throws InvalidInputParametersException;

}
