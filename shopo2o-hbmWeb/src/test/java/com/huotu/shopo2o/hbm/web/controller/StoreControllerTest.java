package com.huotu.shopo2o.hbm.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.hbm.web.CommonTestBase;
import com.huotu.shopo2o.service.entity.store.DistributionMarker;
import com.huotu.shopo2o.service.entity.store.DistributionRegion;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.LngLat;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.service.StoreService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * Created by helloztt on 2017-08-23.
 */
public class StoreControllerTest extends CommonTestBase {
    private static final String baseUrl = "/mall/store";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private StoreService storeService;

    private MallCustomer mockCustomer;
    private Cookie cookie;

    @Before
    public void init(){
        mockCustomer = mockCustomer();
        cookie = new Cookie(customerCookiesName,String.valueOf(mockCustomer.getCustomerId()));
    }

    @Test
    public void shopList() throws Exception {

    }

    @Test
    public void edit() throws Exception {

    }

    @Test
    public void saveBaseInfo() throws Exception {
        String saveUrl = baseUrl + "/saveStoreBaseInfo";
        String loginName = UUID.randomUUID().toString(), openTime = "09:00", closeTime = "18:00", deadlineTime = "20:00";
        Store store = mockShopWithoutSave();
        mockMvc.perform(post(saveUrl)
                .cookie(cookie)
                .param("loginName",loginName)
                .param("name", store.getName())
                .param("areaCode", store.getAreaCode())
                .param("telephone", store.getTelephone())
                .param("provinceCode", store.getProvinceCode())
                .param("cityCode", store.getCityCode())
                .param("districtCode", store.getDistrictCode())
                .param("address", store.getAddress())
                .param("lan", String.valueOf(store.getLngLat().getLng()))
                .param("lat", String.valueOf(store.getLngLat().getLat()))
                .param("openTime",openTime)
                .param("closeTime",closeTime)
                .param("deadlineTime",deadlineTime)
                .param("logo", store.getLogo())
                .param("erpId", store.getErpId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        Page<Store> shopList = storeService.findAll(mockCustomer.getCustomerId(),new PageRequest(0, Constant.PAGE_SIZE));
        assertEquals(1,shopList.getTotalElements());
        assertEquals(mockCustomer.getCustomerId(),shopList.getContent().get(0).getCustomer().getCustomerId());
    }

    @Test
    public void saveMoreInfo() throws Exception{
        String saveUrl = baseUrl + "/saveStoreMoreInfo";
        MallCustomer mockShopCustomer = mockCustomer();
        Store mockStore = mockShop(mockShopCustomer,mockCustomer);
        List<LngLat> regionList = new ArrayList<>();
        int randomNum = random.nextInt(10);
        for(int i=0;i< randomNum ; i++){
            regionList.add(new LngLat(random.nextDouble(),random.nextDouble()));
        }
        String distributionMarkers = "{\"1\":{\"number\":1,\"lngLat\":{\"O\":30.208392,\"M\":120.22380699999997,\"lng\":120.223807,\"lat\":30.208392},\"isUsed\":true},\"2\":{\"number\":2,\"lngLat\":{\"O\":30.211989420618668,\"M\":120.21789205856987,\"lng\":120.217892,\"lat\":30.211989}},\"3\":{\"number\":3,\"lngLat\":{\"O\":30.211655644859277,\"M\":120.23351324387261,\"lng\":120.233513,\"lat\":30.211656}},\"4\":{\"number\":4,\"lngLat\":{\"O\":30.20268036105437,\"M\":120.23394239731499,\"lng\":120.233942,\"lat\":30.20268},\"isUsed\":true},\"5\":{\"number\":5,\"lngLat\":{\"O\":30.20438646974161,\"M\":120.21214140244194,\"lng\":120.212141,\"lat\":30.204386},\"isUsed\":true}}";
        String distributionDivisionRegions = "{\"-1\":{\"name\":\"区域名称1\",\"markerNum\":\"1,5,4\",\"color\":\"#3480b8\",\"distributionRegions\":[{\"lng\":120.223807,\"lat\":30.208392},{\"lng\":120.212141,\"lat\":30.204386},{\"lng\":120.233942,\"lat\":30.20268}]}}";
        mockMvc.perform(post(saveUrl)
                .cookie(cookie)
                .param("storeId", String.valueOf(mockStore.getId()))
                .param("distributionRegions",objectMapper.writeValueAsString(regionList))
                .param("distributionMarkers",distributionMarkers)
                .param("distributionDivisionRegions",distributionDivisionRegions)
                .param("deliveryCost", String.valueOf(random.nextDouble()))
                .param("minCost",String.valueOf(random.nextDouble()))
                .param("freeCost",String.valueOf(random.nextDouble())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value(200));
        Store realStore = storeService.findOne(mockStore.getId());
        assertEquals(randomNum, realStore.getDistributionRegions().size());
        for(int i = 0;i<randomNum;i++){
            assertEquals(regionList.get(i).getLng(), realStore.getDistributionRegions().get(i).getLng());
            assertEquals(regionList.get(i).getLat(), realStore.getDistributionRegions().get(i).getLat());
        }
    }

    private Store mockShopWithoutSave(){
        Store store = new Store();
        store.setName(UUID.randomUUID().toString());
        store.setAreaCode(UUID.randomUUID().toString());
        store.setTelephone(UUID.randomUUID().toString());
        store.setProvinceCode(UUID.randomUUID().toString());
        store.setCityCode(UUID.randomUUID().toString());
        store.setDistrictCode(UUID.randomUUID().toString());
        store.setAddress(UUID.randomUUID().toString());
        store.setLogo(UUID.randomUUID().toString());
        store.setErpId(UUID.randomUUID().toString());
        store.setLngLat(new LngLat(random.nextDouble(),random.nextDouble()));
        return store;
    }

    @Test
    public void changeOption() throws Exception {
        String changeOptionUrl = baseUrl + "/changeOption";
        MallCustomer mockShopCustomer = mockCustomer();
        Store mockStore = mockShop(mockShopCustomer,mockCustomer);

        //禁用某个门店
        mockMvc.perform(post(changeOptionUrl)
                .cookie(cookie)
                .param("storeId", String.valueOf(mockStore.getId()))
                .param("isDisabled","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Store realStore = storeRepository.findByIdAndCustomer_CustomerId(mockStore.getId(),mockCustomer.getCustomerId());
        assertNotNull(realStore);
        assertTrue(realStore.isDisabled());

        //禁用某个不存在的门店
        mockMvc.perform(post(changeOptionUrl)
                .cookie(cookie)
                .param("storeId", String.valueOf("-1"))
                .param("isDisabled","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void remove() throws Exception {
        String removeUrl = baseUrl + "/remove";
        MallCustomer mockShopCustomer = mockCustomer();
        Store mockStore = mockShop(mockShopCustomer,mockCustomer);

        //删除某个门店
        mockMvc.perform(post(removeUrl)
                .cookie(cookie)
                .param("storeId", String.valueOf(mockStore.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Store realStore = storeRepository.findByIdAndCustomer_CustomerId(mockStore.getId(),mockCustomer.getCustomerId());
        assertNotNull(realStore);
        assertTrue(realStore.isDisabled());
        assertTrue(realStore.isDeleted());
    }

}