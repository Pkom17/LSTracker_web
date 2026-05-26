/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.SampleAtLabRepository;
import org.itech.labSampleTracker.entities.SampleAtLab;
import org.itech.labSampleTracker.service.SampleAtLabService;

import java.util.Collections;
import java.util.List;

/**
* <h2>SampleAtLabServiceimpl</h2>
*/
@Service
@Transactional
public class SampleAtLabServiceImpl implements SampleAtLabService {

@Autowired
private SampleAtLabRepository sampleatlabRepo;

 @Override
    public SampleAtLab create(SampleAtLab d) {

       SampleAtLab entity;

        try {
            entity = sampleatlabRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public SampleAtLab update(SampleAtLab d) {
        SampleAtLab c;

        try {
            c = sampleatlabRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public SampleAtLab getOne(int id) {
        SampleAtLab t;

        try {
            t = sampleatlabRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<SampleAtLab> getAll() {
        List<SampleAtLab> lst;

        try {
            lst = sampleatlabRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = sampleatlabRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            sampleatlabRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
