package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.NoemRenbanBookDtlEntity;
import com.tpex.entity.NoemRenbanBookDtlIdEntity;
import com.tpex.entity.NoemRenbanSetupMstEntity;
import com.tpex.entity.NoemRenbanSetupMstIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.GroupCodeDetailsDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterRequestDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseFinalDto;
import com.tpex.invoice.dto.RenbancodeMasterDeleteDto;
import com.tpex.invoice.serviceimpl.RenbanGroupCodeMasterServiceImpl;
import com.tpex.repository.NoemRenbanBookDtlRepository;
import com.tpex.repository.NoemRenbanSetupMstRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@ExtendWith(MockitoExtension.class)
class RenbanGroupCodeMasterServiceImplTest {

	@InjectMocks
	RenbanGroupCodeMasterServiceImpl renbanGroupCodeMasterServiceImpl;

	@Mock
	private NoemRenbanSetupMstRepository noemRenbanSetupMstRepository;

	@Mock
	private NoemRenbanBookDtlRepository noemRenbanBookDtlRepository;

	@Test
	void testGetRenbanGroupCodeMasterDetails() throws ParseException {

		RenbanGroupCodeMasterRequestDto request=new RenbanGroupCodeMasterRequestDto();

		request.setFinalDestCodeAndName("812B-Test Code");

		RenbanGroupCodeMasterResponseDto response=new RenbanGroupCodeMasterResponseDto();

		Map<String, List<GroupCodeDetailsDto>> renbanData = new HashMap<String, List<GroupCodeDetailsDto>>();

		renbanData.put("1", getGroupOneList());
		renbanData.put("2", getGroupTwoList());
		renbanData.put("3", getGroupthreeList());
		renbanData.put("4", getGroupFourList());
		renbanData.put("5", getGroupFiveList());

		List<RenbanGroupCodeMasterResponseDto> renbanDataList=new ArrayList<>();

		response.setContDstCd("812B");
		response.setEtdFromDate("2002-02-02");
		response.setEtdToDate("2023-03-03");

		response.setGoupdIdDetails(renbanData);

		renbanDataList.add(response);

		RenbanGroupCodeMasterResponseFinalDto finalResponse = new RenbanGroupCodeMasterResponseFinalDto();

		finalResponse.setRenbanData(renbanDataList);
		finalResponse.setRenbanGroupCodeList(getCommonMultiSelectDropdownDtoList());


		Mockito.lenient().when(noemRenbanSetupMstRepository.findByIdContDstCdOrderByIdContGrpCdAsc("812B")).thenReturn(entityList());

		RenbanGroupCodeMasterResponseFinalDto finalRes=renbanGroupCodeMasterServiceImpl.getRenbanGroupCodeMasterDetails(request);
		assertNotNull(finalRes);
	}


