package com.huotu.shopo2o.web.controller;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.web.CommonTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by hxh on 2017-09-22.
 */
public class TestAuthorControllerLoad extends CommonTestBase{
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Test
    public void test() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        MallCustomer mallCustomer = new MallCustomer();
        mallCustomer.setNickName(UUID.randomUUID().toString());
        mallCustomer.setUsername(userName);
        mallCustomer.setPassword(passwordEncoder.encode(password));
        mallCustomer.setCustomerType(CustomerTypeEnum.STORE);
        MallCustomer storeCustomer = mallCustomerRepository.saveAndFlush(mallCustomer);
        Store store = new Store();
        store.setId(mallCustomer.getCustomerId());
        store.setCustomer(mallCustomer);
        store.setDeleted(false);
        store.setDisabled(false);
        storeCustomer.setStore(store);
        mallCustomerRepository.saveAndFlush(storeCustomer);
        MockHttpSession session = (MockHttpSession)this.mockMvc.perform(get("/code/verifyImage"))
                .andReturn().getRequest().getSession(true);
       String verifyCode = session.getAttribute("verifyCode").toString();
        session = (MockHttpSession) this.mockMvc.perform(post("/login").session(session)
                .param("username", userName)
                .param("password", password)
                .param("roleType", String.valueOf(CustomerTypeEnum.HUOBAN_MALL.getCode()))
                .param("verifyCode",verifyCode)
        )
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));

        //供应商角色登录提示：用户名或密码错误
        session = (MockHttpSession) this.mockMvc.perform(post("/login").session(session)
                .param("username", userName)
                .param("password", password)
                .param("roleType", String.valueOf(CustomerTypeEnum.SUPPLIER.getCode()))
                .param("verifyCode",verifyCode)
        )
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("用户名或密码错误")>-1);

        //用已冻结的代理商账号以代理商角色登录
        store.setDisabled(true);
        storeRepository.saveAndFlush(store);
        session = (MockHttpSession) this.mockMvc.perform(post("/login").session(session)
                .param("username", userName)
                .param("password", password)
                .param("roleType", String.valueOf(CustomerTypeEnum.HUOBAN_MALL.getCode()))
                .param("verifyCode",verifyCode)
        )
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已冻结")>-1);

        //用已删除的代理商账号以代理商角色登录
        store.setDisabled(false);
        store.setDeleted(true);
        storeRepository.saveAndFlush(store);
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已冻结")>-1);
    }
}
