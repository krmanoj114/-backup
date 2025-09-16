package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new address master id entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AddressMasterIdEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cmp code. */
	@Column(name = "CD")
	private String cmpCode;

	/** The branch. */
	@Column(name = "BRANCH")
	private String branch;
	
	/** The company. */
	@Column(name = "COMPANY")
	private String company;

}