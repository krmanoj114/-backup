package com.tpex.invoice.dto;

import lombok.Data;

@Data
public class InhouseShopDTO { 

	/** INS_SHOP_CD */
	private String insShopCd;

	/** concat(INS_SHOP_CD,DESCRIPTION) */
	private String insShopCdDesc;

}