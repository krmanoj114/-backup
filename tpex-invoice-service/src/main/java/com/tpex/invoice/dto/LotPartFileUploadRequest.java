package com.tpex.invoice.dto;

import java.io.File;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotPartFileUploadRequest {
	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String effectFromMonth;
	
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String effectToMonth;
	
}
