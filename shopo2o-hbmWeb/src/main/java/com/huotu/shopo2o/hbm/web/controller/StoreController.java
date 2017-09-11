package com.huotu.shopo2o.hbm.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.hbm.web.service.StaticResourceService;
import com.huotu.shopo2o.service.entity.store.DistributionMarker;
import com.huotu.shopo2o.service.entity.store.DistributionRegion;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.LngLat;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.service.MallCustomerService;
import com.huotu.shopo2o.service.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by helloztt on 2017-08-22.
 */
@Controller
@RequestMapping("/mall/store")
public class StoreController extends MallBaseController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private MallCustomerService customerService;
    @Autowired
    private StaticResourceService resourceService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/list")
    public String shopList(@ModelAttribute("customerId") Long customerId
            , @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo
            , Model model) {
        Pageable pageable = new PageRequest(pageNo - 1, Constant.PAGE_SIZE);
        Page<Store> shopPage = storeService.findAll(customerId, pageable);
        model.addAttribute("shopPage", shopPage);
        model.addAttribute("pageSize", Constant.PAGE_SIZE);
        model.addAttribute("pageNo", pageNo);
        return "store/store_manager";
    }

    @GetMapping("/edit")
    public String edit(@ModelAttribute("customerId") Long customerId
            , @RequestParam(value = "storeId", required = false, defaultValue = "0") Long storeId
            , Model model) {
        Store store = null;
        if (storeId != null && storeId != 0) {
            store = storeService.findOne(storeId, customerId);
            MallCustomer shopCustomer = customerService.findOne(storeId);
            if(shopCustomer != null){
                model.addAttribute("loginName",shopCustomer.getUsername());
            }
            try {
                URI imgUri = resourceService.getResource(StaticResourceService.huobanmallMode, store.getLogo());
                store.setMallLogoUri(imgUri.toString());
            } catch (URISyntaxException ignored) {
            }
        }
        if (store == null) {
            store = new Store();
        }
        model.addAttribute("currentData", store);
        return "store/edit_store";
    }

    @PostMapping("/saveStoreBaseInfo")
    @ResponseBody
    @Transactional
    public ApiResult save(@ModelAttribute("customerId") Long customerId
            , @RequestParam(required = false, defaultValue = "0") Long storeId, @RequestParam(required = false) String loginName, @RequestParam String name
            , @RequestParam String areaCode, @RequestParam String telephone
            , @RequestParam String provinceCode, @RequestParam String cityCode, @RequestParam String districtCode
            , @RequestParam String address, @RequestParam Double lng, @RequestParam Double lat
            , @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime openTime, @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime closeTime
            , @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime deadlineTime, @RequestParam String logo
            , @RequestParam String erpId) {
        Store store;
        if (storeId != null && storeId != 0) {
            store = storeService.findOne(storeId, customerId);
            if (store == null) {
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
            }
        } else {
            store = new Store();
            store.setCreateTime(new Date());
            //判断门店登录名是否唯一
            boolean isExist = customerService.isExist(loginName);
            if(isExist){
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR,"登录用户名已存在");
            }
            store.setCreateTime(new Date());
        }
        store.setName(name);
        store.setAreaCode(areaCode);
        store.setTelephone(telephone);
        store.setProvinceCode(provinceCode);
        store.setCityCode(cityCode);
        store.setDistrictCode(districtCode);
        store.setAddress(address);
        store.setLngLat(new LngLat(lng,lat));
        store.setOpenTime(openTime);
        store.setCloseTime(closeTime);
        store.setDeadlineTime(deadlineTime);
        store.setLogo(logo);
        store.setErpId(erpId);
        return storeService.saveStore(customerId, store, loginName);
    }

    @PostMapping("/saveStoreMoreInfo")
    @ResponseBody
    @Transactional
    public ApiResult save(@ModelAttribute("customerId")Long customerId,@RequestParam Long storeId
            ,@RequestParam String distributionRegions,@RequestParam String distributionMarkers,@RequestParam String distributionDivisionRegions
            ,@RequestParam Double deliveryCost,@RequestParam Double minCost,@RequestParam Double freeCost){
        Store store = storeService.findOne(storeId, customerId);
        if(store == null){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
        }
        //解析配送范围点坐标
        List<LngLat> distributionRegionList;
        try {
            distributionRegionList = objectMapper.readValue(distributionRegions,new TypeReference<List<LngLat>>() {});
        } catch (IOException e) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "配送范围解析失败");
        }
        if(distributionRegionList == null){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "请设置配送范围");
        }
        //解析配送区域点坐标
        Map<Long,DistributionMarker> distributionMarkerMap;
        try {
            distributionMarkerMap = objectMapper.readValue(distributionMarkers, new TypeReference<Map<Long,DistributionMarker>>() {});
        } catch (IOException e) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "配送区域点标记解析失败");
        }
        //解析配送区域
        Map<Long,DistributionRegion> distributionDivisionRegionMap;
        try {
            distributionDivisionRegionMap = objectMapper.readValue(distributionDivisionRegions, new TypeReference<Map<Long,DistributionRegion>>() {});
        } catch (IOException e) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "配送区域解析失败");
        }
        store.setDistributionRegions(distributionRegionList);
        store.setDistributionMarkers(storeService.saveMarker(store,distributionMarkerMap));
        store.setDistributionDivisionRegions(storeService.saveRegion(store,distributionDivisionRegionMap));
        store.setDeliveryCost(new BigDecimal(deliveryCost).setScale(2,BigDecimal.ROUND_HALF_DOWN));
        store.setMinCost(new BigDecimal(minCost).setScale(2,BigDecimal.ROUND_HALF_DOWN));
        store.setFreeCost(new BigDecimal(freeCost).setScale(2,BigDecimal.ROUND_HALF_DOWN));
        return storeService.saveStore(customerId, store,null);
    }

    @PostMapping("/changeOption")
    @ResponseBody
    public ApiResult changeOption(@ModelAttribute("customerId") Long customerId
            , @RequestParam Long storeId, @RequestParam boolean isDisabled) {
        Store store = storeService.findOne(storeId, customerId);
        if (store == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
        }
        if (store.isDisabled() != isDisabled) {
            storeService.disableStore(store, isDisabled);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/remove")
    @ResponseBody
    public ApiResult remove(@ModelAttribute("customerId") Long customerId
            , @RequestParam Long storeId) {
        Store store = storeService.findOne(storeId, customerId);
        if (store == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
        }
        if (!store.isDeleted()) {
            storeService.deleteStore(store);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
