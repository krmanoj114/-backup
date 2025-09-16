package com.tpex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.FinalDestEntity;

@Repository
public interface FinalDestRepository extends JpaRepository<FinalDestEntity, String> {
	
	List<FinalDestEntity> findAllByOrderByDestinationCdAsc();

}
