package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.GroupCodeDetailsDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterRequestDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseDto;
import com.tpex.invoice.dto.RenbanGroupCodeMasterResponseFinalDto;
import com.tpex.invoice.dto.RenbancodeMasterDeleteDto;
import com.tpex.invoice.service.RenbanGroupCodeMasterService;

@ExtendWith(MockitoExtension.class)
class RenbanGroupCodeMasterControllerTest {

	@InjectMocks
	RenbanGroupCodeMasterController renbanGroupCodeMasterController;
	
	@Mock
	RenbanGroupCodeMasterService renbanGroupCodeMasterService;

	@Test
	void testSearchRenbanGroupCodeMasterDetails() throws ParseException {
		
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
			
			RenbanGroupCodeMasterResponseFinalDto finalResponseDto = new RenbanGroupCodeMasterResponseFinalDto();

			finalResponseDto.setRenbanData(renbanDataList);
			finalResponseDto.setRenbanGroupCodeList(getCommonMultiSelectDropdownDtoList());
			
			Mockito.lenient().when(renbanGroupCodeMasterService.getRenbanGroupCodeMasterDetails(request)).thenReturn(finalResponseDto);
		
			ResponseEntity<RenbanGroupCodeMasterResponseFinalDto> finalresponse = renbanGroupCodeMasterController.searchRenbanGroupCodeMasterDetails(request);
			
			assertNotNull(finalresponse);
	}
	
	@Test
	void testSearchRenbanGroupCodeMasterDetailsWithException() throws ParseException {
		
		RenbanGroupCodeMasterRequestDto request=new RenbanGroupCodeMasterRequestDto();

		request.setFinalDestCodeAndName("301B-MALAYSIA");
		
			List<RenbanGroupCodeMasterResponseDto> renbanDataList=new ArrayList<>();
	 
			RenbanGroupCodeMasterResponseFinalDto finalResponseDto = new RenbanGroupCodeMasterResponseFinalDto();

			List<CommonMultiSelectDropdownDto> listOfDropdownValues = new ArrayList<>();
			finalResponseDto.setRenbanData(renbanDataList);
			finalResponseDto.setRenbanGroupCodeList(listOfDropdownValues);
			
			Mockito.lenient().when(renbanGroupCodeMasterService.getRenbanGroupCodeMasterDetails(request)).thenReturn(finalResponseDto);
		
			assertThrows(MyResourceNotFoundException.class, () -> renbanGroupCodeMasterController.searchRenbanGroupCodeMasterDetails(request));

	}
	
	public List<GroupCodeDetailsDto> getGroupOneList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto dtoOne=new GroupCodeDetailsDto();
		dtoOne.setFolderName("FOLDE NAME ONE");
		dtoOne.setGroupId("1");
		dtoOne.setRenbanGroupCode("1");

		GroupCodeDetailsDto dtoOne1=new GroupCodeDetailsDto();
		dtoOne1.setFolderName("FOLDE NAME ONE 1");
		dtoOne1.setGroupId("1");
		dtoOne1.setRenbanGroupCode("1");

		groupCodeDetailsDtoList.add(dtoOne);
		groupCodeDetailsDtoList.add(dtoOne1);
		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupTwoList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto two=new GroupCodeDetailsDto();
		two.setFolderName("FOLDE NAME TWO");
		two.setGroupId("2");
		two.setRenbanGroupCode("2");

		GroupCodeDetailsDto two1=new GroupCodeDetailsDto();
		two1.setFolderName("FOLDE NAME TWO1");
		two1.setGroupId("2");
		two1.setRenbanGroupCode("2");

		groupCodeDetailsDtoList.add(two);
		groupCodeDetailsDtoList.add(two1);

		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupthreeList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto three=new GroupCodeDetailsDto();
		three.setFolderName("FOLDE NAME THREE");
		three.setGroupId("3");
		three.setRenbanGroupCode("3");

		GroupCodeDetailsDto three1=new GroupCodeDetailsDto();
		three1.setFolderName("FOLDE NAME THREE");
		three1.setGroupId("3");
		three1.setRenbanGroupCode("3");