	public List<NoemRenbanSetupMstEntity> entityList(){

		NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity1 = new NoemRenbanSetupMstIdEntity();

		noemRenbanSetupMstIdEntity1.setContDstCd("812B");
		noemRenbanSetupMstIdEntity1.setContGrpCd("A");
		noemRenbanSetupMstIdEntity1.setEffFromDt(new Date(23/02/2023));
		noemRenbanSetupMstIdEntity1.setGroupId("1");

		NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity2 = new NoemRenbanSetupMstIdEntity();

		noemRenbanSetupMstIdEntity2.setContDstCd("812B");
		noemRenbanSetupMstIdEntity2.setContGrpCd("A");
		noemRenbanSetupMstIdEntity2.setEffFromDt(new Date(23/02/2023));
		noemRenbanSetupMstIdEntity2.setGroupId("2");

		NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity3 = new NoemRenbanSetupMstIdEntity();

		noemRenbanSetupMstIdEntity3.setContDstCd("812B");
		noemRenbanSetupMstIdEntity3.setContGrpCd("A");
		noemRenbanSetupMstIdEntity3.setEffFromDt(new Date(23/02/2023));
		noemRenbanSetupMstIdEntity3.setGroupId("3");

		NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity4 = new NoemRenbanSetupMstIdEntity();

		noemRenbanSetupMstIdEntity4.setContDstCd("812B");
		noemRenbanSetupMstIdEntity4.setContGrpCd("A");
		noemRenbanSetupMstIdEntity4.setEffFromDt(new Date(23/02/2023));
		noemRenbanSetupMstIdEntity4.setGroupId("4");

		NoemRenbanSetupMstIdEntity noemRenbanSetupMstIdEntity5 = new NoemRenbanSetupMstIdEntity();

		noemRenbanSetupMstIdEntity5.setContDstCd("812B");
		noemRenbanSetupMstIdEntity5.setContGrpCd("A");
		noemRenbanSetupMstIdEntity5.setEffFromDt(new Date(23/02/2023));
		noemRenbanSetupMstIdEntity5.setGroupId("5");

		NoemRenbanSetupMstEntity noemRenbanSetupMstEntity1= new NoemRenbanSetupMstEntity();
		noemRenbanSetupMstEntity1.setCompanyCode("TMT");
		noemRenbanSetupMstEntity1.setEffToDt(new Date(22/02/2202));
		noemRenbanSetupMstEntity1.setFolderName("FOLDER NAME");
		noemRenbanSetupMstEntity1.setUpdatedBy("TEST USER");
		noemRenbanSetupMstEntity1.setUpdatedDate(new Date(25/07/2002));
		noemRenbanSetupMstEntity1.setId(noemRenbanSetupMstIdEntity1);

		NoemRenbanSetupMstEntity noemRenbanSetupMstEntity2= new NoemRenbanSetupMstEntity();
		noemRenbanSetupMstEntity2.setCompanyCode("TMT");
		noemRenbanSetupMstEntity2.setEffToDt(new Date(22/02/2202));
		noemRenbanSetupMstEntity2.setFolderName("FOLDER NAME");
		noemRenbanSetupMstEntity2.setUpdatedBy("TEST USER");
		noemRenbanSetupMstEntity2.setUpdatedDate(new Date(25/07/2002));
		noemRenbanSetupMstEntity2.setId(noemRenbanSetupMstIdEntity2);

		NoemRenbanSetupMstEntity noemRenbanSetupMstEntity3= new NoemRenbanSetupMstEntity();
		noemRenbanSetupMstEntity3.setCompanyCode("TMT");
		noemRenbanSetupMstEntity3.setEffToDt(new Date(22/02/2202));
		noemRenbanSetupMstEntity3.setFolderName("FOLDER NAME");
		noemRenbanSetupMstEntity3.setUpdatedBy("TEST USER");
		noemRenbanSetupMstEntity3.setUpdatedDate(new Date(25/07/2002));
		noemRenbanSetupMstEntity3.setId(noemRenbanSetupMstIdEntity3);

		NoemRenbanSetupMstEntity noemRenbanSetupMstEntity4= new NoemRenbanSetupMstEntity();
		noemRenbanSetupMstEntity4.setCompanyCode("TMT");
		noemRenbanSetupMstEntity4.setEffToDt(new Date(22/02/2202));
		noemRenbanSetupMstEntity4.setFolderName("FOLDER NAME");
		noemRenbanSetupMstEntity4.setUpdatedBy("TEST USER");
		noemRenbanSetupMstEntity4.setUpdatedDate(new Date(25/07/2002));
		noemRenbanSetupMstEntity4.setId(noemRenbanSetupMstIdEntity4);

		NoemRenbanSetupMstEntity entitynoemRenbanSetupMstEntity5= new NoemRenbanSetupMstEntity();
		entitynoemRenbanSetupMstEntity5.setCompanyCode("TMT");
		entitynoemRenbanSetupMstEntity5.setEffToDt(new Date(22/02/2202));
		entitynoemRenbanSetupMstEntity5.setFolderName("FOLDER NAME");
		entitynoemRenbanSetupMstEntity5.setUpdatedBy("TEST USER");
		entitynoemRenbanSetupMstEntity5.setUpdatedDate(new Date(25/07/2002));
		entitynoemRenbanSetupMstEntity5.setId(noemRenbanSetupMstIdEntity5);

		List<NoemRenbanSetupMstEntity> listOfNoemRenbanSetupMstEntity=new ArrayList<>();

		listOfNoemRenbanSetupMstEntity.add(noemRenbanSetupMstEntity1);
		listOfNoemRenbanSetupMstEntity.add(noemRenbanSetupMstEntity2);
		listOfNoemRenbanSetupMstEntity.add(noemRenbanSetupMstEntity3);
		listOfNoemRenbanSetupMstEntity.add(noemRenbanSetupMstEntity4);
		listOfNoemRenbanSetupMstEntity.add(entitynoemRenbanSetupMstEntity5);

		return listOfNoemRenbanSetupMstEntity;

	}

	public List<GroupCodeDetailsDto> getGroupOneList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto groupCodeDetailsDto1=new GroupCodeDetailsDto();
		groupCodeDetailsDto1.setFolderName("FOLDE NAME ONE");
		groupCodeDetailsDto1.setGroupId("1");
		groupCodeDetailsDto1.setRenbanGroupCode("1");

