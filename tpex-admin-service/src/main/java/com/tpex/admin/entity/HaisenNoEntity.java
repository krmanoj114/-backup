package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "tb_r_haisen_d")
@Entity
public class HaisenNoEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "HAISEN_NO")
	private String haisenNo;
}
