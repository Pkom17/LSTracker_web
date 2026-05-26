/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.SampleAtLab;

import java.util.List;
/**
* <h2>SampleAtLabServiceimpl</h2>
*/
public interface SampleAtLabService  {
SampleAtLab create(SampleAtLab d);
SampleAtLab update(SampleAtLab d);
SampleAtLab getOne(int id) ;
 List<SampleAtLab> getAll();
long getTotal();
boolean delete(int id);
}
