package com.tpex.month.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.tpex.month.exception.InvalidFileException;
import com.tpex.month.exception.MyResourceNotFoundException;
import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.CommonPaginationResponse;
import com.tpex.month.model.dto.CommonResponse;
import com.tpex.month.model.dto.CustomBrokerMasterDropdown;
import com.tpex.month.model.dto.PaginationResponse;
import com.tpex.month.model.dto.ValidList;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearchResponse;
import com.tpex.month.service.DropdownService;
import com.tpex.month.service.VesselBookingMasterDownloadService;
import com.tpex.month.service.VesselBookingMasterSaveService;
import com.tpex.month.service.VesselBookingMasterSearchService;
import com.tpex.month.service.VesselBookingMasterUploadService;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.validate.VesselBookingMasterSaveValidator;

import lombok.RequiredArgsConstructor;

@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/month/vesselBookingMaster")
public class VesselBookingMasterController {

	private final VesselBookingMasterSearchService vesselBookingMasterSearchService;
	private final VesselBookingMasterDownloadService vesselBookingMasterDownloadService;
	private final VesselBookingMasterSaveValidator vesselBookingMasterSaveValidator;
	private final VesselBookingMasterSaveService vesselBookingMasterSaveService;
	private final DropdownService dropdownService;

	private final VesselBookingMasterUploadService vesselBookingMasterUploadService;

	@PostMapping(path = "/search")
	public ResponseEntity<CommonPaginationResponse<VesselBookingMasterSearchResponse>> searchVesselBookingMaster(
			@Valid @RequestBody CommonPaginationRequest<VesselBookingMasterSearchRequest> request) {

		Page<VesselBookingMasterSearch> page = vesselBookingMasterSearchService.searchVesselBookingMaster(request);
		List<CustomBrokerMasterDropdown> customBrokerMasterDropDown = dropdownService.getCustomBrokerMasterDropDown();

		VesselBookingMasterSearchResponse searchRes = new VesselBookingMasterSearchResponse();
		searchRes.setData(page.getContent());
		searchRes.setCustomBrokerMaster(customBrokerMasterDropDown);

		CommonPaginationResponse<VesselBookingMasterSearchResponse> res = new CommonPaginationResponse<>();
		res.setResponseBody(searchRes);
		res.setPagination(new PaginationResponse(page.getNumber(), page.getSize(), page.getTotalPages(),
				page.getTotalElements()));

		return ResponseEntity.ok(res);
	}

	@PostMapping(path = "/download")
	public ResponseEntity<Object> downloadVesselBookingMaster(
			@Valid @RequestBody VesselBookingMasterSearchRequest request) {
		Map<String, Object> map = vesselBookingMasterDownloadService.downloadVesselBookingMaster(request);

		if (map.containsKey(ConstantUtil.STATUS) && map.get(ConstantUtil.STATUS).equals(ConstantUtil.OFFLINE)) {
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			String fileName = (String) map.get("filename");
			HttpHeaders headers = new HttpHeaders();
			headers.add("filename", "\"" + fileName + "\"");
			headers.add("Content-Disposition", "inline; filename= \"" + fileName + "\"");
			headers.add("Access-Control-Expose-Headers", "Content-Disposition, filename");
			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(map.get("outStream"));
		}
	}

	@PostMapping(path = "/save")
	public ResponseEntity<CommonResponse> saveVesselBookingMaster(
			@Valid @RequestBody ValidList<VesselBookingMasterSaveRequest> request, BindingResult bindingResult)
			throws NoSuchMethodException, SecurityException, MethodArgumentNotValidException {

		vesselBookingMasterSaveValidator.validate(request, bindingResult);
		if (bindingResult.hasErrors()) {
			throw new MethodArgumentNotValidException(new MethodParameter(
					this.getClass().getDeclaredMethod("saveVesselBookingMaster", ValidList.class, BindingResult.class),
					0), bindingResult);
		}

		CommonResponse response = vesselBookingMasterSaveService.saveVesselBookingMaster(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Method for upload Vessel Booking Master Batch
	 * 
	 * @param batchName
	 * @param userId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping(path = "/uplaodvesselbooking")
	public ResponseEntity<Object> uploadVesselBookingMaster(@RequestParam String batchName, @RequestParam String userId,
			@RequestParam("file") MultipartFile file) throws IOException, MultipartException {

		if (batchName.isBlank() || batchName.isEmpty() || file.isEmpty()) {
			throw new MyResourceNotFoundException(ConstantUtil.ERR_CM_3001);
		}
		String filename = file.getOriginalFilename();
		if (filename != null && (!(filename.endsWith(".xls") || filename.endsWith(ConstantUtil.EXCEL_FORMAT)))) {
			throw new InvalidFileException(ConstantUtil.ERR_CM_3021);
		}
		return vesselBookingMasterUploadService.uploadVesselBookingMaster(batchName, file, userId);
		
	}
}
