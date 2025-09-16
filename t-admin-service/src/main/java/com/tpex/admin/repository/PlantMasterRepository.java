package com.tpex.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.admin.entity.TbMPlant;

import java.util.List;


@Repository
public interface PlantMasterRepository extends JpaRepository<TbMPlant, String>{
    List<TbMPlant>  findByCompanyCode(String companyCode);
}
