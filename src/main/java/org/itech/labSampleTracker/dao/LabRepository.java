package org.itech.labSampleTracker.dao;

import java.util.List;
import java.util.Map;

import org.itech.labSampleTracker.entities.Lab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabRepository extends JpaRepository<Lab, Integer>, JpaSpecificationExecutor<Lab> {

	/**
	 * Paginated admin lab list with full-text search and filters
	 * (region/district, lab type, active flag).
	 */
	@Query(value = "SELECT l.id, l.lab_name, l.lab_phone, l.lab_mail, l.lab_type, "
			+ "       l.longitude, l.latitude, l.is_active, "
			+ "       l.district_id, d.name AS district, d.region_id, r.name AS region "
			+ "FROM lab l "
			+ "LEFT JOIN district d ON d.id = l.district_id "
			+ "LEFT JOIN region r ON r.id = d.region_id "
			+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR ("
			+ "      l.lab_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR l.lab_phone ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR l.lab_mail ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR d.name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%'))) "
			+ "AND (CAST(:regionId AS INT) IS NULL OR d.region_id = CAST(:regionId AS INT)) "
			+ "AND (CAST(:districtId AS INT) IS NULL OR l.district_id = CAST(:districtId AS INT)) "
			+ "AND (:typesActive = FALSE OR l.lab_type IN (:labTypes)) "
			+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR l.is_active = CAST(:activeFlag AS BOOLEAN)) "
			+ "ORDER BY l.is_active DESC, r.name NULLS LAST, d.name NULLS LAST, l.lab_name",
			countQuery = "SELECT COUNT(l.id) FROM lab l "
					+ "LEFT JOIN district d ON d.id = l.district_id "
					+ "LEFT JOIN region r ON r.id = d.region_id "
					+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR ("
					+ "      l.lab_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR l.lab_phone ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR l.lab_mail ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR d.name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%'))) "
					+ "AND (CAST(:regionId AS INT) IS NULL OR d.region_id = CAST(:regionId AS INT)) "
					+ "AND (CAST(:districtId AS INT) IS NULL OR l.district_id = CAST(:districtId AS INT)) "
					+ "AND (:typesActive = FALSE OR l.lab_type IN (:labTypes)) "
					+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR l.is_active = CAST(:activeFlag AS BOOLEAN))",
			nativeQuery = true)
	Page<Map<String, Object>> findLabsAdvanced(Pageable pageable,
			@Param("searchText") String searchText,
			@Param("regionId") Integer regionId,
			@Param("districtId") Integer districtId,
			@Param("labTypes") List<String> labTypes,
			@Param("typesActive") Boolean typesActive,
			@Param("activeFlag") Boolean activeFlag);
}
