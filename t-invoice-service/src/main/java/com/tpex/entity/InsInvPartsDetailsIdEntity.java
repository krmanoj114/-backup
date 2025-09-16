package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsInvPartsDetailsIdEntity implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "INV_NO")
	private String inpInvNo;
	@Column(name = "MOD_NO")
	private String inpModNo;
	@Column(name = "LOT_NO")
	private String inpLotNo;
	@Column(name = "PART_NO")
	private String inpPartNo;
	@Column(name = "BOX_NO")
	private String inpBoxNo;

}
