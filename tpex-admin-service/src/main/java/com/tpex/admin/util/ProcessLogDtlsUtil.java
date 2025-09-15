package com.tpex.admin.util;

import com.tpex.admin.repository.ProcessLogDtlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import com.tpex.admin.entity.ProcessLogDtlsEntity;


@Component
public class ProcessLogDtlsUtil {

	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired
	private Tracer tracer;

	/**
	 * @return
	 * @throws Exception
	 */
	public int getNextProcessLogId() {
		ProcessLogDtlsEntity processLogDtlsEntity = processLogDtlsRepository.findTopByOrderByIdDesc();
		if (processLogDtlsEntity == null) {
			return 1;
		} else {
			return processLogDtlsEntity.getId() + 1;
		}

	}

	public String getCurrentSpanId() {
		Span span = tracer.currentSpan();
		String spanId = null;
		if (span != null) {
			spanId = span.context().spanId();
		}

		return spanId;
	}
}
