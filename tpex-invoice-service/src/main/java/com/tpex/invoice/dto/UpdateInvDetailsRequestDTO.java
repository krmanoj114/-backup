package com.tpex.invoice.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInvDetailsRequestDTO implements Serializable {

	/**
	 * 
	 * Updating Invoice Details, tablename= INS_INV_DTLS
	 * 
	 * @author R.1.Reddy
	 * 
	 * @Tepex-137
	 * 
	 */

	private static final long serialVersionUID = 1L;

	@NotNull(message = ConstantUtils.ERR_CM_3001)

	@NotEmpty(message = ConstantUtils.ERR_CM_3001)

	private String invNo;

	private String scAuthorize;

	/** The freight. */
	private String freight;

	/** The insurance. */
	private String insurance;

	@NotNull(message = ConstantUtils.ERR_CM_3001)

	@NotEmpty(message = ConstantUtils.ERR_CM_3001)

	private String productGrpObj;

	@NotNull(message = ConstantUtils.ERR_CM_3001)

	@NotEmpty(message = ConstantUtils.ERR_CM_3001)

	private String paymentTermObj;

	@NotNull(message = ConstantUtils.ERR_CM_3001)

	@NotEmpty(message = ConstantUtils.ERR_CM_3001)

	private String custCode;

	@NotNull(message = ConstantUtils.ERR_CM_3001)

	@NotEmpty(message = ConstantUtils.ERR_CM_3001)

	private String consineeName;

	private String notifyPartyName;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indMark1;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indMark2;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indMark3;

	private String indMark4;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indMark5;

	private String indMark6;

	private String indMark7;

	private String indMark8;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indGoodsDesc1;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String indGoodsDesc2;

	private String indGoodsDesc3;

	private String indGoodsDesc4;

	private String indGoodsDesc5;

	private String indGoodsDesc6;

}
