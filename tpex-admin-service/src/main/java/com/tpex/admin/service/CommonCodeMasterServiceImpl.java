package com.tpex.admin.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.admin.dto.BuyerMasterDTO;
import com.tpex.admin.dto.CodeMasterRequestDTO;
import com.tpex.admin.dto.CurrencyMasterDTO;
import com.tpex.admin.dto.FinalDestinationMasterResponseDto;
import com.tpex.admin.entity.BuyerMasterEntity;
import com.tpex.admin.entity.CurrencyMasterEntity;
import com.tpex.admin.entity.OemPmntTermMstEntity;
import com.tpex.admin.exception.DuplicateRecordException;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.exception.MyResourceNotFoundException;
import com.tpex.admin.exception.RecordExceedException;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.repository.BuyerMasterRepository;
import com.tpex.admin.repository.CarFamilyMastRepository;
import com.tpex.admin.repository.CountryMasterRepository;
import com.tpex.admin.repository.CurrencyMasterRepository;
import com.tpex.admin.repository.FinalDestRepository;
import com.tpex.admin.repository.InsProdGrpMstRepository;
import com.tpex.admin.repository.OemPmntTermMstRepository;
import com.tpex.admin.repository.OemPortMstRepository;
import com.tpex.admin.repository.ShippingCompanyMaintainanceRepository;
import com.tpex.admin.util.ConstantUtils;

/**
 * The Class CommonCodeMasterServiceImpl.
 */
