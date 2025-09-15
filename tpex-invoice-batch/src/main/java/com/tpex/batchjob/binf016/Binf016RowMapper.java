package com.tpex.batchjob.binf016;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class Binf016RowMapper implements RowMapper<Binf016OutputDto> {

	@Override
	public Binf016OutputDto mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Binf016OutputDto binf016OutputDto = new Binf016OutputDto();
		binf016OutputDto.setRecordId(rs.getString("REC_ID"));
		binf016OutputDto.setInvNo(rs.getString("INV_NO"));
		binf016OutputDto.setInvDate(rs.getString("INV_DT"));
		binf016OutputDto.setVesselNameOcean(rs.getString("VESSEL_NM_OCEAN"));
		binf016OutputDto.setBuyerCode(rs.getString("IND_BUYER"));
		binf016OutputDto.setConsigneeCode(rs.getString("IND_CONSIGNEE"));
		binf016OutputDto.setTradingTermCode(rs.getString("TRADE_TRM"));
		binf016OutputDto.setCurrencyCode(rs.getString("PAY_CRNCY"));
		binf016OutputDto.setInvoiceAmount(rs.getString("INV_AMT"));
		binf016OutputDto.setFreightAmount(rs.getString("FREIGHT"));
		binf016OutputDto.setInsuranceAmount(rs.getString("INSURANCE"));
		binf016OutputDto.setShippingMark1(rs.getString("MARK1"));
		binf016OutputDto.setShippingMark2(rs.getString("MARK2"));
		binf016OutputDto.setShippingMark3(rs.getString("MARK3"));
		binf016OutputDto.setShippingMark4(rs.getString("MARK4"));
		binf016OutputDto.setShippingMark5(rs.getString("MARK5"));
		binf016OutputDto.setShippingMark6(rs.getString("MARK6"));
		binf016OutputDto.setShippingMark7(rs.getString("MARK7"));
		binf016OutputDto.setShippingMark8(rs.getString("MARK8"));
		binf016OutputDto.setFinalDestination(rs.getString("FINAL_DST"));
		binf016OutputDto.setPaymentTerm(rs.getString("PAY_TERM"));
		binf016OutputDto.setFinalETD(rs.getString("ETD"));
		binf016OutputDto.setFinalETA(rs.getString("ETA"));
		binf016OutputDto.setMeasurement(rs.getString("MEASUREMENT"));
		binf016OutputDto.setGrossWeight(rs.getString("GROSS_WT"));
		binf016OutputDto.setNetWeight(rs.getString("NET_WT"));
		binf016OutputDto.setOrderType(rs.getString("ORD_TYP"));
		binf016OutputDto.setPaymentDueDate(rs.getString("DUE_DT"));
		binf016OutputDto.setTotalNoOfCase(rs.getString("NO_OF_CASES"));
		binf016OutputDto.setTransportMode(rs.getString("LOCAL_TPT_CD"));
		binf016OutputDto.setProductName(rs.getString("PROD_GRP_DESC"));
		binf016OutputDto.setNonCommercialFlag(rs.getString("NON_COMM_FLAG"));
		binf016OutputDto.setInvoicePrivilegeType(rs.getString("INV_PRIVILEGE"));
		binf016OutputDto.setPackingMonth(rs.getString("PKG_MTH"));
		binf016OutputDto.setPortName(rs.getString("DST_PORT_NM"));
		binf016OutputDto.setVoyageNoOcean(rs.getString("VOYAGE_NO_OCEAN"));
		binf016OutputDto.setGoodsDescription(rs.getString("GOODS_DESC"));
		binf016OutputDto.setVesselNameFeeder(rs.getString("VESSEL_NM_FEEDER"));
		binf016OutputDto.setVoyageNoFeeder(rs.getString("VOYAGE_NO_FEEDER"));
		binf016OutputDto.setLotPxP(rs.getString("LOT_PTTRN"));
		binf016OutputDto.setShippingCompanyOcean(rs.getString("SHIPPING_COMP"));
		binf016OutputDto.setTransactionType(rs.getString("TRN_TYP"));

		return binf016OutputDto;
	}

}
