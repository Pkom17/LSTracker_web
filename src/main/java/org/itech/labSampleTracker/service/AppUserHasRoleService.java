/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.AppUserHasRole;

import java.util.List;
/**
* <h2>AppUserHasRoleServiceimpl</h2>
*/
public interface AppUserHasRoleService  {
AppUserHasRole create(AppUserHasRole d);
AppUserHasRole update(AppUserHasRole d);
AppUserHasRole getOne(int id) ;
 List<AppUserHasRole> getAll();
long getTotal();
boolean delete(int id);
}
