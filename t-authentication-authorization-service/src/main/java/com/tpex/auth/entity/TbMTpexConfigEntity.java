package com.tpex.auth.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "tb_m_tpex_config")
//avoid conflict mapping entity
@Entity(name = "c")
public class TbMTpexConfigEntity {

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
