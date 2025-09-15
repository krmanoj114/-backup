package com.tpex.month.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpex.month.model.dto.CommonPaginationRequest;
import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.PaginationRequest;
import com.tpex.month.model.dto.VesselBookingMasterSearch;
import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;
import com.tpex.month.model.repository.CustomVesselBookingMasterRepository;
import com.tpex.month.util.ConstantUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@SuppressWarnings("squid:S3776")
public class VesselBookingMasterSearchService {

	private final CustomVesselBookingMasterRepository customVesselBookingMasterRepository;
	
	private static final String SOME_CONT_NOT_BOOK = "No. of container change, booking not update.";
	
	public Page<VesselBookingMasterSearch> searchVesselBookingMaster(CommonPaginationRequest<VesselBookingMasterSearchRequest> request){
		PaginationRequest pagination = request.getPagination();
		PageRequest pageable = pagination != null ? PageRequest.of(pagination.getPageNo(), pagination.getPageSize()) : null;
		VesselBookingMasterSearchRequest requestBody = request.getRequestBody();
		
		Page<VesselBookingMasterSearch> page = customVesselBookingMasterRepository
				.findRenbanBookingData(request.getRequestBody(), pageable);
		
		List<VesselBookingMasterSearch> listRenban = page.getContent();
		if(!listRenban.isEmpty()) {
			MixedVesselBooking input = new MixedVesselBooking();
			input.setKeihenRevNo(customVesselBookingMasterRepository.findKeihenRev(requestBody.getVanningMonth()));
			input.setDestinationCodes(listRenban.stream().map(r -> r.getDestinationCode()).distinct().collect(Collectors.toList()));
			input.setVanningMonth(requestBody.getVanningMonth());
			input.setEtdFrom(requestBody.getEtdFrom());
			input.setEtdTo(requestBody.getEtdTo());
			input.setShippingCompanyCode(requestBody.getShippingCompanyCode());
			
			List<MixedVesselBooking> findNoOfRenban = findNoOfRenban(input);
			
			for (VesselBookingMasterSearch renban : listRenban) {
				setCustomBrokerForDisplay(renban);
				
				for (MixedVesselBooking noOfRenban : findNoOfRenban) {
					if(renban.getEtd1().equals(noOfRenban.getEtd())
						&& renban.getShippingCompany().equals(noOfRenban.getShippingCompanyCode())
						&& renban.getGroupId().equals(noOfRenban.getGroupId())
						&& ConstantUtil.NO.equals(noOfRenban.getFlagCont())
						&& renban.getDestinationCode().equals(noOfRenban.getDestinationCode())) {
						
						renban.setUpdate(true);
						renban.setBookingStatus(SOME_CONT_NOT_BOOK);
						renban.setNoOfContainer20ft(Integer.toString(noOfRenban.getCont20()));
						renban.setNoOfContainer40ft(Integer.toString(noOfRenban.getCont40()));
					}
				}
				checkContZeroSetCancelled(renban);
			}
		}
		
		return page;
	}

	private void setCustomBrokerForDisplay(VesselBookingMasterSearch renban) {
		StringBuilder customBrokerDisplay = new StringBuilder();
		if(StringUtils.isNotBlank(renban.getCustomBrokerCode())) {
			customBrokerDisplay.append(renban.getCustomBrokerCode() + " ");
		}
		if(StringUtils.isNotBlank(renban.getCustomBrokerName())) {
			customBrokerDisplay.append(renban.getCustomBrokerName());
		}
		renban.setCustomBrokerName(customBrokerDisplay.toString());
	}

	private void checkContZeroSetCancelled(VesselBookingMasterSearch renban) {
		if("0".equals(renban.getNoOfContainer20ft()) && "0".equals(renban.getNoOfContainer40ft())
				&& !"Cancelled".equals(renban.getVessel1())) {
			renban.setVessel1("Cancelled");
			renban.setCancelled(true);
			renban.setUpdate(true);
		}
	}