		GroupCodeDetailsDto groupCodeDetailsDto2=new GroupCodeDetailsDto();
		groupCodeDetailsDto2.setFolderName("FOLDE NAME ONE 1");
		groupCodeDetailsDto2.setGroupId("1");
		groupCodeDetailsDto2.setRenbanGroupCode("1");

		groupCodeDetailsDtoList.add(groupCodeDetailsDto1);
		groupCodeDetailsDtoList.add(groupCodeDetailsDto2);
		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupTwoList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto groupCodeDetailsDto1=new GroupCodeDetailsDto();
		groupCodeDetailsDto1.setFolderName("FOLDE NAME TWO");
		groupCodeDetailsDto1.setGroupId("2");
		groupCodeDetailsDto1.setRenbanGroupCode("2");

		GroupCodeDetailsDto groupCodeDetailsDto2=new GroupCodeDetailsDto();
		groupCodeDetailsDto2.setFolderName("FOLDE NAME TWO1");
		groupCodeDetailsDto2.setGroupId("2");
		groupCodeDetailsDto2.setRenbanGroupCode("2");

		groupCodeDetailsDtoList.add(groupCodeDetailsDto1);
		groupCodeDetailsDtoList.add(groupCodeDetailsDto2);

		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupthreeList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto groupCodeDetailsDto1=new GroupCodeDetailsDto();
		groupCodeDetailsDto1.setFolderName("FOLDE NAME THREE");
		groupCodeDetailsDto1.setGroupId("3");
		groupCodeDetailsDto1.setRenbanGroupCode("3");

		GroupCodeDetailsDto groupCodeDetailsDto2=new GroupCodeDetailsDto();
		groupCodeDetailsDto2.setFolderName("FOLDE NAME THREE");
		groupCodeDetailsDto2.setGroupId("3");
		groupCodeDetailsDto2.setRenbanGroupCode("3");

		groupCodeDetailsDtoList.add(groupCodeDetailsDto1);
		groupCodeDetailsDtoList.add(groupCodeDetailsDto2);


		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupFourList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto groupCodeDetailsDto1=new GroupCodeDetailsDto();
		groupCodeDetailsDto1.setFolderName("FOLDE NAME FOUR");
		groupCodeDetailsDto1.setGroupId("4");
		groupCodeDetailsDto1.setRenbanGroupCode("4");

		GroupCodeDetailsDto fogroupCodeDetailsDto2=new GroupCodeDetailsDto();
		fogroupCodeDetailsDto2.setFolderName("FOLDE NAME FOUR 1");
		fogroupCodeDetailsDto2.setGroupId("4");
		fogroupCodeDetailsDto2.setRenbanGroupCode("4");

		groupCodeDetailsDtoList.add(groupCodeDetailsDto1);
		groupCodeDetailsDtoList.add(fogroupCodeDetailsDto2);

		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupFiveList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto groupCodeDetailsDto1=new GroupCodeDetailsDto();
		groupCodeDetailsDto1.setFolderName("FOLDE NAME FIVE");
		groupCodeDetailsDto1.setGroupId("5");
		groupCodeDetailsDto1.setRenbanGroupCode("5");

		GroupCodeDetailsDto groupCodeDetailsDto2=new GroupCodeDetailsDto();
		groupCodeDetailsDto2.setFolderName("FOLDE NAME FIVE 1");
		groupCodeDetailsDto2.setGroupId("5");
		groupCodeDetailsDto2.setRenbanGroupCode("5");

