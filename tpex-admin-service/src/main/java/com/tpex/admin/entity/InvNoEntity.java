package com.tpex.admin.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Data
@Table(name = "tb_r_inv_invoice_h")
@Entity
public class InvNoEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id		
	@Column(name = "INV_NO")
	private String invNo;
	
	@Column(name="HAISEN_NO")
	private String haisenNo;
	
	@Column(name="IXOS_FLG")
    private String ixosFlg;
}
