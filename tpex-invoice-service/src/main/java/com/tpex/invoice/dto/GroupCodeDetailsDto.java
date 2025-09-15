package com.tpex.invoice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupCodeDetailsDto {

	private String groupId;
	private String renbanGroupCode;
	private String folderName;
}
