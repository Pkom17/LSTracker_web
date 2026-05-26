/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.SampleRejection;

import java.util.List;

/**
 * <h2>SampleRejectionServiceimpl</h2>
 */
public interface SampleRejectionService {
	SampleRejection create(SampleRejection d);

	SampleRejection update(SampleRejection d);
	
	boolean removeBySampleId(Integer sampleId);
	
	SampleRejection getOneBySampleId(Integer sampleId);

	SampleRejection getOne(int id);

	List<SampleRejection> getAll();

	long getTotal();

	boolean delete(int id);
}
