package com.tpex.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "TPEX_CONFIG")
//avoid conflict mapping entity
@Entity(name = "TpexConfigAuthEntity")
public class TpexConfigEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Integer id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "VALUE")
	private String value;

}