		groupCodeDetailsDtoList.add(groupCodeDetailsDto1);
		groupCodeDetailsDtoList.add(groupCodeDetailsDto2);
		return groupCodeDetailsDtoList;

	}

	public List<CommonMultiSelectDropdownDto> getCommonMultiSelectDropdownDtoList(){

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto1 = new CommonMultiSelectDropdownDto("1", "1");

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto2 = new CommonMultiSelectDropdownDto("2", "2");

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto3 = new CommonMultiSelectDropdownDto("3", "3");

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto4 = new CommonMultiSelectDropdownDto("G", "G");

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto5 = new CommonMultiSelectDropdownDto("A", "A");

		CommonMultiSelectDropdownDto commonMultiSelectDropdownDto6 = new CommonMultiSelectDropdownDto("S", "S");

		List<CommonMultiSelectDropdownDto> listOfDropdownValues = Arrays.asList(commonMultiSelectDropdownDto1, commonMultiSelectDropdownDto2, commonMultiSelectDropdownDto3, commonMultiSelectDropdownDto4, commonMultiSelectDropdownDto5, commonMultiSelectDropdownDto6);

		return listOfDropdownValues;
	}

	@Test
	void TestDeleteRenbanGroupCodeMasterDetailsWithError() throws ParseException {

		List<String> renbanCode= Arrays.asList("Y", "Z"); 
		RenbancodeMasterDeleteDto dto1 = new RenbancodeMasterDeleteDto("709B-Testing","14/12/2022","01/08/2005",renbanCode);

		List<RenbancodeMasterDeleteDto> deleteRequest = Arrays.asList(dto1);

		List<NoemRenbanBookDtlEntity> renbanCodes= new ArrayList<>();

		NoemRenbanBookDtlIdEntity idEntity = new NoemRenbanBookDtlIdEntity();
		idEntity.setContDestCd("709B");
		idEntity.setContGrpCode("Y");
		idEntity.setContVanningMnth("202212");
		idEntity.setEtd1(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,"2022-12-14"));
		NoemRenbanBookDtlEntity entity = new NoemRenbanBookDtlEntity();
		entity.setId(idEntity);
		entity.setUpdatedBy("Test User");

		renbanCodes.add(entity);
		String destCode="709B";
		Date effFrom=DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,"2022-12-14");

		Mockito.lenient().when(noemRenbanBookDtlRepository.findByIdContDestCdAndIdEtd1(destCode ,effFrom)).thenReturn(renbanCodes);

		assertThrows(MyResourceNotFoundException.class, () -> renbanGroupCodeMasterServiceImpl.deleteRenbanGroupCodeMasterDetails(deleteRequest));

	}

	@Test
	void TestDeleteRenbanGroupCodeMasterDetailsWithException() throws ParseException {

		List<String> renbanCode= Arrays.asList("A", "S");  
		List<String> renbanCode1= Arrays.asList("Q", "Y"); 
		RenbancodeMasterDeleteDto renbancodeMasterDeleteDto1 = new RenbancodeMasterDeleteDto("709B-Testing","01/07/2005","01/07/2005",renbanCode);
		RenbancodeMasterDeleteDto renbancodeMasterDeleteDto2 = new RenbancodeMasterDeleteDto("306B-Testing","01/08/2015","01/08/2015",renbanCode1);

		List<RenbancodeMasterDeleteDto> deleteRequest = Arrays.asList(renbancodeMasterDeleteDto1, renbancodeMasterDeleteDto2);

		List<NoemRenbanBookDtlEntity> renbanCodes= new ArrayList<>();
		String destCode="709B";
		Date effFrom=DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,"2005-07-01");

		Mockito.lenient().when(noemRenbanBookDtlRepository.findByIdContDestCdAndIdEtd1(destCode ,effFrom)).thenReturn(renbanCodes);

		assertThrows(InvalidInputParametersException.class, () -> renbanGroupCodeMasterServiceImpl.deleteRenbanGroupCodeMasterDetails(deleteRequest));

	}
	
	@Test
	void TestDeleteRenbanGroupCodeMasterDetails() throws ParseException {

		List<String> renbanCode= Arrays.asList("A", "S");  
		RenbancodeMasterDeleteDto renbancodeMasterDeleteDto1 = new RenbancodeMasterDeleteDto("301B-Testing","01/06/2005","21/03/2007",renbanCode);

		List<RenbancodeMasterDeleteDto> deleteRequest = Arrays.asList(renbancodeMasterDeleteDto1);

		List<NoemRenbanBookDtlEntity> renbanCodes= new ArrayList<>();
		String destCode="301B";
		Date effFrom=DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,"2005-06-01");
		Date effTo=DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE,"2007-03-21");

		Mockito.when(noemRenbanSetupMstRepository.existsByIdContDstCdAndIdEffFromDtAndEffToDt(destCode, effFrom, effTo)).thenReturn(true);
		Mockito.lenient().when(noemRenbanBookDtlRepository.findByIdContDestCdAndIdEtd1(destCode ,effFrom)).thenReturn(renbanCodes);

		doNothing().when(noemRenbanSetupMstRepository).deleteByIdContDstCdAndIdEffFromDtAndEffToDt(destCode, effFrom, effTo);
		Assertions.assertDoesNotThrow(() -> renbanGroupCodeMasterServiceImpl.deleteRenbanGroupCodeMasterDetails(deleteRequest));

	}

}
