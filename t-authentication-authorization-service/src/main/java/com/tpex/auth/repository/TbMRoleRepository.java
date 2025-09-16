package com.tpex.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.auth.entity.TbMRoleEntity;

public interface TbMRoleRepository extends JpaRepository<TbMRoleEntity, Long> {
	
	public Optional<TbMRoleEntity> findByRoleName(String roleName);

}
