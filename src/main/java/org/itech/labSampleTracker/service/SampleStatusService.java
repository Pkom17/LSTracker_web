/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.SampleStatus;

import java.util.List;

/**
 * <h2>SampleStatusServiceimpl</h2>
 */
public interface SampleStatusService {
	SampleStatus create(SampleStatus d);

	SampleStatus update(SampleStatus d);

	SampleStatus getOne(int id);

	List<SampleStatus> getAll();

	long getTotal();

	boolean delete(int id);

	SampleStatus findByStatus(String status);
}
