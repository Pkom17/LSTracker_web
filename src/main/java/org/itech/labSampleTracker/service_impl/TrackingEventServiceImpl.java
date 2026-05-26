/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.TrackingEventRepository;
import org.itech.labSampleTracker.entities.TrackingEvent;
import org.itech.labSampleTracker.service.TrackingEventService;

import java.util.Collections;
import java.util.List;

/**
* <h2>TrackingEventServiceimpl</h2>
*/
@Service
@Transactional
public class TrackingEventServiceImpl implements TrackingEventService {

@Autowired
private TrackingEventRepository trackingeventRepo;

 @Override
    public TrackingEvent create(TrackingEvent d) {

       TrackingEvent entity;

        try {
            entity = trackingeventRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public TrackingEvent update(TrackingEvent d) {
        TrackingEvent c;

        try {
            c = trackingeventRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public TrackingEvent getOne(int id) {
        TrackingEvent t;

        try {
            t = trackingeventRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<TrackingEvent> getAll() {
        List<TrackingEvent> lst;

        try {
            lst = trackingeventRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = trackingeventRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            trackingeventRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
