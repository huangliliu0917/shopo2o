package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hxh on 2017-09-25.
 */
public class AfterSaleControllerTest extends CommonTestBase {
    private static String BASE_URL = "/afterSale";
    private MallCustomer user;
    private String userName;
    private String passWord;
    List<MallAfterSales> afterSalesList = new ArrayList<>();
    List<MallOrder> mallOrderList = new ArrayList<>();

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
        for (int i = 0; i <= random.nextInt(5); i++) {
            MallOrder mallOrder = mockMallOrder(user.getCustomerId().intValue());
            mallOrderList.add(mallOrder);
        }
        for (int i = 0; i < mallOrderList.size(); i++) {
            MallAfterSales mallAfterSales = mockMallAfterSales(user, mallOrderList.get(i));
            afterSalesList.add(mallAfterSales);
        }
    }

    @Test
    public void testAfterSaleList() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        //测试发货列表
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/afterSaleList").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("afterSales"))
                .andReturn();
        List<MallAfterSales> mallAfterSalesList = (List<MallAfterSales>) mvcResult.getModelAndView().getModel().get("afterSales");
        Assert.assertNotNull(mallAfterSalesList);
        Assert.assertTrue(mallAfterSalesList.size() == afterSalesList.size());
        for (int i = 1; i <= afterSalesList.size(); i++) {
            Assert.assertTrue(afterSalesList.get(i - 1).getAfterId() == mallAfterSalesList.get(mallAfterSalesList.size() - i).getAfterId());
        }
    }

    @Test
    public void testAfterSaleDetail() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        //测试发货列表
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/afterSalesDetail")
                .session(mockHttpSession)
                .param("afterId", afterSalesList.get(0).getAfterId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("afterSales"))
                .andReturn();
        MallAfterSales afterSales = (MallAfterSales) mvcResult.getModelAndView().getModel().get("afterSales");
        Assert.assertNotNull(afterSales);
        Assert.assertTrue(afterSales.getAfterId() == afterSalesList.get(0).getAfterId());
    }
}
