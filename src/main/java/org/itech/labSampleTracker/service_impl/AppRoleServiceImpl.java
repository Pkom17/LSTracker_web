/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.AppRoleRepository;
import org.itech.labSampleTracker.entities.AppRole;
import org.itech.labSampleTracker.service.AppRoleService;

import java.util.Collections;
import java.util.List;

/**
* <h2>AppRoleServiceimpl</h2>
*/
@Service
@Transactional
public class AppRoleServiceImpl implements AppRoleService {

@Autowired
private AppRoleRepository approleRepo;

 @Override
    public AppRole create(AppRole d) {

       AppRole entity;

        try {
            entity = approleRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public AppRole update(AppRole d) {
        AppRole c;

        try {
            c = approleRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public AppRole getOne(int id) {
        AppRole t;

        try {
            t = approleRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<AppRole> getAll() {
        List<AppRole> lst;

        try {
            lst = approleRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = approleRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            approleRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
