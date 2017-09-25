package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by hxh on 2017-09-25.
 */
public class DeliveryControllerTest extends CommonTestBase {
    private static String BASE_URL = "/order";
    private MallCustomer user;
    private String userName;
    private String passWord;
    List<MallDelivery> mallDeliveryList = new ArrayList<>();
    List<MallOrder> mallOrders = new ArrayList<>();
    List<MallDelivery> mallReturnList = new ArrayList<>();

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
        for (int i = 0; i < 5; i++) {
            MallOrder mallOrder = mockMallOrder(user.getCustomerId().intValue());
            mallOrders.add(mallOrder);
        }
        for (int i = 0; i < mallOrders.size(); i++) {
            MallDelivery mallDelivery = mockMallDeliveryOrder(store, mallOrders.get(i), "delivery");
            mallDeliveryList.add(mallDelivery);
        }
        for (int i = 0; i < mallOrders.size(); i++) {
            MallDelivery mallDelivery = mockMallDeliveryOrder(store, mallOrders.get(i), "return");
            mallReturnList.add(mallDelivery);
        }
    }

    /**
     * 测试发货和退货单列表
     *
     * @throws Exception
     */
    @Test
    public void testShowDeliveryList() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        //测试发货列表
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/deliveries").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order/delivery/delivery_list"))
                .andExpect(model().attributeExists("deliveryList"))
                .andReturn();
        List<MallDelivery> deliveryList = (List<MallDelivery>) mvcResult.getModelAndView().getModel().get("deliveryList");
        Assert.assertNotNull(deliveryList);
        Assert.assertTrue(deliveryList.size() == mallDeliveryList.size());
        for (int i = 1; i < mallOrders.size(); i++) {
            Assert.assertEquals(mallDeliveryList.get(mallDeliveryList.size() - i).getDeliveryId(), deliveryList.get(i - 1).getDeliveryId());
        }
        //测试进货列表
        MvcResult mvcResult1 = mockMvc.perform(post(BASE_URL + "/deliveries")
                .session(mockHttpSession)
                .param("type", "return"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/delivery/return_list"))
                .andExpect(model().attributeExists("deliveryList"))
                .andReturn();
        List<MallDelivery> deliveryList1 = (List<MallDelivery>) mvcResult1.getModelAndView().getModel().get("deliveryList");
        Assert.assertNotNull(deliveryList1);
        Assert.assertTrue(deliveryList1.size() == mallReturnList.size());
        for (int i = 1; i <= mallOrders.size(); i++) {
            Assert.assertEquals(mallReturnList.get(mallDeliveryList.size() - i).getDeliveryId(), deliveryList1.get(i - 1).getDeliveryId());
        }
    }

    @Test
    public void testShowDeliveryInfo() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/deliveryInfo")
                .session(mockHttpSession)
                .param("deliveryId", mallDeliveryList.get(0).getDeliveryId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("deliveryInfo"))
                .andReturn();
        MallDelivery deliveryInfo = (MallDelivery) mvcResult.getModelAndView().getModel().get("deliveryInfo");
        Assert.assertNotNull(deliveryInfo);
        Assert.assertTrue(deliveryInfo.getDeliveryId() == mallDeliveryList.get(0).getDeliveryId());
    }
}
