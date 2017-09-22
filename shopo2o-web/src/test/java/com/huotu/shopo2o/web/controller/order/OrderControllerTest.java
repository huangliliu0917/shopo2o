package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.service.entity.MallCustomer;
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
 * Created by hxh on 2017-09-22.
 */
public class OrderControllerTest extends CommonTestBase {
    private static String BASE_URL = "/order";
    private MallCustomer user;
    private String userName;
    private String passWord;
    List<MallOrder> mallOrders = new ArrayList<>();

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
            mallOrders.add(mallOrder);
        }
    }

    /**
     * 测试订单列表页面
     *
     * @throws Exception
     */
    @Test
    public void testShowGoodsList() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult agentResult = mockMvc.perform(post(BASE_URL + "/getOrderList").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ordersList"))
                .andReturn();
        List<MallOrder> ordersList = (List<MallOrder>) agentResult.getModelAndView().getModel().get("ordersList");
        Assert.assertEquals("order/order_list", agentResult.getModelAndView().getViewName());
        Assert.assertNotNull(ordersList);
        Assert.assertTrue(ordersList.size() == mallOrders.size());
        for (int i = 1; i < mallOrders.size(); i++) {
            Assert.assertEquals(ordersList.get(ordersList.size() - i).getOrderId(), mallOrders.get(i - 1).getOrderId());
        }
    }
}
