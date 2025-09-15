package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.ExporterCodeMasterEntity;

@Repository
public interface ExporterCodeMasterRepository extends JpaRepository<ExporterCodeMasterEntity,Integer>{

}
