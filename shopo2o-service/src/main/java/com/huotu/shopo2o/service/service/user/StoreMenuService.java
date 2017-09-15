package com.huotu.shopo2o.service.service.user;

import com.huotu.shopo2o.service.entity.StoreMenu;

import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
public interface StoreMenuService {
    List<StoreMenu> findPrimary();

    StoreMenu findById(String id);

    List<StoreMenu> findByParent(String parentId, int status);
}