@Service
public class CommonCodeMasterServiceImpl implements CommonCodeMasterService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonCodeMasterServiceImpl.class);

	@Autowired
	EntityManager entityManager;

	@Autowired
	BuyerMasterRepository buyerMasterRepository;

	@Autowired
	CurrencyMasterRepository currencyMasterRepository;

	@Autowired
	CountryMasterRepository countryMasterRepository;

	@Autowired
	CarFamilyMastRepository carFamilyMastRepository;

	@Autowired
	OemPortMstRepository oemPortMstRepository;

	@Autowired
	FinalDestRepository finalDestRepository;

	@Autowired
	InsProdGrpMstRepository insProdGrpMstRepository;

	@Autowired
	ShippingCompanyMaintainanceRepository shippingCompanyMaintainanceRepository;

	@Autowired
	OemPmntTermMstRepository oemPmntTermMstRepository;

	/**
	 * Code master data.
	 *
	 * @param columnsModel the columns model
	 * @return the list
	 */
	@Override
	public List<Map<String, String>> codeMasterData(ColumnsDTO columnsModel, String cmpCd) {
		
		List<String> listOfColumnNames = columnsModel.getColumns().stream().map(s -> s.getId())
				.collect(Collectors.toList());
		String joiningString3 = listOfColumnNames.stream().collect(Collectors.joining(",", "", ""));
		List<Object[]> listOfObject = execute(joiningString3, columnsModel.getTableName(), columnsModel.getOrderBy(),
				cmpCd);

		List<Map<String, String>> list = new ArrayList<>();
		for (Object[] ob : listOfObject) {
			Map<String, String> map = new HashMap<>();
			for (int i = 0; i < ob.length; i++) {
				map.put(listOfColumnNames.get(i), String.valueOf(ob[i]).replace("null", ""));
			}
			list.add(map);
		}

		return list;
	}

	/**
	 * Execute.
	 *
	 * @param columnName the column name
	 * @param tabelName  the tabel name
	 * @return the list
	 */
	public List<Object[]> execute(String columnName, String tabelName, String orderBy, String cmpCd) {
		StringBuilder str = new StringBuilder("Select ").append(columnName).append(" from ").append(tabelName)
				.append(" where cmp_cd ='").append(cmpCd).append("'");
			if (orderBy != null && !orderBy.isBlank()) {
			str.append(" order by ").append(orderBy);
		}

		Query q = entityManager.createNativeQuery(str.toString());

		List<Object[]> result = q.getResultList();
		entityManager.close();
		return result;
	}

	/**
	 * Save code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 */
	@Transactional
	@Override
	public void saveCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO) {
		if (codeMasterRequestDTO != null && codeMasterRequestDTO.getData() != null) {
			List<Map<String, String>> data = codeMasterRequestDTO.getData();

			int i = 0;
			int countOfCountryName;
			int countOfCountryCode;
			int countOfDuplicateCountryName;

			if (data.size() > 10) {
				throw new RecordExceedException(ConstantUtils.MORTHANTENRECORD);
			}

			for (Map<String, String> dataOfList : data) {

				countOfCountryName = countryMasterRepository.getCountOfCountryName(
						dataOfList.get(ConstantUtils.COUNTRYNAME), dataOfList.get(ConstantUtils.COUNTRYCODE));
				countOfCountryCode = countryMasterRepository.getCountOfCountryCode(
						dataOfList.get(ConstantUtils.COUNTRYCODE), dataOfList.get(ConstantUtils.COUNTRYNAME));
				countOfDuplicateCountryName = countryMasterRepository.getCountOfCountryNameAndCode(
						dataOfList.get(ConstantUtils.COUNTRYNAME), dataOfList.get(ConstantUtils.COUNTRYCODE));

				validateCountryCodeAndName(countOfCountryName, countOfCountryCode, countOfDuplicateCountryName,
						dataOfList);

				if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD) != null) {
					duplicateCheck(codeMasterRequestDTO, i);
				} else if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.DST_CD) != null) {
					duplicateDestinationCheck(codeMasterRequestDTO, i);
				} else if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.PROD_GRP_CD) != null) {
					duplicateProductCheck(codeMasterRequestDTO, i);
				}
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ");
				sb.append(codeMasterRequestDTO.getTableName());
				sb.append(" (");
				sb.append(dataOfList.entrySet().stream().map(Entry::getKey).collect(Collectors.joining(",")));
				sb.append(") VALUES ('");
				sb.append(dataOfList.entrySet().stream().map(e -> e.getValue().toUpperCase())
						.collect(Collectors.joining("','")));
				sb.append("' )");
				Query q = entityManager.createNativeQuery(
						sb.toString().replace(ConstantUtils.SQL_SYSDATE, ConstantUtils.SYSDATE_FUNCTION));

				i++;
				if (q.executeUpdate() == 0) {
					throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3025);
				}

				entityManager.close();
			}
		}

	}

	private void duplicateCheck(CodeMasterRequestDTO codeMasterRequestDTO, int i) {
		StringBuilder placeHolder = new StringBuilder("");
		boolean exist = false;
		if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD) != null) {
			if (codeMasterRequestDTO.getTableName() != null
					&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("TB_M_PORT")) {
				exist = oemPortMstRepository.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD));
				placeHolder.append(ConstantUtils.DUPLICATERECORDPORT);
			}
			if (codeMasterRequestDTO.getTableName() != null
					&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("tb_m_carfamily")) {
				exist = carFamilyMastRepository.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD));
				placeHolder.append(ConstantUtils.DUPLICATERECORDCARMASTER);
			}
			if (codeMasterRequestDTO.getTableName() != null
					&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("tb_m_ship_cmp")) {
				exist = shippingCompanyMaintainanceRepository
						.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD));
			}
			if (codeMasterRequestDTO.getTableName() != null
					&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("tb_m_currency")) {
				exist = currencyMasterRepository
						.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD));
				placeHolder.append(ConstantUtils.DUPLICATERECORDC);
			}
			if (exist) {
				throw new DuplicateRecordException(ConstantUtils.DUPLICATERECORDFORSHIPPING + placeHolder
						+ codeMasterRequestDTO.getData().get(i).get(ConstantUtils.CD));
			}

		}
	}

	private void duplicateProductCheck(CodeMasterRequestDTO codeMasterRequestDTO, int i) {
		if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.PROD_GRP_CD) != null
				&& codeMasterRequestDTO.getTableName() != null
				&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("TB_M_PROD_GRP")) {
			boolean existCdProdGrp = insProdGrpMstRepository
					.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.PROD_GRP_CD));
			if (existCdProdGrp) {
				throw new DuplicateRecordException(ConstantUtils.DUPLICATE_REC_FOR_PRODUCT_GRP_CD
						+ codeMasterRequestDTO.getData().get(i).get(ConstantUtils.PROD_GRP_CD));
			}
		}

	}

	private void duplicateDestinationCheck(CodeMasterRequestDTO codeMasterRequestDTO, int i) {
		if (codeMasterRequestDTO.getData().get(i).get(ConstantUtils.DST_CD) != null
				&& codeMasterRequestDTO.getTableName() != null
				&& codeMasterRequestDTO.getTableName().equalsIgnoreCase("TB_M_FINAL_DESTINATION")) {
			boolean existFinalDestination = finalDestRepository
					.existsById(codeMasterRequestDTO.getData().get(i).get(ConstantUtils.DST_CD));
			if (existFinalDestination) {
				throw new DuplicateRecordException(ConstantUtils.DUPLICATERECORDFINALDESTINATION
						+ codeMasterRequestDTO.getData().get(i).get(ConstantUtils.DST_CD));
			}
		}
	}

	private void validateCountryCodeAndName(int countOfCountryName, int countOfCountryCode,
			int countOfDuplicateCountryName, Map<String, String> dataOfList) {
		if (countOfCountryName > 0) {

			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.KEYCOLUMNS, dataOfList.get(ConstantUtils.COUNTRYNAME));
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3008, errorMessageParams);
		} else if (countOfCountryCode > 0) {

			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.KEYCOLUMNS, dataOfList.get(ConstantUtils.COUNTRYCODE));
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3008, errorMessageParams);
		} else if (countOfDuplicateCountryName > 0) {

			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.KEYCOLUMNS, dataOfList.get(ConstantUtils.COUNTRYCODE));
			errorMessageParams.put("keyColumns2", dataOfList.get(ConstantUtils.COUNTRYNAME));
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3008, errorMessageParams);
		}
	}

	/**
	 * Delete code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 */
	@Transactional
	@Override
	public void deleteCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO) {

		List<Map<String, String>> listOfData = codeMasterRequestDTO.getData();
		for (Map<String, String> map : listOfData) {
			String pkValue = map.get(codeMasterRequestDTO.getPrimaryKey());
			StringBuilder sb = new StringBuilder();
			sb.append("DELETE FROM ");
			sb.append(codeMasterRequestDTO.getTableName());
			sb.append(ConstantUtils.SQL_WHERE);
			sb.append(codeMasterRequestDTO.getPrimaryKey());
			sb.append("='" + pkValue + "'");
			Query q = entityManager.createNativeQuery(sb.toString());
			if (q.executeUpdate() == 0) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3023);
			}

			entityManager.close();
		}
	}

	/**
	 * Update code master.
	 *
	 * @param codeMasterRequestDTO the code master request DTO
	 */
	@Transactional
	@Override
	public void updateCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO) {
		List<Map<String, String>> listOfData = codeMasterRequestDTO.getData();
		for (Map<String, String> map : listOfData) {
			StringBuilder sb2 = new StringBuilder();
			Set<String> keys = map.keySet();
			for (String key : keys) {
				if (!key.equals(ConstantUtils.DST_CD_PRIMARY)) {
					sb2.append(key + " = '" + map.get(key));
					sb2.append("',");
				}

			}
			String keyvalue = sb2.toString().substring(0, sb2.toString().length() - 1)
					.replace(ConstantUtils.SQL_SYSDATE, ConstantUtils.SYSDATE_FUNCTION);
			String pkValue = map.get(codeMasterRequestDTO.getPrimaryKey() + "_PRIMARY");
			StringBuilder sb = new StringBuilder();
			sb.append(ConstantUtils.SQL_UPDATE);
			sb.append(" " + codeMasterRequestDTO.getTableName());
			sb.append(" " + ConstantUtils.SQL_SET + " ");
			sb.append(keyvalue);
			sb.append(ConstantUtils.SQL_WHERE + " ");
			sb.append(codeMasterRequestDTO.getPrimaryKey());
			sb.append("='" + pkValue + "'");
			Query q = entityManager.createNativeQuery(sb.toString());
			if (q.executeUpdate() == 0) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3025);
			}

			entityManager.close();
		}

	}

	public FinalDestinationMasterResponseDto finalDestinationDtls(String cmpCd) {

		FinalDestinationMasterResponseDto finalDestinationMasterResponseDto = new FinalDestinationMasterResponseDto();

		List<BuyerMasterEntity> buyerMasterEntityList = buyerMasterRepository.findAllBycmpCdOrderByBuyerCodeAsc(cmpCd);
		List<BuyerMasterDTO> buyerMasterDtoList = new ArrayList<>();
		for (BuyerMasterEntity buyerMasterEntity : buyerMasterEntityList) {
			BuyerMasterDTO buyerMasterDto = new BuyerMasterDTO();
			buyerMasterDto.setId(buyerMasterEntity.getBuyerCode());
			buyerMasterDto.setName(buyerMasterEntity.getBuyerCode() + "-" + buyerMasterEntity.getBuyerName());
			buyerMasterDtoList.add(buyerMasterDto);
		}
		finalDestinationMasterResponseDto.setBuyer(buyerMasterDtoList);

		List<CurrencyMasterEntity> currencyMasterEntityList = currencyMasterRepository
				.findAllBycmpCdOrderByCurrencyCodeAsc(cmpCd);
		List<CurrencyMasterDTO> currencyMasterDtoList = new ArrayList<>();
		for (CurrencyMasterEntity currencyMasterEntity : currencyMasterEntityList) {
			CurrencyMasterDTO currencyMasterDto = new CurrencyMasterDTO();
			currencyMasterDto.setId(currencyMasterEntity.getCurrencyCode());
			currencyMasterDto
					.setName(currencyMasterEntity.getCurrencyCode() + "-" + currencyMasterEntity.getDescription());
			currencyMasterDtoList.add(currencyMasterDto);
		}
		finalDestinationMasterResponseDto.setCurrency(currencyMasterDtoList);

		return finalDestinationMasterResponseDto;
	}

	/**
	 * Code master data Final Destination.
	 *
	 * @param columnsModel the columns model
	 * @return the list
	 */
	@Override
	public List<Map<String, String>> codeMasterDataFdm(ColumnsDTO columnsModel) {
		List<String> listOfColumnNames = columnsModel.getColumns().stream().map(s -> s.getId())
				.collect(Collectors.toList());
		String joiningString3 = listOfColumnNames.stream().collect(Collectors.joining(",", "", ""));
		List<Object[]> listOfObject = executeFdm(joiningString3, columnsModel.getTableName());

		List<Map<String, String>> list = new ArrayList<>();
		for (Object[] ob : listOfObject) {
			Map<String, String> map = new HashMap<>();
			for (int i = 0; i < ob.length; i++) {
				map.put(listOfColumnNames.get(i), String.valueOf(ob[i]).replace("null", ""));
			}
			list.add(map);
		}

		return list;
	}

	/**
	 * FdmExecute with where condition
	 *
	 * @param columnName the column name
	 * @param tabelName  the tabel name
	 * @return the list
	 */
	public List<Object[]> executeFdm(String columnName, String tabelName) {
		Query queryFinalDstMaster = entityManager.createNativeQuery(
				"select DST_CD ,DST_NM ,CONCAT(b.CD,'-',NM) ,CONCAT(c.CD,'-',c.description) from tb_m_final_destination a ,TB_M_BUYER_SELLER_DETAIL b ,TB_M_CURRENCY c where a.BUY_CD = b.CD AND  a.CURR_CD = c.CD");
		List<Object[]> result = queryFinalDstMaster.getResultList();
		entityManager.close();
		return result;
	}

	@Transactional
	@Override
	public Map<String, Object> savePaymentTermMaster(CodeMasterRequestDTO codeMasterRequestDTO, boolean isSave) {

		LOGGER.info("Calling SavePaymentTermMaster");

		List<Map<String, String>> data = codeMasterRequestDTO.getData();

		List<OemPmntTermMstEntity> oemPmntTermMstEntityRegular = null;

		List<OemPmntTermMstEntity> oemPmntTermMstEntityCpoSpo = null;

		List<String> listRegular = data.stream().filter(a -> a.get(ConstantUtils.REGULAR).equalsIgnoreCase("Y"))
				.map(x -> x.get(ConstantUtils.CD)).collect(Collectors.toList());
		if (listRegular.size() > 1) {
			/* Error message for newly added Multiple records */
			LOGGER.info("SavePaymentTermMaster Filed because of multiple select of REGULAR");
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.PAYMENTTERM_CODE, String.join(",", listRegular));
			throw new MyResourceNotFoundException(ConstantUtils.ERR_AD_2013, errorMessageParams);

		} else {
			oemPmntTermMstEntityRegular = oemPmntTermMstRepository.findByRegular("Y");
		}

		List<String> listCpoSpo = data.stream().filter(a -> a.get(ConstantUtils.CPO_SPO).equalsIgnoreCase("Y"))
				.map(x -> x.get(ConstantUtils.CD)).collect(Collectors.toList());
		if (listCpoSpo.size() > 1) {
			/* Error message for newly added Multiple records */
			LOGGER.info("SavePaymentTermMaster Filed because of multiple select of CPOSPO");
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.PAYMENTTERM_CODE, String.join(",", listCpoSpo));
			throw new MyResourceNotFoundException(ConstantUtils.ERR_AD_2014, errorMessageParams);
		} else {
			oemPmntTermMstEntityCpoSpo = oemPmntTermMstRepository.findByCpoSpo("Y");
		}

		return getResponseMsg(codeMasterRequestDTO, isSave, data, oemPmntTermMstEntityRegular,
				oemPmntTermMstEntityCpoSpo);
	}

	private Map<String, Object> getResponseMsg(CodeMasterRequestDTO codeMasterRequestDTO, boolean isSave,
			List<Map<String, String>> data, List<OemPmntTermMstEntity> oemPmntTermMstEntityRegular,
			List<OemPmntTermMstEntity> oemPmntTermMstEntityCpoSpo) {

		Map<String, Object> responseMessageParams = new HashMap<>();

		for (Map<String, String> dataOfList : data) {

			if (isSave) {
				boolean exist = oemPmntTermMstRepository.existsById(dataOfList.get((ConstantUtils.CD)));
				if (exist) {
					throw new DuplicateRecordException(
							ConstantUtils.DUPLICATERECORDPAYMENTTERM + " " + dataOfList.get((ConstantUtils.CD)));
				}
			}

			emptyCheckValidation(oemPmntTermMstEntityRegular, oemPmntTermMstEntityCpoSpo, dataOfList);

			responseMessageParams = getresponseMessageParams(oemPmntTermMstEntityRegular, oemPmntTermMstEntityCpoSpo,
					dataOfList);

			if (isSave) {
				savePaymentTermMasterEntity(codeMasterRequestDTO, dataOfList);
			} else {
				updatePaymentTermMasterEntity(codeMasterRequestDTO, dataOfList);
			}

			LOGGER.info("SavePaymentTermMaster Success");
		}

		return responseMessageParams;
	}

	private Map<String, Object> getresponseMessageParams(List<OemPmntTermMstEntity> oemPmntTermMstEntityRegular,
			List<OemPmntTermMstEntity> oemPmntTermMstEntityCpoSpo, Map<String, String> dataOfList) {

		Map<String, Object> responseMessageParams = new HashMap<>();

		if (dataOfList.get(ConstantUtils.REGULAR).equalsIgnoreCase("Y")) {
			updatePaymentTermMasterRegular();
			if (!oemPmntTermMstEntityRegular.isEmpty()
					&& !oemPmntTermMstEntityRegular.get(0).getPtCd().equalsIgnoreCase(dataOfList.get(ConstantUtils.CD)))
				responseMessageParams.put(ConstantUtils.INFO_CM_3014, dataOfList.get(ConstantUtils.CD).concat(",")
						.concat(oemPmntTermMstEntityRegular.get(0).getPtCd()));
		}

		if (dataOfList.get(ConstantUtils.CPO_SPO).equalsIgnoreCase("Y")) {
			updatePaymentTermMasterCpoSpo();
			if (!oemPmntTermMstEntityCpoSpo.isEmpty()
					&& !oemPmntTermMstEntityCpoSpo.get(0).getPtCd().equalsIgnoreCase(dataOfList.get(ConstantUtils.CD)))
				responseMessageParams.put(ConstantUtils.INFO_CM_3015, dataOfList.get(ConstantUtils.CD).concat(",")
						.concat(oemPmntTermMstEntityCpoSpo.get(0).getPtCd()));
		}

		return responseMessageParams;
	}

	private void emptyCheckValidation(List<OemPmntTermMstEntity> oemPmntTermMstEntityRegular,
			List<OemPmntTermMstEntity> oemPmntTermMstEntityCpoSpo, Map<String, String> dataOfList) {
		if (oemPmntTermMstEntityRegular.isEmpty() && dataOfList.get(ConstantUtils.REGULAR).equalsIgnoreCase("N")) {
			throw new MyResourceNotFoundException(ConstantUtils.PAYMENTTERINFOMMSG + " " + ConstantUtils.REGULARINFO);
		}

		if (oemPmntTermMstEntityCpoSpo.isEmpty() && dataOfList.get(ConstantUtils.CPO_SPO).equalsIgnoreCase("N")) {
			throw new MyResourceNotFoundException(ConstantUtils.PAYMENTTERINFOMMSG + " " + ConstantUtils.CPO_SPO);
		}
	}

	private void updatePaymentTermMasterEntity(CodeMasterRequestDTO codeMasterRequestDTO, Map<String, String> map) {
		StringBuilder sb2 = new StringBuilder();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			sb2.append(key + " = '" + map.get(key));
			sb2.append("',");
		}
		String keyvalue = sb2.toString().substring(0, sb2.toString().length() - 1).replace(ConstantUtils.SQL_SYSDATE,
				ConstantUtils.SYSDATE_FUNCTION);
		String pkValue = map.get(codeMasterRequestDTO.getPrimaryKey());
		StringBuilder sb = new StringBuilder();
		sb.append(ConstantUtils.SQL_UPDATE);
		sb.append(codeMasterRequestDTO.getTableName());
		sb.append(ConstantUtils.SQL_SET);
		sb.append(keyvalue);
		sb.append(ConstantUtils.SQL_WHERE);
		sb.append(codeMasterRequestDTO.getPrimaryKey());
		sb.append("='" + pkValue + "'");
		Query q = entityManager.createNativeQuery(sb.toString());
		if (q.executeUpdate() == 0) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3025);
		}

		entityManager.close();

	}

	@Transactional
	public void savePaymentTermMasterEntity(CodeMasterRequestDTO codeMasterRequestDTO, Map<String, String> dataOfList) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(codeMasterRequestDTO.getTableName());
		sb.append(" (");
		sb.append(dataOfList.entrySet().stream().map(Entry::getKey).collect(Collectors.joining(",")));
		sb.append(") VALUES ('");
		sb.append(dataOfList.entrySet().stream().map(Entry::getValue).collect(Collectors.joining("','")));
		sb.append("' )");
		Query q = entityManager
				.createNativeQuery(sb.toString().replace(ConstantUtils.SQL_SYSDATE, ConstantUtils.SYSDATE_FUNCTION));
		q.executeUpdate();
		entityManager.close();
	}

	@Transactional
	public void updatePaymentTermMasterRegular() {
		StringBuilder sb = updatePaymentTermMasterQuery("REGULAR");
		Query q = entityManager.createNativeQuery(sb.toString());
		q.executeUpdate();
		entityManager.close();
	}

	@Transactional
	public void updatePaymentTermMasterCpoSpo() {
		StringBuilder sb = updatePaymentTermMasterQuery("CPO_SPO");
		Query q = entityManager.createNativeQuery(sb.toString());
		q.executeUpdate();
		entityManager.close();
	}

	private StringBuilder updatePaymentTermMasterQuery(String regularOrCpoSpo) {
		StringBuilder sb = new StringBuilder();
		sb.append(ConstantUtils.SQL_UPDATE);
		sb.append("tb_m_payment_term");
		sb.append(ConstantUtils.SQL_SET);
		sb.append(regularOrCpoSpo + " = '" + "N'");
		sb.append(ConstantUtils.SQL_WHERE);
		sb.append(regularOrCpoSpo + " = '" + "Y'");
		return sb;
	}
}
