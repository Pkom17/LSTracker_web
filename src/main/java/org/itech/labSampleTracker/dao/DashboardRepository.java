package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.Sample;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository extends Repository<Sample, Integer> {

	// FIX bug "rejetés sans réceptionnés" : received et accepted s'appuient
	// désormais sur les colonnes de dates persistantes (deliver_at_*_date,
	// accepted_at_*_date) au lieu du statut courant qui peut avoir évolué.
	// Garantit que rejected ⊆ accepted ⊆ received sur n'importe quelle
	// période et n'importe quel filtre géographique.
	@Query(value = "SELECT COUNT(*) AS all_count, "
			+ "  SUM(CASE WHEN ss.status = 'ON_TRANSIT' THEN 1 ELSE 0 END) AS in_transit, "
			+ "  SUM(CASE WHEN COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) IS NOT NULL THEN 1 ELSE 0 END) AS received, "
			+ "  SUM(CASE WHEN COALESCE(s.accepted_at_lab_date, s.accepted_at_hub_date) IS NOT NULL THEN 1 ELSE 0 END) AS accepted, "
			+ "  SUM(CASE WHEN s.analysis_released_date IS NOT NULL THEN 1 ELSE 0 END) AS result_ready, "
			+ "  SUM(CASE WHEN s.result_collection_date IS NOT NULL THEN 1 ELSE 0 END) AS result_collected, "
			+ "  SUM(CASE WHEN s.result_delivery_date IS NOT NULL THEN 1 ELSE 0 END) AS result_on_site, "
			+ "  SUM(CASE WHEN ss.status = 'NON_CONFORM' AND s.rejection_date IS NOT NULL THEN 1 ELSE 0 END) AS rejected, "
			+ "  SUM(CASE WHEN ss.status = 'ANALYSIS_FAILED' AND s.analysis_completed_date IS NOT NULL THEN 1 ELSE 0 END) AS failed "
			+ " FROM sample s JOIN sample_status ss ON ss.id = s.sample_status_id "
			+ " LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ " LEFT JOIN site st ON st.id = sr.site_id LEFT JOIN district d ON d.id = st.district_id WHERE "
			+ "  (CAST(:startDate AS date) IS NULL OR CAST(s.collection_date as DATE) >= :startDate) AND "
			+ "  (CAST(:endDate   AS date) IS NULL OR CAST(s.collection_date as DATE) <= :endDate) AND "
			+ "  (:siteId IS NULL OR st.id = :siteId ) AND "
			+ "  (:districtId IS NULL OR st.district_id = :districtId) AND "
			+ "  (:regionId   IS NULL OR d.region_id = :regionId)", nativeQuery = true)
	List<Object[]> summary(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);

	@Query(value = "SELECT CAST(d AS DATE) AS day, COUNT(s.id) AS cnt "
			+ "FROM generate_series(:startDate, :endDate, interval '1 day') d "
			+ "LEFT JOIN sample s ON coalesce(CAST(s.pickup_date AS DATE), CAST(s.created_at AS DATE)) = CAST(d AS DATE) "
			+ "AND (CAST(:startDate AS date) IS NULL OR coalesce(CAST(s.pickup_date AS DATE), CAST(s.created_at AS DATE)) >= :startDate) "
			+ "AND (CAST(:endDate   AS date) IS NULL OR coalesce(CAST(s.pickup_date AS DATE), CAST(s.created_at AS DATE)) <= :endDate) "
			+ "LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ "LEFT JOIN site st ON st.id = sr.site_id LEFT JOIN district dis ON dis.id = st.district_id "
			+ "AND (:siteId IS NULL OR st.id = :siteId ) AND "
			+ "(:districtId IS NULL OR st.district_id = :districtId) AND "
			+ "(:regionId IS NULL OR dis.region_id = :regionId) "
			+ "GROUP BY CAST(d AS DATE) ORDER BY CAST(d AS DATE)", nativeQuery = true)
	List<Object[]> tsCollected(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);

	@Query(value = "SELECT CAST(d AS DATE) AS day, COUNT(s.id) AS cnt "
			+ "FROM generate_series(:startDate, :endDate, interval '1 day') d "
			+ "LEFT JOIN sample s ON coalesce(CAST(s.deliver_at_lab_date AS DATE),CAST(s.deliver_at_hub_date AS DATE)) = CAST(d AS DATE) "
			+ "AND (CAST(:startDate AS date) IS NULL OR coalesce(CAST(s.deliver_at_lab_date AS DATE),CAST(s.deliver_at_hub_date AS DATE)) >= :startDate) "
			+ "AND (CAST(:endDate   AS date) IS NULL OR coalesce(CAST(s.deliver_at_lab_date AS DATE),CAST(s.deliver_at_hub_date AS DATE)) <= :endDate) "
			+ "LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ "LEFT JOIN site st ON st.id = sr.site_id " + "LEFT JOIN district dis ON dis.id = st.district_id "
			+ "AND (:siteId IS NULL OR st.id = :siteId ) AND "
			+ "(:districtId IS NULL OR st.district_id = :districtId) AND "
			+ "(:regionId IS NULL OR dis.region_id = :regionId) "
			+ "GROUP BY CAST(d AS DATE) ORDER BY CAST(d AS DATE)", nativeQuery = true)
	List<Object[]> tsDeposited(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);

	@Query(value = "SELECT CAST(d AS DATE) AS day, COUNT(s.id) AS cnt "
			+ "FROM generate_series(:startDate, :endDate, interval '1 day') d "
			+ "LEFT JOIN sample s ON CAST(s.analysis_released_date AS DATE) = CAST(d AS DATE) "
			+ "AND (CAST(:startDate AS date) IS NULL OR CAST(s.analysis_released_date AS DATE) >= :startDate) "
			+ "AND (CAST(:endDate   AS date) IS NULL OR CAST(s.analysis_released_date AS DATE) <= :endDate) "
			+ "LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ "LEFT JOIN site st ON st.id = sr.site_id " + "LEFT JOIN district dis ON dis.id = st.district_id "
			+ "AND (:siteId IS NULL OR st.id = :siteId ) AND "
			+ "(:districtId IS NULL OR st.district_id = :districtId) AND "
			+ "(:regionId IS NULL OR dis.region_id = :regionId) "
			+ "GROUP BY CAST(d AS DATE) ORDER BY CAST(d AS DATE)", nativeQuery = true)
	List<Object[]> tsAnalysed(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);

	@Query(value = "SELECT CAST(d AS DATE) AS day, COUNT(s.id) AS cnt "
			+ "FROM generate_series(:startDate, :endDate, interval '1 day') d "
			+ "LEFT JOIN sample s ON CAST(s.result_delivery_date AS DATE) = CAST(d AS DATE) "
			+ "AND (CAST(:startDate AS date) IS NULL OR CAST(s.result_delivery_date AS DATE) >= :startDate) "
			+ "AND (CAST(:endDate   AS date) IS NULL OR CAST(s.result_delivery_date AS DATE) <= :endDate) "
			+ "LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ "LEFT JOIN site st ON st.id = sr.site_id " + "LEFT JOIN district dis ON dis.id = st.district_id "
			+ "AND (:siteId IS NULL OR st.id = :siteId ) AND "
			+ "(:districtId IS NULL OR st.district_id = :districtId) AND "
			+ "(:regionId IS NULL OR dis.region_id = :regionId) "
			+ "GROUP BY CAST(d AS DATE) ORDER BY CAST(d AS DATE)", nativeQuery = true)
	List<Object[]> tsDelivered(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);

	@Query(value = "WITH base AS ( SELECT s.*, "
			+ "         COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date)  AS deposit_dt, "
			+ "         COALESCE(s.accepted_at_lab_date, s.accepted_at_hub_date) AS accepted_dt " + "  FROM sample s "
			+ "  LEFT JOIN sample_retrieving sr ON sr.id = s.sample_retrieving_id "
			+ "  LEFT JOIN site st           ON st.id  = sr.site_id "
			+ "  LEFT JOIN district dis      ON dis.id = st.district_id "
			+ "  WHERE (CAST(:startDate AS date) IS NULL OR CAST(s.collection_date AS DATE) >= :startDate) "
			+ "    AND (CAST(:endDate   AS date) IS NULL OR CAST(s.collection_date AS DATE) <= :endDate) "
			+ "    AND (:siteId     IS NULL OR st.id          = :siteId) "
			+ "    AND (:districtId IS NULL OR st.district_id = :districtId) "
			+ "    AND (:regionId   IS NULL OR dis.region_id  = :regionId) ), durations AS ( "
			+ "  SELECT 'collecte→dépôt' AS step, sample_type_id, EXTRACT(EPOCH FROM (b.deposit_dt - COALESCE(b.pickup_date, b.created_at))) / 86400.0 AS days "
			+ "  FROM base b "
			+ "  WHERE b.deposit_dt IS NOT NULL AND COALESCE(b.pickup_date, b.created_at) IS NOT NULL AND b.deposit_dt >= COALESCE(b.pickup_date, b.created_at) "
			+ "  UNION ALL "
			+ "  SELECT 'dépôt→réception' AS step, sample_type_id, EXTRACT(EPOCH FROM (b.accepted_dt - b.deposit_dt)) / 86400.0 AS days FROM base b "
			+ "  WHERE b.accepted_dt IS NOT NULL AND b.deposit_dt IS NOT NULL AND b.accepted_dt >= b.deposit_dt "
			+ "  UNION ALL "
			+ "  SELECT 'réception→analyse' AS step, sample_type_id, EXTRACT(EPOCH FROM (b.analysis_completed_date - b.accepted_dt)) / 86400.0 AS days FROM base b "
			+ "  WHERE b.analysis_completed_date IS NOT NULL AND b.accepted_dt IS NOT NULL AND b.analysis_completed_date >= b.accepted_dt "
			+ "  UNION ALL "
			+ "  SELECT 'analyse→résultat prêt' AS step, sample_type_id, EXTRACT(EPOCH FROM (b.analysis_released_date - b.analysis_completed_date)) / 86400.0 AS days FROM base b "
			+ "  WHERE b.analysis_released_date IS NOT NULL AND b.analysis_completed_date IS NOT NULL AND b.analysis_released_date >= b.analysis_completed_date "
			+ "  UNION ALL "
			+ "  SELECT 'résultat prêt→livraison' AS step, sample_type_id, EXTRACT(EPOCH FROM (b.result_delivery_date - b.analysis_released_date)) / 86400.0 AS days FROM base b "
			+ "  WHERE b.result_delivery_date IS NOT NULL AND b.analysis_released_date IS NOT NULL AND b.result_delivery_date >= b.analysis_released_date "
			+ ") SELECT st.name AS sample_type, step, COUNT(*) AS n, "
			+ "       CEIL(AVG(days)) AS avg_days, CEIL(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY days)) AS median_days, "
			+ "       CEIL(MIN(days)) AS min_days, CEIL(MAX(days)) AS max_days "
			+ "FROM durations du JOIN sample_type st ON st.id = du.sample_type_id GROUP BY st.name, step "
			+ "ORDER BY st.name, step", nativeQuery = true)
	List<Object[]> stepDurationsDays(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("regionId") Integer regionId, @Param("districtId") Integer districtId,
			@Param("siteId") Integer siteId);
}