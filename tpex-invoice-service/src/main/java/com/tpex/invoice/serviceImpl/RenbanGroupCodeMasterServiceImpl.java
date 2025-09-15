package com.tpex.invoice.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.NoemRenbanBookDtlEntity;
import com.tpex.entity.NoemRenbanSetupMstEntity;
import com.tpex.entity.NoemRenbanSetupMstIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.GroupCodeDetailsDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterRequestDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseFinalDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterSaveRequestDto;
import com.tpex.invoice.dto.RenbancodeMasterDeleteDto;
import com.tpex.invoice.service.RenbanGroupCodeMasterService;
import com.tpex.repository.NoemRenbanBookDtlRepository;
import com.tpex.repository.NoemRenbanSetupMstRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@Service
public class RenbanGroupCodeMasterServiceImpl implements RenbanGroupCodeMasterService {

	@Autowired
	private NoemRenbanSetupMstRepository noemRenbanSetupMstRepository;

	@Autowired
	private NoemRenbanBookDtlRepository noemRenbanBookDtlRepository;

	@Override
	public RenbanGroupCodeMasterResponseFinalDto getRenbanGroupCodeMasterDetails(
			@Valid RenbanGroupCodeMasterRequestDto request) throws ParseException {

		RenbanGroupCodeMasterResponseFinalDto finalResponse = new RenbanGroupCodeMasterResponseFinalDto();
		Set<RenbanGroupCodeMasterResponseDto> renbanGroupCodeMasterResponseDtoList = new HashSet<>();

		String[] splitting = request.getFinalDestCodeAndName().split("-");
		String destCode = splitting[0];

		List<NoemRenbanSetupMstEntity> renbanCodeList = noemRenbanSetupMstRepository
				.findByIdContDstCdOrderByIdContGrpCdAsc(destCode);
		
		List<NoemRenbanSetupMstEntity> renbanGroupCodeList = noemRenbanSetupMstRepository.findAll();

		List<CommonMultiSelectDropdownDto> renbanCodes = renbanGroupCodeList.stream()
				.map(u -> new CommonMultiSelectDropdownDto(u.getId().getContGrpCd(), u.getId().getContGrpCd()))
				.distinct().collect(Collectors.toList());

		List<CommonMultiSelectDropdownDto> numericRenbanCodeList = renbanCodes.stream()
				.filter(t -> t.getValue().matches("\\d")).collect(Collectors.toList());

		List<CommonMultiSelectDropdownDto> alphabetRenbanCodeList = renbanCodes.stream()
				.filter(t -> t.getValue().matches("[a-zA-Z]+")).collect(Collectors.toList());
		
		alphabetRenbanCodeList.addAll(numericRenbanCodeList);

		finalResponse.setRenbanGroupCodeList(alphabetRenbanCodeList);

		for (NoemRenbanSetupMstEntity dataBaseEntity : renbanCodeList) {

			RenbanGroupCodeMasterResponseDto renbanGroupCodeMasterResponseDto = new RenbanGroupCodeMasterResponseDto();

			renbanGroupCodeMasterResponseDto.setContDstCd(dataBaseEntity.getId().getContDstCd());
			String effFrom = dataBaseEntity.getId().getEffFromDt().toString();
			String effTo = dataBaseEntity.getEffToDt().toString();

			SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
			SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);

			try {
				renbanGroupCodeMasterResponseDto.setEtdFromDate(outPutFormat.format(inputFormat.parse(effFrom)));
				renbanGroupCodeMasterResponseDto.setEtdToDate(outPutFormat.format(inputFormat.parse(effTo)));
			} catch (ParseException e1) {
				throw new ParseException(e1.getMessage(), e1.getErrorOffset());
			}

			Map<String, List<GroupCodeDetailsDto>> collect = renbanCodeList.stream()
					.filter(e -> dataBaseEntity.getId().getEffFromDt().equals(e.getId().getEffFromDt())).collect(
							Collectors.groupingBy(e -> e.getId().getGroupId(),
									Collectors.mapping(
											e -> new GroupCodeDetailsDto(e.getId().getGroupId(),
													e.getId().getContGrpCd(), e.getFolderName()),
											Collectors.toList())));

			renbanGroupCodeMasterResponseDto.setGoupdIdDetails(collect);

			renbanGroupCodeMasterResponseDtoList.add(renbanGroupCodeMasterResponseDto);

		}

		List<RenbanGroupCodeMasterResponseDto> renbanGroupCodeMasterResponseDtoList1 = new ArrayList<>(
				renbanGroupCodeMasterResponseDtoList);

