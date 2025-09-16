package com.tpex.repository;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;
import com.tpex.exception.BatchReadFailedException;
import com.tpex.util.ConstantUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomVesselBookingRepository implements PagingAndSortingRepository<WrkPlanMasterUploadBatchInputDto, Object> {

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	public Page<WrkPlanMasterUploadBatchInputDto> getVesselBookingToUploadWorkPlanMaster(UploadWrkPlanMasterFromVesselBookingRequest in, Pageable page) {
		String sqlCount;
		List<WrkPlanMasterUploadBatchInputDto> list;
		MapSqlParameterSource param;
		try {
			StringBuilder mainSql = new StringBuilder("select tb.CONT_DST_CD as contDest "
					+ ", date_format(tb.ETD1, '%d/%m/%Y') as originalEtd "
					+ ", date_format(tb.ETD1, '%d/%m/%Y') as etd1 "
					+ ", date_format(tb.ETA, '%d/%m/%Y') as eta1 "
					+ ", tb.SHP_NM_1 as shipComp "
					+ ", tb.CONT_20 as cont20 "
					+ ", tb.CONT_40 as cont40 "
					+ ", tb.VESSEL1 as vessel1 "
					+ ", tb.BOOK_NO as bookingNo "
					+ ", tb.CB_CD as customBroker "
					+ ", date_format(FN_GET_ISSUE_INV_DATE(tb.VAN_END_DT), '%d/%m/%Y') as issueInvoiceDate "
					+ ", group_concat(tb.CONT_GRP_CD order by tb.CONT_GRP_CD) as renbanCode "
					+ ", tb.FOLDER_NAME as folderName "
					+ "from ( select h.CONT_DST_CD "
					+ ", h.ETD1 as ORIGINAL_ETD "
					+ ", h.ETD1 as ETD1 "
					+ ", h.ETA as ETA "
					+ ", h.SHP_NM_1 , h.CONT_20 "
					+ ", h.CONT_40 , h.VESSEL1 "
					+ ", h.BOOK_NO , h.CB_CD "
					+ ", h.VAN_END_DT, d.CONT_GRP_CD "
					+ ", (select FOLDER_NAME from tb_m_mth_renban_setup st "
					+ "where st.CONT_DST_CD = h.CONT_DST_CD  "
					+ "and st.EFF_FROM_DT <= h.ETD1 "
					+ "and st.EFF_TO_DT >= h.ETD1 "
					+ "and st.CONT_GRP_CD = d.CONT_GRP_CD "
					+ "order by d.CONT_GRP_CD "
					+ "limit 1 ) as FOLDER_NAME " // prevent multiple record
					+ "from tb_r_mth_renban_booking_h h "
					+ "inner join tb_r_mth_renban_booking_d d on h.GROUP_ID = d.GROUP_ID "
					+ "and h.SHP_NM_1 = d.SHP_NM_1 "
					+ "and h.ETD1 = d.ETD1 "
					+ "and h.CONT_VAN_MTH = d.CONT_VAN_MTH "
					+ "and h.CONT_DST_CD = d.CONT_DST_CD "
					+ "where h.CONT_VAN_MTH = :vanningMonth "
					+ "and (h.BOOK_NO is not null and TRIM(h.BOOK_NO) != '') ");
			appendWhereIfHas(mainSql, " and h.CONT_DST_CD = :destinationCode ", in.getDestinationCode());
			appendWhereIfHas(mainSql, " and h.ETD1 >= STR_TO_DATE(:etdFrom,'%d/%m/%Y') ", in.getEtdFrom());
			appendWhereIfHas(mainSql, " and h.ETD1 <= STR_TO_DATE(:etdTo,'%d/%m/%Y') ", in.getEtdTo());
			appendWhereIfHas(mainSql, " and h.SHP_NM_1 = :shippingCompanyCode ", in.getShippingCompanyCode());
			mainSql.append(") tb "
					+ "group by tb.CONT_DST_CD, tb.ETD1, tb.ETA, tb.SHP_NM_1 "
					+ ", tb.CONT_20, tb.CONT_40, tb.VESSEL1, tb.BOOK_NO "
					+ ", tb.VAN_END_DT, tb.CB_CD, tb.FOLDER_NAME ");
			
			//store main SQL before append limit
			sqlCount = mainSql.toString();
			
			mainSql.append(" LIMIT "+ page.getPageSize() +" OFFSET "+ page.getOffset());
			
			param = new MapSqlParameterSource().addValue("vanningMonth", in.getVanningMonth());
			addParamIfHas(param, "destinationCode", in.getDestinationCode());
			addParamIfHas(param, "etdFrom", in.getEtdFrom());
			addParamIfHas(param, "etdTo", in.getEtdTo());
			addParamIfHas(param, "shippingCompanyCode", in.getShippingCompanyCode());
			
			list = namedParameterJdbcTemplate.query(mainSql.toString(), param,
					new BeanPropertyRowMapper<>(WrkPlanMasterUploadBatchInputDto.class));
		} catch (Exception e) {
			throw new BatchReadFailedException(ConstantUtils.ERR_TO_READ_MSG);
		}
		
		return new PageImpl<>(list, page, getCount(sqlCount, param));
	}
	
	private Integer getCount(String sql, MapSqlParameterSource param) {
		StringBuilder sqlCount = new StringBuilder();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" ) cnt ");
		
		Integer count = namedParameterJdbcTemplate.queryForObject(sqlCount.toString(), param, Integer.class);
		return count == null ? 0 : count;
	}
	
	private void appendWhereIfHas(StringBuilder mainSql, String appendWhere, Object value) {
		if (ObjectUtils.isNotEmpty(value))
			mainSql.append(appendWhere);
	}
	
	private void addParamIfHas(MapSqlParameterSource param, String name, Object value) {
		if (ObjectUtils.isNotEmpty(value))
			param.addValue(name, value);
	}

	// below section be ignored for implementation, used for ItemReader only
	@Override
	public <S extends WrkPlanMasterUploadBatchInputDto> S save(S entity) {
		return null;
	}

	@Override
	public <S extends WrkPlanMasterUploadBatchInputDto> Iterable<S> saveAll(Iterable<S> entities) {
		return null;
	}

	@Override
	public Optional<WrkPlanMasterUploadBatchInputDto> findById(Object id) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(Object id) {
		return false;
	}

	@Override
	public Iterable<WrkPlanMasterUploadBatchInputDto> findAll() {
		return null;
	}

	@Override
	public Iterable<WrkPlanMasterUploadBatchInputDto> findAllById(Iterable<Object> ids) {
		return null;
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(Object id) {
		// ignore implement, use for ItemReader only
	}

	@Override
	public void delete(WrkPlanMasterUploadBatchInputDto entity) {
		// ignore implement, use for ItemReader only
	}

	@Override
	public void deleteAllById(Iterable<? extends Object> ids) {
		// ignore implement, use for ItemReader only
	}

	@Override
	public void deleteAll(Iterable<? extends WrkPlanMasterUploadBatchInputDto> entities) {
		// ignore implement, use for ItemReader only
	}

	@Override
	public void deleteAll() {
		// ignore implement, use for ItemReader only
	}

	@Override
	public Iterable<WrkPlanMasterUploadBatchInputDto> findAll(Sort sort) {
		return null;
	}

	@Override
	public Page<WrkPlanMasterUploadBatchInputDto> findAll(Pageable pageable) {
		return null;
	}
	
}
