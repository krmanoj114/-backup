package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new tpex config entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_M_TPEX_CONFIG")
@Entity
public class TpexConfigEntity implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Integer id;
	
	/** The name. */
	@Column(name = "NAME")
	private String name;

	/** The value. */
	@Column(name = "VALUE")
	private String value;
	
	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;

}