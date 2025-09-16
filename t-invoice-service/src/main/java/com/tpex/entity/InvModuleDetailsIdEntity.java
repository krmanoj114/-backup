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
public class InvModuleDetailsIdEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "INV_NO")
	private String invNo;
	
	@Column(name = "MOD_NO")
	private String modNo;
	
	@Column(name = "LOT_NO")
	private String lotNo;

}
