package com.huotu.shopo2o.web.controller.shop;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import com.huotu.shopo2o.service.entity.config.SupShopConfig;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hxh on 2017-09-26.
 */
public class SupConfigControllerTest extends CommonTestBase{
    private static String BASE_URL = "/supConfig";
    private MallCustomer user;
    private String userName;
    private String passWord;
    SupBasicConfig supBasicConfig = new SupBasicConfig();

    @Before
    public void setInfo() throws Exception {
        userName = UUID.randomUUID().toString();
        passWord = UUID.randomUUID().toString();
        MallCustomer mallCustomer = new MallCustomer();
        mallCustomer.setNickName(UUID.randomUUID().toString());
        mallCustomer.setUsername(userName);
        mallCustomer.setPassword(passwordEncoder.encode(passWord));
        mallCustomer.setCustomerType(CustomerTypeEnum.STORE);
        MallCustomer storeCustomer = mallCustomerRepository.saveAndFlush(mallCustomer);
        Store store = new Store();
        store.setId(mallCustomer.getCustomerId());
        store.setCustomer(mallCustomer);
        store.setDeleted(false);
        store.setDisabled(false);
        storeCustomer.setStore(store);
        user = mallCustomerRepository.saveAndFlush(storeCustomer);
        SupShopConfig supShopConfig = mockSupShopConfig();
        supBasicConfig = mockSupBasicConfig(store,supShopConfig);
    }
    @Test
    public void testBasicConfig() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/basicConfig").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("config"))
                .andReturn();
        SupBasicConfig config = (SupBasicConfig) mvcResult.getModelAndView().getModel().get("config");
        Assert.assertTrue(config.getId()==supBasicConfig.getId());
    }
}
