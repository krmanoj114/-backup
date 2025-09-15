package com.tpex.daily.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.daily.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsoContainerMasterDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	    private String etd;
        
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
		private String bookingNo;
        
	
	@NotBlank(message = ConstantUtils.ERR_CM_3001)
	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
		private String containerRanbanNo;

		private String isoContainerNo;

		private String containerType;

		private String sealNo;

		private BigInteger tareWeight;

		private String containerSize;

		private String shipComp;

		private String vanningStatus;

		private String idCount;

		private String idList;

		private String sno;
        
		@NotBlank(message = ConstantUtils.ERR_CM_3001)
		@NotNull(message = ConstantUtils.ERR_CM_3001)
		@NotEmpty(message = ConstantUtils.ERR_CM_3001)
		private String vanMth;
		
		@NotBlank(message = ConstantUtils.ERR_CM_3001)
		@NotNull(message = ConstantUtils.ERR_CM_3001)
		@NotEmpty(message = ConstantUtils.ERR_CM_3001)
		private String dstCd;

		private String cmpCd;

		private Date updDt;
		
		private String updBy;
}