		DateTimeFormatter formate = DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATE_FORMATE);

		Collections.sort(renbanGroupCodeMasterResponseDtoList1, (e1, e2) -> LocalDate
				.parse(e1.getEtdFromDate(), formate).compareTo(LocalDate.parse(e2.getEtdFromDate(), formate)));
		finalResponse.setRenbanData(renbanGroupCodeMasterResponseDtoList1);
		return finalResponse;

	}

	@Transactional
	@Override
	public void deleteRenbanGroupCodeMasterDetails(List<RenbancodeMasterDeleteDto> deleteRequest)
			throws ParseException {

		for (RenbancodeMasterDeleteDto request : deleteRequest) {

			String[] splitting = request.getContDstCd().split("-");

			String destCode = splitting[0];

			SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
			SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);

			String effectiveFrom = outPutFormat.format(inputFormat.parse(request.getEffectiveFromDt()));
			String effectiveTo = outPutFormat.format(inputFormat.parse(request.getEffctiveToDt()));

			Date effFrom = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,
					effectiveFrom);
			Date effTo = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,
					effectiveTo);

			List<NoemRenbanBookDtlEntity> renbanCodes = noemRenbanBookDtlRepository
					.findByIdContDestCdAndIdEtd1(destCode, effFrom);

			List<String> existingRenbanCodeList = renbanCodes.stream()
					.filter(e -> request.getContGrpCd().contains(e.getId().getContGrpCode()))
					.map(e -> e.getId().getContGrpCode()).collect(Collectors.toList());

			if (existingRenbanCodeList.isEmpty()) {

				try {

					if (!noemRenbanSetupMstRepository.existsByIdContDstCdAndIdEffFromDtAndEffToDt(destCode, effFrom,
							effTo)) {
						throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1030);
					}
					noemRenbanSetupMstRepository.deleteByIdContDstCdAndIdEffFromDtAndEffToDt(destCode, effFrom, effTo);
				} catch (Exception e) {
					throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1030);
				}

			} else {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1031);
			}
		}

	}

	@Override
	@Transactional
	public boolean saveRenbanCodeMaster(
			List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList) throws ParseException {

		List<NoemRenbanSetupMstEntity> noemRenbanSetupMstEntities = new ArrayList<>();

		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMAT_FOR_RANBAN);
		SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
		for (RenbanGroupCodeMasterSaveRequestDto renbanGroupCodeMasterSaveRequestDto : renbanGroupCodeMasterSaveRequestDtoList) {

			List<NoemRenbanSetupMstEntity> noemRenbanSetupMstEntityList = new ArrayList<>();

			for (GroupCodeDetailsDto groupCodeDetailsDto : renbanGroupCodeMasterSaveRequestDto.getGoupdIdDetails()) {
				NoemRenbanSetupMstEntity noemRenbanSetupMstEntity = new NoemRenbanSetupMstEntity();
				NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity = new NoemRenbanSetupMstIdEntity();
				String effectiveFrom = outPutFormat
						.format(inputFormat.parse(renbanGroupCodeMasterSaveRequestDto.getEtdFromDate()));

				java.sql.Date effFrom = DateUtil
						.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE, effectiveFrom);
				String effectiveTo = outPutFormat
						.format(inputFormat.parse(renbanGroupCodeMasterSaveRequestDto.getEtdToDate()));

				java.sql.Date effTo = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,
						effectiveTo);

				noemRenbanSetupMstIdEntity.setContDstCd(renbanGroupCodeMasterSaveRequestDto.getContDstCd());
				noemRenbanSetupMstIdEntity.setContGrpCd(groupCodeDetailsDto.getRenbanGroupCode());
				noemRenbanSetupMstIdEntity.setEffFromDt(effFrom);
				noemRenbanSetupMstIdEntity.setGroupId(groupCodeDetailsDto.getGroupId());
				if (!groupCodeDetailsDto.getFolderName().isEmpty() || groupCodeDetailsDto.getFolderName() != null) {
					noemRenbanSetupMstEntity.setFolderName(groupCodeDetailsDto.getFolderName());
				}
				noemRenbanSetupMstEntity.setUpdatedBy(renbanGroupCodeMasterSaveRequestDto.getUpdateBy());
				noemRenbanSetupMstEntity.setEffToDt(effTo);
				noemRenbanSetupMstEntity.setUpdatedDate(java.sql.Date.valueOf(LocalDate.now()));

				noemRenbanSetupMstEntity.setId(noemRenbanSetupMstIdEntity);

				noemRenbanSetupMstEntityList.add(noemRenbanSetupMstEntity);

			}

			noemRenbanSetupMstEntities.addAll(noemRenbanSetupMstEntityList);

		}
		try {
			// Save all records
			return !noemRenbanSetupMstRepository.saveAll(noemRenbanSetupMstEntities).isEmpty();
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	@Transactional
	public void updateRenbanCodeMaster(
			List<RenbanGroupCodeMasterSaveRequestDto> renbanGroupCodeMasterSaveRequestDtoList) throws ParseException {

		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);
		SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);

		for (RenbanGroupCodeMasterSaveRequestDto renbanGroupCodeMasterSaveRequestDto : renbanGroupCodeMasterSaveRequestDtoList) {

			for (GroupCodeDetailsDto groupCodeDetailsDto : renbanGroupCodeMasterSaveRequestDto.getGoupdIdDetails()) {
				String effectiveFrom = outPutFormat
						.format(inputFormat.parse(renbanGroupCodeMasterSaveRequestDto.getEtdFromDate()));

				Date effFrom = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,
						effectiveFrom);

				String effectiveTo = outPutFormat
						.format(inputFormat.parse(renbanGroupCodeMasterSaveRequestDto.getEtdToDate()));

				Date effTo = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,
						effectiveTo);

				int check = noemRenbanSetupMstRepository.updateRenbanCodeMaster(renbanGroupCodeMasterSaveRequestDto.getContDstCd(),
						groupCodeDetailsDto.getRenbanGroupCode(), effFrom, effTo, groupCodeDetailsDto.getGroupId(),
						groupCodeDetailsDto.getFolderName(), ConstantUtils.TEST_USER);
				if (check == 0) {
					throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3006);
				}

			}

		}

	}

}
