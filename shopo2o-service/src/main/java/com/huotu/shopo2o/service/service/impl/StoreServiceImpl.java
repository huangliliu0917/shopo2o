package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.MallCustomer_;
import com.huotu.shopo2o.service.entity.store.DistributionMarker;
import com.huotu.shopo2o.service.entity.store.DistributionRegion;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.store.Store_;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.store.DistributionMarkerRepository;
import com.huotu.shopo2o.service.repository.store.DistributionRegionRepository;
import com.huotu.shopo2o.service.repository.store.StoreRepository;
import com.huotu.shopo2o.service.service.MallCustomerService;
import com.huotu.shopo2o.service.service.StoreService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class StoreServiceImpl implements StoreService {
    private static final Log log = LogFactory.getLog(StoreServiceImpl.class);
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MallCustomerService customerService;
    @Autowired
    private DistributionMarkerRepository markerRepository;
    @Autowired
    private DistributionRegionRepository regionRepository;

    @Override
    public ApiResult saveStore(Long customerId, Store store, String loginName) {
        if (store.getId() == null || store.getId() == 0) {
            //调用商城接口，判断该登录名是否是有效的
            ApiResult apiResult = customerService.newCustomer(loginName, null, CustomerTypeEnum.STORE);
            if (apiResult.getCode() == 200 && apiResult.getData() instanceof MallCustomer) {
                MallCustomer shopCustomer = (MallCustomer) apiResult.getData();
                MallCustomer customer = customerService.findOne(customerId);
                store.setId(shopCustomer.getCustomerId());
                store.setCustomer(customer);
            } else {
                return apiResult;
            }
        }
        storeRepository.saveAndFlush(store);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS,store.getId());
    }

    @Override
    public List<DistributionMarker> saveMarker(Store store, Map<Long,DistributionMarker> markers) {
        //先删掉不在 markerList 中的点标记
        if(store.getDistributionMarkers() != null){
            store.getDistributionMarkers().stream().filter(p->
                !markers.containsKey(p.getNumber())
            ).forEach(marker-> {
                log.debug("id:"+marker.getId() + ",num:" + marker.getNumber());
                markerRepository.delete(marker);
            });
        }
        List<DistributionMarker> markerList = new ArrayList<>();
        markers.keySet().forEach(markerKey->{
            DistributionMarker newMarker = markers.get(markerKey);
            DistributionMarker oldMarker = markerRepository.findByStore_IdAndNumber(store.getId(),newMarker.getNumber());
            if(oldMarker == null){
                oldMarker = newMarker;
                oldMarker.setStore(store);
            }else{
                oldMarker.setLngLat(markers.get(markerKey).getLngLat());
            }
            markerList.add(oldMarker);
        });
        markerRepository.save(markerList);
        markerRepository.flush();
        return markerList;
    }

    @Override
    public List<DistributionRegion> saveRegion(Store store, Map<Long,DistributionRegion> regions) {
        //先删除不在 regionList 中的区域
        if(store.getDistributionDivisionRegions() != null){
            store.getDistributionDivisionRegions().stream().filter(p->!regions.containsKey(p.getId())).forEach(region->regionRepository.delete(region));
        }
        List<DistributionRegion> regionList = new ArrayList<>();
        regions.keySet().forEach(regionKey->{
            DistributionRegion oldRegion;
            DistributionRegion newRegion = regions.get(regionKey);
            if(newRegion.getId() == null || newRegion.getId() <= 0){
                oldRegion = newRegion;
                oldRegion.setStore(store);
            }else {
                oldRegion = regionRepository.findOne(regionKey);
                oldRegion.setName(newRegion.getName());
                oldRegion.setMarkerNum(newRegion.getMarkerNum());
                oldRegion.setColor(newRegion.getColor());
                oldRegion.setDistributionRegions(newRegion.getDistributionRegions());
            }
            regionList.add(oldRegion);
        });
        regionRepository.save(regionList);
        regionRepository.flush();
        return regionList;
    }

    @Override
    public Page<Store> findAll(Long customerId, Pageable pageable) {
        return storeRepository.findAll((root, query, cb) -> cb.and(
                cb.isFalse(root.get(Store_.isDeleted))
//                , cb.isFalse(root.get(Shop_.isDisabled))
                , cb.equal(root.get(Store_.customer).get(MallCustomer_.customerId), customerId)
        ), pageable);
    }

    @Override
    public Store findOne(Long shopId) {
        return storeRepository.findOne(shopId);
    }

    @Override
    public Store findOne(Long shopId, Long customerId) {
        return storeRepository.findByIdAndCustomer_CustomerIdAndIsDeletedFalse(shopId, customerId);
    }

    @Override
    public void disableStore(Store store, boolean isDisabled) {
        store.setDisabled(isDisabled);
        storeRepository.save(store);
    }

    @Override
    public void deleteStore(Store store) {
        store.setDisabled(true);
        store.setDeleted(true);
        storeRepository.save(store);
    }
}
