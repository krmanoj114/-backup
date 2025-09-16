package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoemRenbanBookDtlIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	 
	 @Column(name="CONT_VAN_MTH")
	 private String contVanningMnth;
	 
	 @Column(name="CONT_DST_CD")
	 private String contDestCd;
	 
	 @Column(name="ETD1")
	 private Date etd1;
	 
	 @Column(name="SHP_NM_1")
	 private String shpName1;
	 
	 @Column(name="GROUP_ID")
	 private String groupId;
	 
	 @Column(name="CONT_GRP_CD")
	 private String contGrpCode;
	 
	 
	
	
	
	
	

}
