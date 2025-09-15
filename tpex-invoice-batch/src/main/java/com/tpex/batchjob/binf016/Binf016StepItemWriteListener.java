package com.tpex.batchjob.binf016;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.repository.Binf016CustomRepository;
import com.tpex.util.DateUtil;

public class Binf016StepItemWriteListener implements ItemWriteListener<Binf016OutputDto> {

	@Autowired
	private Binf016CustomRepository binf016CustomRepository;
	
	@Override
	public void beforeWrite(List<? extends Binf016OutputDto> items) {
		// Do nothing
	}

	@Override
	public void afterWrite(List<? extends Binf016OutputDto> items) {
		List<String> invNoList = items.stream().map(m -> m.getInvNo()).distinct().collect(Collectors.toList());
		for (String invNo : invNoList) {
			binf016CustomRepository.updateInvIxosFlag(invNo, "Y", DateUtil.convertToSqlDate(LocalDate.now()));
		}
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Binf016OutputDto> items) {
		// Do nothing
	}

}
