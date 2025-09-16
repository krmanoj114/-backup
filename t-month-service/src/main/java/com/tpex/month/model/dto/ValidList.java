package com.tpex.month.model.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import lombok.experimental.Delegate;

public class ValidList<E> implements List<E> {

	@Valid
	@Delegate
    private List<E> list = new ArrayList<>();
}
