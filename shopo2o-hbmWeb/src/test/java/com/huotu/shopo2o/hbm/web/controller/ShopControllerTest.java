package com.huotu.shopo2o.hbm.web.controller;

import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.hbm.web.CommonTestBase;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.Shop;
import com.huotu.shopo2o.service.service.ShopService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by helloztt on 2017-08-23.
 */
public class ShopControllerTest extends CommonTestBase {
    private static final String baseUrl = "/hbmWeb/shop";
    @Autowired
    private ShopService shopService;

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
    public void save() throws Exception {
        String saveUrl = baseUrl + "/save";
        String loginName = UUID.randomUUID().toString(), openTime = "09:00", closeTime = "18:00", deadlineTime = "20:00";
        Shop shop = mockShopWithoutSave();
        mockMvc.perform(post(saveUrl)
                .cookie(cookie)
                .param("loginName",loginName)
                .param("name",shop.getName())
                .param("areaCode",shop.getAreaCode())
                .param("telephone",shop.getTelephone())
                .param("provinceCode",shop.getProvinceCode())
                .param("cityCode",shop.getCityCode())
                .param("districtCode",shop.getDistrictCode())
                .param("address",shop.getAddress())
                .param("lan", String.valueOf(shop.getLan()))
                .param("lat", String.valueOf(shop.getLat()))
                .param("openTime",openTime)
                .param("closeTime",closeTime)
                .param("deadlineTime",deadlineTime)
                .param("logo",shop.getLogo())
                .param("erpId",shop.getErpId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        Page<Shop> shopList = shopService.findAll(mockCustomer.getCustomerId(),new PageRequest(0, Constant.PAGE_SIZE));
        assertEquals(1,shopList.getTotalElements());
        assertEquals(mockCustomer.getCustomerId(),shopList.getContent().get(0).getCustomer().getCustomerId());
    }

    private Shop mockShopWithoutSave(){
        Shop shop = new Shop();
        shop.setName(UUID.randomUUID().toString());
        shop.setAreaCode(UUID.randomUUID().toString());
        shop.setTelephone(UUID.randomUUID().toString());
        shop.setProvinceCode(UUID.randomUUID().toString());
        shop.setCityCode(UUID.randomUUID().toString());
        shop.setDistrictCode(UUID.randomUUID().toString());
        shop.setAddress(UUID.randomUUID().toString());
        shop.setLogo(UUID.randomUUID().toString());
        shop.setErpId(UUID.randomUUID().toString());
        shop.setLan(random.nextDouble());
        shop.setLat(random.nextDouble());
        return shop;
    }

    @Test
    public void changeOption() throws Exception {
        String changeOptionUrl = baseUrl + "/changeOption";
        MallCustomer mockShopCustomer = mockCustomer();
        Shop mockShop = mockShop(mockShopCustomer,mockCustomer);

        //禁用某个门店
        mockMvc.perform(post(changeOptionUrl)
                .cookie(cookie)
                .param("shopId", String.valueOf(mockShop.getId()))
                .param("isDisabled","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Shop realShop = shopRepository.findByIdAndCustomer_CustomerId(mockShop.getId(),mockCustomer.getCustomerId());
        assertNotNull(realShop);
        assertTrue(realShop.isDisabled());

        //禁用某个不存在的门店
        mockMvc.perform(post(changeOptionUrl)
                .cookie(cookie)
                .param("shopId", String.valueOf("-1"))
                .param("isDisabled","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void remove() throws Exception {
        String removeUrl = baseUrl + "/remove";
        MallCustomer mockShopCustomer = mockCustomer();
        Shop mockShop = mockShop(mockShopCustomer,mockCustomer);

        //删除某个门店
        mockMvc.perform(post(removeUrl)
                .cookie(cookie)
                .param("shopId", String.valueOf(mockShop.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Shop realShop = shopRepository.findByIdAndCustomer_CustomerId(mockShop.getId(),mockCustomer.getCustomerId());
        assertNotNull(realShop);
        assertTrue(realShop.isDisabled());
        assertTrue(realShop.isDeleted());
    }

}