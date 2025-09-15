package com.tpex.barcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tpex.barcode.entity.TpexBarcodeEntity;
import com.tpex.barcode.entity.TpexBarcodeEntityPk;

@Repository
public interface TpexBarcodeRepository extends JpaRepository<TpexBarcodeEntity, TpexBarcodeEntityPk> {

}