	private List<MixedVesselBooking> findNoOfRenban(MixedVesselBooking input){
		List<MixedVesselBooking> out = new ArrayList<>();
		List<MixedVesselBooking> findShipCompETD = customVesselBookingMasterRepository.findShipCompETD(input);
		
		for(MixedVesselBooking shipEtd : findShipCompETD) {
			MixedVesselBooking mixedIn = new MixedVesselBooking();
			mixedIn.setVanningMonth(input.getVanningMonth());
			mixedIn.setDestinationCode(shipEtd.getDestinationCode());
			mixedIn.setEtd(shipEtd.getEtd());
			mixedIn.setShippingCompanyCode(shipEtd.getShippingCompanyCode());
			mixedIn.setKeihenRevNo(shipEtd.getKeihenRevNo());
			
			List<MixedVesselBooking> allTypeList = customVesselBookingMasterRepository.getAllTypeGrpCnt(mixedIn);
			
			List<MixedVesselBooking> monthlyList = allTypeList.stream().filter(a -> a.getListType().equals("MONTHLY")).collect(Collectors.toList());
			List<MixedVesselBooking> keihenList = allTypeList.stream().filter(a -> a.getListType().equals("KEIHEN")).collect(Collectors.toList());
			List<MixedVesselBooking> dailyList = allTypeList.stream().filter(a -> a.getListType().equals("DAILY")).collect(Collectors.toList());
			List<MixedVesselBooking> renbanList = allTypeList.stream().filter(a -> a.getListType().equals("RENBAN")).collect(Collectors.toList());
			
			if(hasAllValues(monthlyList, keihenList, dailyList)) {
				prepareMonthlyKeihenDaily(out, mixedIn, monthlyList, keihenList, dailyList, renbanList);
			} else if (hasKeihenDaily(monthlyList, keihenList, dailyList)) {
				prepareKeihenDaily(out, mixedIn, keihenList, dailyList, renbanList);
			} else if (hasMonthlyKeihen(monthlyList, keihenList, dailyList)) {
				prepareMonthlyKeihenOrDaily(out, mixedIn, monthlyList, keihenList, renbanList);
			} else if (hasMonthlyDaily(monthlyList, keihenList, dailyList)) {
				prepareMonthlyKeihenOrDaily(out, mixedIn, monthlyList, dailyList, renbanList);
			} else if (hasKeihen(monthlyList, keihenList, dailyList)) {
				prepareKeihenOrDaily(out, mixedIn, keihenList, renbanList);
			} else if (hasDaily(monthlyList, keihenList, dailyList)) {
				prepareKeihenOrDaily(out, mixedIn, dailyList, renbanList);
			}
		}
		
		return out;
	}

	private boolean hasDaily(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList,
			List<MixedVesselBooking> dailyList) {
		return isEmpty(monthlyList) && isEmpty(keihenList) && isNotEmpty(dailyList);
	}

	private boolean hasKeihen(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList,
			List<MixedVesselBooking> dailyList) {
		return isEmpty(monthlyList) && isNotEmpty(keihenList) && isEmpty(dailyList);
	}

	private boolean hasMonthlyDaily(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList,
			List<MixedVesselBooking> dailyList) {
		return isNotEmpty(monthlyList) && isEmpty(keihenList) && isNotEmpty(dailyList);
	}

	private boolean hasMonthlyKeihen(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList,
			List<MixedVesselBooking> dailyList) {
		return isNotEmpty(monthlyList) && isNotEmpty(keihenList) && isEmpty(dailyList);
	}

	private boolean hasKeihenDaily(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList, List<MixedVesselBooking> dailyList) {
		return isEmpty(monthlyList) && isNotEmpty(keihenList) && isNotEmpty(dailyList);
	}

	private boolean hasAllValues(List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> keihenList, List<MixedVesselBooking> dailyList) {
		return isNotEmpty(monthlyList) && isNotEmpty(keihenList) && isNotEmpty(dailyList);
	}
	
	private boolean isNotEmpty(List<?> list) {
		return CollectionUtils.isNotEmpty(list);
	}
	private boolean isEmpty(List<?> list) {
		return CollectionUtils.isEmpty(list);
	}

	private MixedVesselBooking prepareBookingStatus(MixedVesselBooking mixedIn, MixedVesselBooking sub, String flag) {
		MixedVesselBooking forOut = new MixedVesselBooking();
		forOut.setShippingCompanyCode(mixedIn.getShippingCompanyCode());
		forOut.setEtd(mixedIn.getEtd());
		forOut.setGroupId(sub.getGroupId());
		forOut.setCont20(sub.getCont20());
		forOut.setCont40(sub.getCont40());
		forOut.setFlagCont(flag);
		forOut.setDestinationCode(mixedIn.getDestinationCode());
		return forOut;
	}

	private boolean isContLeftGreatherThanRight(MixedVesselBooking left, MixedVesselBooking right) {
		return left.getCont20() > right.getCont20() || left.getCont40() > right.getCont40();
	}
	
