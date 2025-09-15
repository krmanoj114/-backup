package com.tpex.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.entity.PartMasterEntity;
import com.tpex.invoice.dto.InhouseShopDTO;
import com.tpex.invoice.dto.PartMasterDTO;
import com.tpex.invoice.dto.PartMasterRequestDTO;
import com.tpex.invoice.dto.PartMasterSearchRequestDto;
import com.tpex.util.ConstantUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PartMasterDao {
	@PersistenceContext
	EntityManager em;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<PartMasterEntity> search(PartMasterSearchRequestDto partMasterSearchRequestDto) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PartMasterEntity> cq = cb.createQuery(PartMasterEntity.class);

		Root<PartMasterEntity> partMater = cq.from(PartMasterEntity.class);

		Predicate cmpCdPredicate = cb.equal(partMater.get(ConstantUtils.CMP_CODE),
				partMasterSearchRequestDto.getCmpCd());

		ArrayList<Predicate> conditions = new ArrayList<>();
		conditions.add(cb.equal(partMater.get(ConstantUtils.PART_TYPE), partMasterSearchRequestDto.getPartType()));

		if (StringUtil.isNotBlank(partMasterSearchRequestDto.getPartName())) {
			conditions.add(
					cb.like(partMater.get(ConstantUtils.PART_NAME), partMasterSearchRequestDto.getPartName() + "%"));

		}
		if (StringUtil.isNotBlank(partMasterSearchRequestDto.getPartNo())) {
			conditions.add(cb.like(partMater.get(ConstantUtils.PART_NO), partMasterSearchRequestDto.getPartNo() + "%"));
		}

		cq.where(cb.or(conditions.toArray(new Predicate[conditions.size()])), cb.and(cmpCdPredicate));

		return em.createQuery(cq).getResultList();

	}

	@Transactional
	public int deletePartMaster(PartMasterRequestDTO partMasterRequestDTO) {
		int numberOfRowDeleted = 0;
		List<PartMasterDTO> data = partMasterRequestDTO.getData();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<PartMasterEntity> deleteCriteria = cb.createCriteriaDelete(PartMasterEntity.class);
		Root<PartMasterEntity> partMater = deleteCriteria.from(PartMasterEntity.class);
		ArrayList<Predicate> conditions = new ArrayList<>();
		for (PartMasterDTO partMasterDTO : data) {
			conditions.add(cb.equal(partMater.get(ConstantUtils.PART_NO), partMasterDTO.getPartNo()));
			conditions.add(cb.equal(partMater.get(ConstantUtils.CMP_CODE), partMasterDTO.getCmpCd()));

			deleteCriteria.where(conditions.toArray(new Predicate[conditions.size()]));

			numberOfRowDeleted += em.createQuery(deleteCriteria).executeUpdate();

			conditions.clear();

		}
		
		return numberOfRowDeleted;

	}

	@Transactional
	public void updatePartMaster(List<PartMasterEntity> data) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<PartMasterEntity> updateCriteria = cb.createCriteriaUpdate(PartMasterEntity.class);
		Root<PartMasterEntity> partMater = updateCriteria.from(PartMasterEntity.class);

		ArrayList<Predicate> conditions = new ArrayList<>();
		for (PartMasterEntity partMasterEntity : data) {
			conditions.add(cb.equal(partMater.get(ConstantUtils.PART_NO), partMasterEntity.getPartNo()));
			conditions.add(cb.equal(partMater.get(ConstantUtils.CMP_CODE), partMasterEntity.getCmpCode()));

			updateCriteria.set(ConstantUtils.PART_NAME, partMasterEntity.getPartName());
			updateCriteria.set(ConstantUtils.PART_TYPE, partMasterEntity.getType());
			updateCriteria.set(ConstantUtils.PART_INHOUSE_SHOP, partMasterEntity.getInhouseShop());
			updateCriteria.set(ConstantUtils.PART_WEIGHT,
					StringUtils.isBlank(partMasterEntity.getWeight()) ? 0 : partMasterEntity.getWeight());
			updateCriteria.set(ConstantUtils.PART_UPD_BY, partMasterEntity.getUpdateBy());
			updateCriteria.set(ConstantUtils.PART_UPD_DT, partMasterEntity.getUpdateDate());
			updateCriteria.set(ConstantUtils.PART_BATCH_UPD_DT, partMasterEntity.getBatchUpdateDate());

			updateCriteria.where(conditions.toArray(new Predicate[conditions.size()]));

			em.createQuery(updateCriteria).executeUpdate();

			conditions.clear();

		}

	}

	public List<InhouseShopDTO> getInhouseDropdownData(String companyCode) { 

		String sql = "SELECT distinct INS_SHOP_CD as insShopCd, CONCAT(tmis.ins_shop_cd, ' ', tmis.DESCRIPTION) AS insShopCdDesc "
				+ "    FROM tb_m_part tmp INNER JOIN "
				+ "    TB_M_INH_SHOP tmis ON tmis.ins_shop_cd = tmp.inhouse_shop  AND tmp.cmp_cd = :companyCode ";
		MapSqlParameterSource param = new MapSqlParameterSource().addValue("companyCode", companyCode);

		return namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<>(InhouseShopDTO.class));

	}
}
