package com.tpex.invoice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MixPrivilegeDetailsListResponseDto{


	private List<MixPrivilegeDetailsDto> mixPrivilegeDetails;
	private List<ReExporterCodeDto> reExporterCode;
	private List<PriorityDto> priority;

}
