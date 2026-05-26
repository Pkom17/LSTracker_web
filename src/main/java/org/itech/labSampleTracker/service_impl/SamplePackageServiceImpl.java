/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.SamplePackageRepository;
import org.itech.labSampleTracker.entities.SamplePackage;
import org.itech.labSampleTracker.service.SamplePackageService;

import java.util.Collections;
import java.util.List;

/**
* <h2>SamplePackageServiceimpl</h2>
*/
@Service
@Transactional
public class SamplePackageServiceImpl implements SamplePackageService {

@Autowired
private SamplePackageRepository samplepackageRepo;

 @Override
    public SamplePackage create(SamplePackage d) {

       SamplePackage entity;

        try {
            entity = samplepackageRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public SamplePackage update(SamplePackage d) {
        SamplePackage c;

        try {
            c = samplepackageRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public SamplePackage getOne(int id) {
        SamplePackage t;

        try {
            t = samplepackageRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<SamplePackage> getAll() {
        List<SamplePackage> lst;

        try {
            lst = samplepackageRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = samplepackageRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            samplepackageRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
