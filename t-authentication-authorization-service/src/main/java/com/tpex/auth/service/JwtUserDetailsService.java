package com.tpex.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tpex.auth.dto.AuthInfo;
import com.tpex.auth.dto.UserDTO;
import com.tpex.auth.dto.UserInfo;
import com.tpex.auth.entity.TbMCompanyPlantMappingEntity;
import com.tpex.auth.entity.TbMPlantEntity;
import com.tpex.auth.entity.TbMRoleEntity;
import com.tpex.auth.entity.TbMRoleUserMappingEntity;
import com.tpex.auth.entity.TbMUserEntity;
import com.tpex.auth.repository.CustomAuthRepository;
import com.tpex.auth.repository.TbMCompanyPlantMappingRepository;
import com.tpex.auth.repository.TbMPlantRepository;
import com.tpex.auth.repository.TbMRoleRepository;
import com.tpex.auth.repository.TbMRoleUserMappingRepository;
import com.tpex.auth.repository.TbMUserRepository;
import com.tpex.auth.util.ConstantUtils;
import com.tpex.auth.util.InitialValueUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {
	
	private final TbMUserRepository tbMUserRepository;
	private final InitialValueUtil tpexConfigUtilAuth;
	private final CustomAuthRepository customAuthRepository;
	private final TbMRoleRepository tbMRoleRepository;
	private final TbMRoleUserMappingRepository tbMRoleUserMappingRepository;
	private final TbMCompanyPlantMappingRepository tbMCompanyPlantMappingRepository;
	private final TbMPlantRepository tbMPlantRepository;
	
	private static final String TDEM = "tmap-em";
	private static final String TMT = "tmt";
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<TbMUserEntity> opt = tbMUserRepository.findById(username);
		return getUserInfo(username, opt);
	}
	
	public UserDetails loadUserByUniqueName(String uniqueName) throws UsernameNotFoundException {
		Optional<TbMUserEntity> opt = tbMUserRepository.findByAzureUniqueNameIgnoreCase(uniqueName);
		return getUserInfo(uniqueName, opt);
	}

	private UserDetails getUserInfo(String userName, Optional<TbMUserEntity> opt) {
		if (opt.isPresent()) {
			TbMUserEntity user = opt.get();
			UserDTO userDto = new UserDTO(user.getUserId(), ConstantUtils.DUMMY_PASS_BCRYPT, getGrantedAuthority(user.getUserId()));
			userDto.setCompanyCode(user.getCompanyCode());
			userDto.setFirstName(user.getName());
			return userDto;
		} else {
			throw new UsernameNotFoundException("User not found with: " + userName);
		}
	}
	
	public List<GrantedAuthority> getGrantedAuthority(String userId){
		if(StringUtils.isBlank(userId)) {
			return new ArrayList<>();
		}
		return customAuthRepository.findRoleNameByUserId(userId).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}
	
	public void createOrUpdateUser(UserInfo userInfo, List<AuthInfo> authInfo) {
		String userId = userInfo.getUserId();
		String comp = userInfo.getLocation();
		Optional<TbMUserEntity> opt = tbMUserRepository.findById(userId);
		TbMUserEntity userEntity = null;
		// insert user if not exist and update if exist
		boolean isUpdate = opt.isPresent();
		if(isUpdate) {
			userEntity = opt.get();
		} else {
			userEntity = new TbMUserEntity();
			userEntity.setUserId(userId);
		}
		// imply uniqueName must has value for register
		if(StringUtils.isNotBlank(userInfo.getUniqueName())) {
			userEntity.setAzureUniqueName(userInfo.getUniqueName());
		}
		userEntity.setName(userInfo.getFirstName());
		userEntity.setSurname(userInfo.getLastName());
		userEntity.setCompanyCode(userInfo.getLocation());
		userEntity.setEmail(userInfo.getEmail());
		
		tbMUserRepository.save(userEntity);
		
		if (!isUpdate) {
			// do if user created only not for updating
			List<String> roles = authInfo.stream()
					.filter(a -> tpexConfigUtilAuth.getConfigValue(ConstantUtils.SC2Config.SYSTEM_DOMAIN)
							.equals(a.getSystemDn()))
					.map(a -> a.getRoleDn().substring(0,
							a.getRoleDn().indexOf(".") != -1 ? a.getRoleDn().indexOf(".") : a.getRoleDn().length()))
					.collect(Collectors.toList());

			mappingRoleToUser(userId, roles);
			mappingCompanyPlantToUser(userId, comp, roles);
		}
	}

	private void mappingCompanyPlantToUser(String userId, String comp, List<String> roles) {
		List<TbMPlantEntity> plantList = null;
		if (isTdemAdmin(comp, roles)) {
			plantList = tbMPlantRepository.findAll();
		} else if (TDEM.equals(comp)) { // TDEM non admin
			plantList = tbMPlantRepository.findByCompanyCode(TMT);
		} else if (isTmtRayong(comp, roles)) {
			plantList = tbMPlantRepository.findById("R").stream().collect(Collectors.toList());
		} else { // normal case (TMT,STM,TMMIN)
			plantList = tbMPlantRepository.findByCompanyCode(comp);
		}
		tbMCompanyPlantMappingRepository.saveAll(plantList.stream().map(p -> {
			TbMCompanyPlantMappingEntity compMapping = new TbMCompanyPlantMappingEntity();
			compMapping.setUserId(userId);
			compMapping.setCompanyCode(p.getCompanyCode());
			compMapping.setPlantCode(p.getPlantCode());
			return compMapping;
		}).collect(Collectors.toList()));
	}

	private boolean isTmtRayong(String comp, List<String> roles) {
		return TMT.equals(comp) && roles.size() == 1 && roles.contains("RayongOff");
	}

	private boolean isTdemAdmin(String comp, List<String> roles) {
		return TDEM.equals(comp) && roles.contains("TDEMAdmin");
	}

	private void mappingRoleToUser(String userId, List<String> roles) {
		//substring domain for get role and save in role master and mapping role user
		//e.g. TDEMAdmin.tmap-em.toyota.co.th.asia to TDEMAdmin
		for (String role : roles) {
			Optional<TbMRoleEntity> roleEntities = tbMRoleRepository.findByRoleName(role);
			TbMRoleEntity roleInDb = null;
			// insert role master if not exist
			if(roleEntities.isPresent()) {
				roleInDb = roleEntities.get();
			} else {
				TbMRoleEntity roleEnt = new TbMRoleEntity();
				roleEnt.setRoleName(role);
				roleInDb = tbMRoleRepository.save(roleEnt);
			}
			// mapping role to user
			TbMRoleUserMappingEntity roleUserEnt = new TbMRoleUserMappingEntity();
			roleUserEnt.setRoleId(roleInDb.getRoleId());
			roleUserEnt.setUserId(userId);
			tbMRoleUserMappingRepository.save(roleUserEnt);
		}
	}

}

