/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.AppRole;

import java.util.List;
/**
* <h2>AppRoleServiceimpl</h2>
*/
public interface AppRoleService  {
AppRole create(AppRole d);
AppRole update(AppRole d);
AppRole getOne(int id) ;
 List<AppRole> getAll();
long getTotal();
boolean delete(int id);
}
