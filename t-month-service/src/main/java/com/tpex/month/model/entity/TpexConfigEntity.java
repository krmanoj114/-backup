package com.tpex.month.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_TPEX_CONFIG")
public class TpexConfigEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "VALUE")
	private String value;
	@Column(name = "CMP_CD")
	private String cmpCd;

}
