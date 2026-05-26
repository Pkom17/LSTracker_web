/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.Ride;

import java.util.List;
/**
* <h2>RideServiceimpl</h2>
*/
public interface RideService  {
Ride create(Ride d);
Ride update(Ride d);
Ride getOne(int id) ;
 List<Ride> getAll();
long getTotal();
boolean delete(int id);
}
