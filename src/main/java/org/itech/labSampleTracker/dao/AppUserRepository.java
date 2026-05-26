/*
 * Java domain class for entity "AppUser" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import java.util.List;
import java.util.Map;

import org.itech.labSampleTracker.entities.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <h2>AppUserRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:03
 * <p>
 * Description: "AppUser" Repository
 */


public interface AppUserRepository  extends JpaRepository<AppUser, Integer> , JpaSpecificationExecutor<AppUser> {
	
	public AppUser findByLogin(String login);

	@Query(value = "select u from AppUser u where u.isLocked = false and u.isActive = true ")
	public List<AppUser> findUsersIdName();

	public List<AppUser> findByIsActive(boolean active);

	public List<AppUser> findByIsLocked(boolean locked);

	public AppUser findByPhoneContact(String contact);

	/**
	 * Paginated user list for the redesigned admin index. Supports full-text
	 * search (login / first / last / phone), role (multi via IN), active /
	 * locked toggles and geographic scope (regionId / districtId / labId).
	 *
	 * Boolean-flag convention to side-step PostgreSQL's type inference around
	 * NULL list params; ORDER BY embedded in the query for stable pagination.
	 */
	@Query(value = "SELECT u.id, u.login, u.first_name, u.last_name, u.phone_contact, "
			+ "       u.role, u.user_type, u.user_level, "
			+ "       u.is_active, u.is_locked, u.last_login, u.password_expire_at "
			+ "FROM app_user u "
			+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR ("
			+ "      u.login ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR u.first_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR u.last_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
			+ "   OR u.phone_contact ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%'))) "
			+ "AND (:rolesActive = FALSE OR u.role IN (:roles)) "
			+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR u.is_active = CAST(:activeFlag AS BOOLEAN)) "
			+ "AND (CAST(:lockedFlag AS BOOLEAN) IS NULL OR u.is_locked = CAST(:lockedFlag AS BOOLEAN)) "
			+ "AND (CAST(:regionId AS INT) IS NULL OR u.id IN ("
			+ "      SELECT app_user_id FROM app_user_has_region WHERE region_id = CAST(:regionId AS INT))) "
			+ "AND (CAST(:districtId AS INT) IS NULL OR u.id IN ("
			+ "      SELECT app_user_id FROM app_user_has_district WHERE district_id = CAST(:districtId AS INT))) "
			+ "AND (CAST(:labId AS INT) IS NULL OR u.id IN ("
			+ "      SELECT app_user_id FROM app_user_has_lab WHERE lab_id = CAST(:labId AS INT))) "
			+ "ORDER BY u.is_active DESC, u.is_locked ASC, u.login ASC",
			countQuery = "SELECT COUNT(u.id) FROM app_user u "
					+ "WHERE (CAST(:searchText AS TEXT) IS NULL OR ("
					+ "      u.login ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR u.first_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR u.last_name ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%') "
					+ "   OR u.phone_contact ILIKE CONCAT('%', CAST(:searchText AS TEXT), '%'))) "
					+ "AND (:rolesActive = FALSE OR u.role IN (:roles)) "
					+ "AND (CAST(:activeFlag AS BOOLEAN) IS NULL OR u.is_active = CAST(:activeFlag AS BOOLEAN)) "
					+ "AND (CAST(:lockedFlag AS BOOLEAN) IS NULL OR u.is_locked = CAST(:lockedFlag AS BOOLEAN)) "
					+ "AND (CAST(:regionId AS INT) IS NULL OR u.id IN ("
					+ "      SELECT app_user_id FROM app_user_has_region WHERE region_id = CAST(:regionId AS INT))) "
					+ "AND (CAST(:districtId AS INT) IS NULL OR u.id IN ("
					+ "      SELECT app_user_id FROM app_user_has_district WHERE district_id = CAST(:districtId AS INT))) "
					+ "AND (CAST(:labId AS INT) IS NULL OR u.id IN ("
					+ "      SELECT app_user_id FROM app_user_has_lab WHERE lab_id = CAST(:labId AS INT)))",
			nativeQuery = true)
	Page<Map<String, Object>> findUsersAdvanced(Pageable pageable,
			@Param("searchText") String searchText,
			@Param("roles") List<String> roles,
			@Param("rolesActive") Boolean rolesActive,
			@Param("activeFlag") Boolean activeFlag,
			@Param("lockedFlag") Boolean lockedFlag,
			@Param("regionId") Integer regionId,
			@Param("districtId") Integer districtId,
			@Param("labId") Integer labId);
}
