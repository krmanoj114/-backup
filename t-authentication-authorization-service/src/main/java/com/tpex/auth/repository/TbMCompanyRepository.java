package com.tpex.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tpex.auth.entity.TbMCompanyEntity;

public interface TbMCompanyRepository extends JpaRepository<TbMCompanyEntity, String> {

}
