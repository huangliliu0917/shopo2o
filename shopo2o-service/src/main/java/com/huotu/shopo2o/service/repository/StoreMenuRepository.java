package com.huotu.shopo2o.service.repository;

import com.huotu.shopo2o.service.entity.StoreMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by hxh on 2017-09-15.
 */
public interface StoreMenuRepository extends JpaRepository<StoreMenu,String>{
    @Query("select menu from StoreMenu menu where menu.parent is null and menu.isDisabled=0 order by menu.sortNum")
    List<StoreMenu> findPrimary();

    List<StoreMenu> findByParent_MenuIdAndIsDisabled(String menuId, int status);
}
