package com.huotu.shopo2o.service.service.user.impl;

import com.huotu.shopo2o.service.entity.StoreMenu;
import com.huotu.shopo2o.service.repository.StoreMenuRepository;
import com.huotu.shopo2o.service.service.user.StoreMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hxh on 2017-09-15.
 */
@Service
public class StoreMenuServiceImpl implements StoreMenuService {
    @Autowired
    private StoreMenuRepository storeMenuRepository;

    @Override
    public List<StoreMenu> findPrimary() {
        return storeMenuRepository.findPrimary();
    }

    @Override
    public StoreMenu findById(String id) {
        return storeMenuRepository.findOne(id);
    }

    @Override
    public List<StoreMenu> findByParent(String parentId, int status) {
        return storeMenuRepository.findByParent_MenuIdAndIsDisabled(parentId, status);
    }
}
