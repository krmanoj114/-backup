package com.tpex.daily.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tb_m_part")
public class PartMasterEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The part no. */
	@Id
	@Column(name="PART_NO")
	private String partNo;
	
	/** The part name. */
	@Column(name="PART_NM")
	private String partName;
	
	/** The type. */
	@Column(name="TYP")
	private String type;
	
	/** The inhouse shop. */
	@Column(name="INHOUSE_SHOP")
	private String inhouseShop;
	
	/** The weight. */
	@Column(name="WEIGHT")
	private String weight;
	
	/** The update date. */
	@Column(name="UPD_DT")
	private String updateDate;
	
	/** The update by. */
	@Column(name="UPD_BY")
	private String updateBy;
	
	/** The batch update date. */
	@Column(name="BATCH_UPD_DT")
	private String batchUpdateDate;
	
	/** The cmp code. */
	@Column(name="CMP_CD")
	private String cmpCode;
	
}