		groupCodeDetailsDtoList.add(three);
		groupCodeDetailsDtoList.add(three1);


		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupFourList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto four=new GroupCodeDetailsDto();
		four.setFolderName("FOLDE NAME FOUR");
		four.setGroupId("4");
		four.setRenbanGroupCode("4");

		GroupCodeDetailsDto four1=new GroupCodeDetailsDto();
		four1.setFolderName("FOLDE NAME FOUR 1");
		four1.setGroupId("4");
		four1.setRenbanGroupCode("4");

		groupCodeDetailsDtoList.add(four);
		groupCodeDetailsDtoList.add(four1);

		return groupCodeDetailsDtoList;

	}

	public List<GroupCodeDetailsDto> getGroupFiveList(){

		List<GroupCodeDetailsDto> groupCodeDetailsDtoList=new ArrayList<>();
		GroupCodeDetailsDto five=new GroupCodeDetailsDto();
		five.setFolderName("FOLDE NAME FIVE");
		five.setGroupId("5");
		five.setRenbanGroupCode("5");

		GroupCodeDetailsDto five1=new GroupCodeDetailsDto();
		five1.setFolderName("FOLDE NAME FIVE 1");
		five1.setGroupId("5");
		five1.setRenbanGroupCode("5");

		groupCodeDetailsDtoList.add(five);
		groupCodeDetailsDtoList.add(five1);
		return groupCodeDetailsDtoList;

	}
	
		
	public List<CommonMultiSelectDropdownDto> getCommonMultiSelectDropdownDtoList(){
		
		CommonMultiSelectDropdownDto list = new CommonMultiSelectDropdownDto();
		
		list.setLabel("1");
		list.setLabel("1");
		
		CommonMultiSelectDropdownDto list2 = new CommonMultiSelectDropdownDto();
		
		list2.setLabel("2");
		list2.setLabel("2");
		
		CommonMultiSelectDropdownDto list3 = new CommonMultiSelectDropdownDto();
		
		list3.setLabel("3");
		list3.setLabel("3");
		
		CommonMultiSelectDropdownDto list4 = new CommonMultiSelectDropdownDto();
		
		list4.setLabel("G");
		list4.setLabel("G");
		
		CommonMultiSelectDropdownDto list5 = new CommonMultiSelectDropdownDto();
		
		list5.setLabel("A");
		list5.setLabel("A");
		
		
		CommonMultiSelectDropdownDto list6 = new CommonMultiSelectDropdownDto();
		
		list6.setLabel("S");
		list6.setLabel("S");
		
		List<CommonMultiSelectDropdownDto> listOfDropdownValues = new ArrayList<>();
		listOfDropdownValues.add(list);
		listOfDropdownValues.add(list2);
		listOfDropdownValues.add(list3);
		listOfDropdownValues.add(list4);
		listOfDropdownValues.add(list5);
		listOfDropdownValues.add(list6);
		
		return listOfDropdownValues;
	
	}
	
	@Test
	void testDeleteRenbanGroupCodeMasterDetails() throws ParseException {
		
		List<String> renbanCode= Arrays.asList("A", "S"); 
		List<String> renbanCode1= Arrays.asList("Q", "Y"); 
		RenbancodeMasterDeleteDto dto1 = new RenbancodeMasterDeleteDto("709B-Testing","01/07/2005","1",renbanCode);
		RenbancodeMasterDeleteDto dto2 = new RenbancodeMasterDeleteDto("306B-Testing","01/08/2015","2",renbanCode1);
		
		List<RenbancodeMasterDeleteDto> asList = Arrays.asList(dto1,dto2);
		
		Mockito.doNothing().when(renbanGroupCodeMasterService).deleteRenbanGroupCodeMasterDetails(anyList());
		
		ResponseEntity<ApiResponseMessage> result = renbanGroupCodeMasterController.renbanGroupCodeMasterDetails(asList);
		
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
	
	

}
