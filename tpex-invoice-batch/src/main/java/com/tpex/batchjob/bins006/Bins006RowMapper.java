package com.tpex.batchjob.bins006;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.tpex.util.GlobalConstants;

/**
 * The Class Bins006RowMapper.
 */
public class Bins006RowMapper implements RowMapper<Bins006OutputDto> {

    /** The Constant ID_COLUMN. */
    public static final String ID_COLUMN = "id";
    
    /** The Constant NAME_COLUMN. */
    public static final String NAME_COLUMN = "name";
    
    /** The Constant CREDIT_COLUMN. */
    public static final String CREDIT_COLUMN = "credit";
    
    /** The mint ctr. */
    private int mintCtr = 0;

    /**
     * Map row.
     *
     * @param rs the rs
     * @param rowNum the row num
     * @return the bins 006 output dto
     * @throws SQLException the SQL exception
     */
    @Override
    public Bins006OutputDto mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Bins006OutputDto bins006OutputDto = new Bins006OutputDto();

    	bins006OutputDto.setRecordId("D");
    	bins006OutputDto.setSeqId(genCtr(7));
    	bins006OutputDto.setInvDate(rs.getString("INV_DT"));
    	bins006OutputDto.setInvNo(rs.getString("INV_NO"));
    	bins006OutputDto.setDocReference(String.format("%15s", ""));
    	bins006OutputDto.setDocType("INV");
    	bins006OutputDto.setCancelFlag(rs.getString("ICS_FLG"));
    	bins006OutputDto.setCustomerCode(rs.getString("CNSG_CD"));
    	bins006OutputDto.setSapCode(rs.getString("SAP_CD"));
    	bins006OutputDto.setMovementType("200");
    	bins006OutputDto.setPartNo(rs.getString("PART_NO"));
    	bins006OutputDto.setPackingTimestamp(rs.getString("ACT_PKG_DT"));
    	bins006OutputDto.setPackingPlant(rs.getString("PCK_PLNT"));
    	bins006OutputDto.setPackingRefLoc(rs.getString("PCK_LN_CD"));
    	bins006OutputDto.setPackingRefLocType("P");
    	bins006OutputDto.setPartQty(rs.getString("UNIT_PER_BOX"));
    	bins006OutputDto.setCurrency(rs.getString("PAY_CRNCY"));
    	bins006OutputDto.setDestName(rs.getString("DST_NM"));
    	bins006OutputDto.setDestCode(rs.getString("FINAL_DST"));
    	bins006OutputDto.setCfCode(rs.getString("CF_CD"));
    	bins006OutputDto.setSeries(rs.getString("SERIES"));
    	bins006OutputDto.setEtd(rs.getString("ETD"));
    	bins006OutputDto.setExchangeRate(rs.getString("EXCH_RT"));
    	bins006OutputDto.setFreight(rs.getString("FREIGHT"));
    	bins006OutputDto.setInsurance(rs.getString("INSURANCE"));
    	bins006OutputDto.setShipModuleNo(rs.getString("SHP_MOD_NO"));
    	bins006OutputDto.setUnitPrice(rs.getString("PRICE"));
    	bins006OutputDto.setTotalAmount(rs.getString("INV_AMT"));
    	bins006OutputDto.setSourceDate(LocalDateTime.now().toString());
    	bins006OutputDto.setExportCreditAmt(rs.getString("EXP_CREDIT"));
    	bins006OutputDto.setCommercialFlag(rs.getString("NONCOM_FLG"));
    	bins006OutputDto.setOrderType(rs.getString("ORD_TYP"));
    	bins006OutputDto.setMeasurement(rs.getString("TOT_M3"));
    	bins006OutputDto.setPartType(rs.getString("PRM_TYP"));
    	bins006OutputDto.setDsiTiNo(rs.getString("DSI_TI_NO"));
    	bins006OutputDto.setTransportMode(rs.getString("TRNSPRT_CD"));
    	
        return bins006OutputDto;
    }
    
    /**
     * Gen ctr.
     *
     * @param aintLC the aint LC
     * @return the string
     */
    private String genCtr(int aintLC){
		mintCtr++;
		StringBuilder lstrCtr = new StringBuilder(mintCtr + GlobalConstants.BLANK);
		int lintLen	= aintLC - lstrCtr.length();

		for(int aintCtr = 1; aintCtr <= lintLen; aintCtr++){
			lstrCtr.insert(0, "0");
		}
		return 	lstrCtr.toString();
	}

}
