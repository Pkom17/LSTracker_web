/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.RideRepository;
import org.itech.labSampleTracker.entities.Ride;
import org.itech.labSampleTracker.service.RideService;

import java.util.Collections;
import java.util.List;

/**
* <h2>RideServiceimpl</h2>
*/
@Service
@Transactional
public class RideServiceImpl implements RideService {

@Autowired
private RideRepository rideRepo;

 @Override
    public Ride create(Ride d) {

       Ride entity;

        try {
            entity = rideRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public Ride update(Ride d) {
        Ride c;

        try {
            c = rideRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public Ride getOne(int id) {
        Ride t;

        try {
            t = rideRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<Ride> getAll() {
        List<Ride> lst;

        try {
            lst = rideRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = rideRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            rideRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
