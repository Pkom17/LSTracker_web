/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.TrackingEvent;

import java.util.List;
/**
* <h2>TrackingEventServiceimpl</h2>
*/
public interface TrackingEventService  {
TrackingEvent create(TrackingEvent d);
TrackingEvent update(TrackingEvent d);
TrackingEvent getOne(int id) ;
 List<TrackingEvent> getAll();
long getTotal();
boolean delete(int id);
}
