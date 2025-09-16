package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_SYSTEM_D")
public class SystemDtlsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SystemDtlsIdEntity id;
}
