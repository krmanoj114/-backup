package com.tpex.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.entity.MixPrivilegeMasterEntity;

@Repository
public interface MixPrivilegeMasterRepository extends JpaRepository<MixPrivilegeMasterEntity,Integer>{

	List<MixPrivilegeMasterEntity> findByCarFmlyCodeAndDestinationCode(String carFmlyCode, String destCode);

	List<MixPrivilegeMasterEntity> findByDestinationCodeAndCarFmlyCodeAndExporterCode(String destCode,
			String crFmlycode, String reExpCode);

	Optional<MixPrivilegeMasterEntity> findBydestinationCodeAndCarFmlyCodeAndExporterCodeAndEffFromAndEffTo(
			String destCode, String crFmlycode, String reExpCode, Date effFromDate, Date effToDate);

}
