package com.tpex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.SsLineGroupBoxEntity;
@Repository
public interface SsLineGroupBoxRepository extends JpaRepository<SsLineGroupBoxEntity, String> {

}
