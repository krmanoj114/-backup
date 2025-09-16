package com.tpex.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.auth.entity.TbMUserEntity;

public interface TbMUserRepository extends JpaRepository<TbMUserEntity, String>{

	public Optional<TbMUserEntity> findByAzureUniqueNameIgnoreCase(String uniqueName);

}
