package com.tpex.admin.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ReportStatusInformation {
	
	List<String> status= new ArrayList<>();
	List<String> reportNames= new ArrayList<>();

}
