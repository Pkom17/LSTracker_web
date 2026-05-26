/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;
import org.itech.labSampleTracker.entities.SamplePackage;

import java.util.List;
/**
* <h2>SamplePackageServiceimpl</h2>
*/
public interface SamplePackageService  {
SamplePackage create(SamplePackage d);
SamplePackage update(SamplePackage d);
SamplePackage getOne(int id) ;
 List<SamplePackage> getAll();
long getTotal();
boolean delete(int id);
}