	private boolean isGroupIdEquals(MixedVesselBooking sub1, MixedVesselBooking sub2) {
		return sub1.getGroupId().equals(sub2.getGroupId());
	}
	
	private boolean isGroupIdEquals(MixedVesselBooking daily, MixedVesselBooking keihen, MixedVesselBooking monthly) {
		return monthly.getGroupId().equals(daily.getGroupId()) && keihen.getGroupId().equals(daily.getGroupId());
	}
	
	private void addRenban(List<MixedVesselBooking> out, MixedVesselBooking mixedIn, List<MixedVesselBooking> renbanList,
			MixedVesselBooking sub) {
		if (isNotEmpty(renbanList)) {
			for (MixedVesselBooking renban : renbanList) {
				if (isGroupIdEquals(renban, sub)) {
					if (isContLeftGreatherThanRight(sub, renban)) {
						out.add(prepareBookingStatus(mixedIn, sub, ConstantUtil.NO));
					} else {
						out.add(prepareBookingStatus(mixedIn, sub, ConstantUtil.YES));
					}
				}
			}
		} else {
			out.add(prepareBookingStatus(mixedIn, sub, ConstantUtil.NO));
		}
	}

	private void prepareKeihenOrDaily(List<MixedVesselBooking> out, MixedVesselBooking mixedIn, List<MixedVesselBooking> subList, List<MixedVesselBooking> renbanList) {
		for (MixedVesselBooking sub : subList) {
			addRenban(out, mixedIn, renbanList, sub);
		}
	}
	
	private void prepareMonthlyKeihenOrDaily(List<MixedVesselBooking> out, MixedVesselBooking mixedIn,
			List<MixedVesselBooking> monthlyList, List<MixedVesselBooking> subList, List<MixedVesselBooking> renbanList) {
		for (MixedVesselBooking monthly : monthlyList) {
			for (MixedVesselBooking sub : subList) {
				if (isGroupIdEquals(monthly, sub)) {
					int monthlyCntTot = monthly.getCont20() + monthly.getCont40();
					int subCntTot = sub.getCont20() + sub.getCont40();

					if (subCntTot >= monthlyCntTot && isContLeftGreatherThanRight(sub, monthly)) {
						addRenban(out, mixedIn, renbanList, sub);
					}
				}
			}
		}
	}

	private void prepareKeihenDaily(List<MixedVesselBooking> out, MixedVesselBooking mixedIn, List<MixedVesselBooking> keihenList,
			List<MixedVesselBooking> dailyList, List<MixedVesselBooking> renbanList) {
		
		for (MixedVesselBooking daily : dailyList) {
			for (MixedVesselBooking keihen : keihenList) {
				if(isGroupIdEquals(daily, keihen)) {
					int dailyCntTot = daily.getCont20() + daily.getCont40();
					int keihenCntTot = keihen.getCont20() + keihen.getCont40();
					
					if (keihenCntTot >= dailyCntTot) {
						if (isContLeftGreatherThanRight(keihen, daily)) {
							addRenban(out, mixedIn, renbanList, keihen);
						} else {
							addRenban(out, mixedIn, renbanList, daily);
						}
					}
				}
			}
		}
	}

	private void prepareMonthlyKeihenDaily(List<MixedVesselBooking> out, MixedVesselBooking mixedIn, List<MixedVesselBooking> monthlyList,
			List<MixedVesselBooking> keihenList, List<MixedVesselBooking> dailyList, List<MixedVesselBooking> renbanList) {
		
		for (MixedVesselBooking monthly : monthlyList) {
			for (MixedVesselBooking keihen : keihenList) {
				for (MixedVesselBooking daily : dailyList) {
					if (isGroupIdEquals(daily, keihen, monthly)) {
						int dailyCntTot = daily.getCont20() + daily.getCont40();
						int keihenCntTot = keihen.getCont20() + keihen.getCont40();
						int monthlyCntTot = monthly.getCont20() + monthly.getCont40();
						
						if(dailyCntTot >= monthlyCntTot || keihenCntTot >= monthlyCntTot) {
							if(dailyCntTot > keihenCntTot) {
								if(isContLeftGreatherThanRight(daily, monthly)) {
									addRenban(out, mixedIn, renbanList, daily);
								}
							} else {
								if(isContLeftGreatherThanRight(keihen, monthly)) {
									addRenban(out, mixedIn, renbanList, keihen);
								}
							}
						}
					}
				}
			}
		}
	}
	
}
