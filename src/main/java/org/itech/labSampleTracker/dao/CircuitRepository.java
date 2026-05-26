/*
 * Java domain class for entity "Circuit" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import java.util.Map;

import org.itech.labSampleTracker.entities.Circuit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CircuitRepository extends JpaRepository<Circuit, Integer>, JpaSpecificationExecutor<Circuit> {

	public Circuit findByCircuitNumber(String circuitNumber);

	/**
	 * Paginated admin circuit list with search and active filter.
	 * Returns each circuit with its site count and (optional) assigned conveyor.
	 */
	@Query(value = "SELECT c.id, c.circuit_number, c.is_active, c.created_at, "
			+ "       (SELECT COUNT(*) FROM circuit_site cs WHERE cs.circuit_id = c.id) AS site_count, "
			+ "       (SELECT STRING_AGG(u.first_name || ' ' || u.last_name, ', ') "
			+ "         FROM app_user_has_circuit auhc "
			+ "         JOIN app_user u ON u.id = auhc.app_user_id "
			+ "         WHERE auhc.circuit_id = c.id) AS conveyors "
			+ "FROM circuit c "
			+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR "
			+ "       c.circuit_number ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%')) "
			+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR c.is_active = CAST(:activeFlag AS BOOLEAN)) "
			+ "ORDER BY c.is_active DESC, c.circuit_number",
			countQuery = "SELECT COUNT(c.id) FROM circuit c "
					+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR "
					+ "       c.circuit_number ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%')) "
					+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR c.is_active = CAST(:activeFlag AS BOOLEAN))",
			nativeQuery = true)
	Page<Map<String, Object>> findCircuitsAdvanced(Pageable pageable,
			@Param("searchText") String searchText,
			@Param("activeFlag") Boolean activeFlag);
}
