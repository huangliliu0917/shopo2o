package com.huotu.shopo2o.service.service.shop.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.MallGoodRepository;
import com.huotu.shopo2o.service.repository.store.SupShopCatRepository;
import com.huotu.shopo2o.service.service.shop.SupShopCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
@Service
public class SupShopCatServiceImpl implements SupShopCatService {
    @Autowired
    private SupShopCatRepository supShopCatRepository;
    @Autowired
    private MallGoodRepository mallGoodRepository;
    @Autowired
    private HbmSupplierGoodsRepository hbmSupplierGoodsRepository;

    @Override
    public List<SupShopCat> findByStoreId(Long storeId) {
        List<SupShopCat> catList = supShopCatRepository.findByStoreIdAndParentIdAndDisabledOrderByOrder(storeId, 0, false);
        catList.forEach(cat -> {
            List<SupShopCat> subCatList = supShopCatRepository.findByStoreIdAndParentIdAndDisabledOrderByOrder(storeId, cat.getCatId(), false);
            cat.setSubShopCat(subCatList);
        });
        return catList;
    }

    @Override
    @Transactional
    public SupShopCat saveCat(SupShopCat supShopCat) {
        SupShopCat old = null;
        SupShopCat parent = null;
        if (supShopCat.getCatId() != null) {
            old = supShopCatRepository.findOne(supShopCat.getCatId());
        }
        if (supShopCat.getParentId() != null && supShopCat.getParentId() != 0) {
            parent = supShopCatRepository.findOne(supShopCat.getParentId());
        }
        if (old != null) {
            old.setCatImg(supShopCat.getCatImg());
            old.setCatName(supShopCat.getCatName());
            old.setDisabled(supShopCat.isDisabled());
            old.setOrder(supShopCat.getOrder());
            if (supShopCat.getParentId() != old.getParentId()) {
                old.setParentId(supShopCat.getParentId());
                old.setCatPath((parent == null ? "|" : parent.getCatPath()) + old.getCatId() + "|");
            }
            old.setStoreId(supShopCat.getStoreId());
        } else {
            old = supShopCat;
            old.setDisabled(false);
            old = supShopCatRepository.saveAndFlush(old);
            if (old.getCatId() == null) {
                supShopCatRepository.flush();
            }
            old.setCatPath((parent == null ? "|" : parent.getCatPath()) + old.getCatId() + "|");
        }
        old = supShopCatRepository.saveAndFlush(old);
        return old;
    }

    @Override
    public List<SupShopCat> findTopCatByStoreId(Long storeId) {
        List<SupShopCat> catList = supShopCatRepository.findByStoreIdAndParentIdAndDisabledOrderByOrder(storeId, 0, false);
        return catList;
    }

    @Override
    public SupShopCat findByCatId(Integer catId) {
        return supShopCatRepository.findOne(catId);
    }

    @Override
    @Transactional
    public ApiResult deleteCat(Integer catId) {
        SupShopCat cat = supShopCatRepository.findOne(catId);
        if (cat == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        List<SupShopCat> subCat = supShopCatRepository.findByParentId(catId);
        subCat.add(cat);
        //设置供应商商品和上架商品的供应商店铺分类为空
        List<Integer> catIdList = new ArrayList<>();
        subCat.forEach(p ->
                catIdList.add(p.getCatId())
        );
        hbmSupplierGoodsRepository.updateSupplierCatId(catIdList);
        mallGoodRepository.updateSupplierCatId(catIdList);
        //删除分类
        supShopCatRepository.delete(cat);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

}
