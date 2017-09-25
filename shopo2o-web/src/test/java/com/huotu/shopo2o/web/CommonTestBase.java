package com.huotu.shopo2o.web;

import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.shopo2o.service.repository.order.MallDeliveryRepository;
import com.huotu.shopo2o.service.repository.order.MallOrderRepository;
import com.huotu.shopo2o.service.repository.store.StoreRepository;
import com.huotu.shopo2o.web.config.MVCConfig;
import com.huotu.shopo2o.web.config.SecurityConfig;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by helloztt on 2017-08-23.
 */
@WebAppConfiguration
@ActiveProfiles({"test", "unit_test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, SecurityConfig.class})
public class CommonTestBase extends SpringWebTest {
    @Autowired
    protected MallCustomerRepository mallCustomerRepository;
    @Autowired
    protected MallAfterSalesRepository mallAfterSalesRepository;
    @Autowired
    protected StoreRepository storeRepository;
    @Autowired
    protected MallOrderRepository mallOrderRepository;
    @Autowired
    private MallDeliveryRepository mallDeliveryRepository;
    @Autowired
    protected MallPasswordEncoder passwordEncoder;

    protected Random random = new Random();
    protected String customerCookiesName = "UserID";

    protected MockHttpSession loginAs(String userName, String password) throws Exception {
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/code/verifyImage"))
                .andReturn().getRequest().getSession(true);
        String verifyCode = session.getAttribute("verifyCode").toString();
        session = (MockHttpSession) this.mockMvc.perform(MockMvcRequestBuilders.post("/login").session(session)
                .param("username", userName)
                .param("password", password)
                .param("roleType", String.valueOf(CustomerTypeEnum.HUOBAN_MALL.getCode()))
                .param("verifyCode",verifyCode)
        )
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        saveAuthedSession(session);
        return session;
    }

    protected MallCustomer mockCustomer(){
        MallCustomer customer = new MallCustomer();
        customer.setUsername(UUID.randomUUID().toString());
        customer.setNickName(UUID.randomUUID().toString());
        customer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        customer.setCustomerType(CustomerTypeEnum.STORE);
        return mallCustomerRepository.saveAndFlush(customer);
    }
    protected MallOrder mockMallOrder(int storeId){
        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderId(UUID.randomUUID().toString());
        mallOrder.setStoreId(storeId);
        mallOrder.setOrderStatus(OrderEnum.OrderStatus.ACTIVE);
        mallOrder.setOrderSourceType(OrderEnum.OrderSourceType.NORMAL);
        mallOrder.setPayStatus(OrderEnum.PayStatus.PAYED);
        mallOrder.setPaymentType(OrderEnum.PaymentOptions.ALIPEY_WEP);
        mallOrder.setCreateTime(new Date());
        return mallOrderRepository.saveAndFlush(mallOrder);
    }
    protected MallDelivery mockMallDeliveryOrder(Store store,MallOrder mallOrder,String type){
        MallDelivery mallDelivery = new MallDelivery();
        mallDelivery.setDeliveryId(UUID.randomUUID().toString());
        mallDelivery.setStore(store);
        mallDelivery.setOrder(mallOrder);
        mallDelivery.setType(type);
        mallDelivery.setCreateTime(new Date());
        return mallDeliveryRepository.saveAndFlush(mallDelivery);
    }
    protected MallAfterSales mockMallAfterSales(MallCustomer mallCustomer,MallOrder mallOrder){
        MallAfterSales mallAfterSales = new MallAfterSales();
        mallAfterSales.setStoreId(mallCustomer.getCustomerId());
        mallAfterSales.setOrderId(mallOrder.getOrderId());
        mallAfterSales.setAfterId(UUID.randomUUID().toString());
        mallAfterSales.setCreateTime(new Date());
        mallAfterSales.setAfterSaleType(AfterSaleEnum.AfterSaleType.REFUND);
        mallAfterSales.setAfterSaleStatus(AfterSaleEnum.AfterSaleStatus.REFUNDING);
        mallAfterSales.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.ONTER_REASON);
        mallAfterSales.setPayStatus(OrderEnum.PayStatus.PAYED);
        return mallAfterSalesRepository.saveAndFlush(mallAfterSales);
    }
}
