package com.huotu.shopo2o.hbm.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.hbm.web.CommonTestBase;
import com.huotu.shopo2o.service.entity.DistributionRegion;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.service.StoreService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
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
                .param("lan", String.valueOf(store.getLan()))
                .param("lat", String.valueOf(store.getLat()))
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
        List<DistributionRegion> regionList = new ArrayList<>();
        int randomNum = random.nextInt(10);
        for(int i=0;i< randomNum ; i++){
            regionList.add(new DistributionRegion(random.nextDouble(),random.nextDouble()));
        }
        mockMvc.perform(post(saveUrl)
                .cookie(cookie)
                .param("storeId", String.valueOf(mockStore.getId()))
                .param("distributionRegions",objectMapper.writeValueAsString(regionList))
                .param("deliveryCost", String.valueOf(random.nextDouble()))
                .param("minCost",String.valueOf(random.nextDouble()))
                .param("freeCost",String.valueOf(random.nextDouble())))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value(200));
        Store realStore = storeService.findOne(mockStore.getId());
        assertEquals(randomNum, realStore.getDistributionRegions().size());
        for(int i = 0;i<randomNum;i++){
            assertEquals(regionList.get(i).getLan(), realStore.getDistributionRegions().get(i).getLan());
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
        store.setLan(random.nextDouble());
        store.setLat(random.nextDouble());
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