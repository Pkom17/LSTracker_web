/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.AppUserHasRoleRepository;
import org.itech.labSampleTracker.entities.AppUserHasRole;
import org.itech.labSampleTracker.service.AppUserHasRoleService;

import java.util.Collections;
import java.util.List;

/**
* <h2>AppUserHasRoleServiceimpl</h2>
*/
@Service
@Transactional
public class AppUserHasRoleServiceImpl implements AppUserHasRoleService {

@Autowired
private AppUserHasRoleRepository appuserhasroleRepo;

 @Override
    public AppUserHasRole create(AppUserHasRole d) {

       AppUserHasRole entity;

        try {
            entity = appuserhasroleRepo.save(d);

        } catch (Exception ex) {
            return null;
        }
        return entity;
    }

    
    @Override
    public AppUserHasRole update(AppUserHasRole d) {
        AppUserHasRole c;

        try {
            c = appuserhasroleRepo.saveAndFlush(d);

        } catch (Exception ex) {
            return null;
        }
        return c;
    }


    @Override
    public AppUserHasRole getOne(int id) {
        AppUserHasRole t;

        try {
            t = appuserhasroleRepo.findById(id).orElse(null);

        } catch (Exception ex) {
            return null;
        }
        return t;
    }


    @Override
    public List<AppUserHasRole> getAll() {
        List<AppUserHasRole> lst;

        try {
            lst = appuserhasroleRepo.findAll();

        } catch (Exception ex) {
            return Collections.emptyList();
        }
        return lst;
    }


    @Override
    public long getTotal() {
        long total;

        try {
            total = appuserhasroleRepo.count();
        } catch (Exception ex) {
            return 0;
        }
        return total;
    }


    @Override
    public boolean delete(int id) {
        try {
            appuserhasroleRepo.deleteById(id);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

   

}
