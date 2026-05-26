/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.SampleRejectionRepository;
import org.itech.labSampleTracker.entities.SampleRejection;
import org.itech.labSampleTracker.service.SampleRejectionService;

import java.util.Collections;
import java.util.List;

/**
 * <h2>SampleRejectionServiceimpl</h2>
 */
@Service
@Transactional
public class SampleRejectionServiceImpl implements SampleRejectionService {

	@Autowired
	private SampleRejectionRepository samplerejectionRepo;

	@Override
	public SampleRejection create(SampleRejection d) {

		SampleRejection entity;

		try {
			entity = samplerejectionRepo.save(d);

		} catch (Exception ex) {
			return null;
		}
		return entity;
	}

	@Override
	public SampleRejection update(SampleRejection d) {
		SampleRejection c;

		try {
			c = samplerejectionRepo.saveAndFlush(d);

		} catch (Exception ex) {
			return null;
		}
		return c;
	}

	@Override
	public SampleRejection getOne(int id) {
		SampleRejection t;

		try {
			t = samplerejectionRepo.findById(id).orElse(null);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public List<SampleRejection> getAll() {
		List<SampleRejection> lst;

		try {
			lst = samplerejectionRepo.findAll();

		} catch (Exception ex) {
			return Collections.emptyList();
		}
		return lst;
	}

	@Override
	public long getTotal() {
		long total;

		try {
			total = samplerejectionRepo.count();
		} catch (Exception ex) {
			return 0;
		}
		return total;
	}

	@Override
	public boolean delete(int id) {
		try {
			samplerejectionRepo.deleteById(id);
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public boolean removeBySampleId(Integer sampleId) {
		try {
			return samplerejectionRepo.removeBySampleId(sampleId) > 0;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public SampleRejection getOneBySampleId(Integer sampleId) {
		try {
			return samplerejectionRepo.findOneBySampleId(sampleId);
		} catch (Exception ex) {
			return null;
		}
	}

}
