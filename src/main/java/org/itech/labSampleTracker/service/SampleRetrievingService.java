/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.SampleRetrieving;

import java.util.List;
/**
* <h2>SampleRetrievingServiceimpl</h2>
*/
public interface SampleRetrievingService  {
SampleRetrieving create(SampleRetrieving d);
SampleRetrieving update(SampleRetrieving d);
SampleRetrieving getOne(int id) ;
 List<SampleRetrieving> getAll();
long getTotal();
boolean delete(int id);
}
