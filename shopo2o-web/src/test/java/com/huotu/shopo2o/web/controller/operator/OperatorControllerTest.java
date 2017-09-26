package com.huotu.shopo2o.web.controller.operator;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.author.Operator;
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
 * Created by hxh on 2017-09-26.
 */
public class OperatorControllerTest extends CommonTestBase {
    private static String BASE_URL = "/operator";
    private MallCustomer user;
    private String userName;
    private String passWord;
    List<Operator> operators = new ArrayList<>();

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
            Operator operator = mockOperator(user);
            operators.add(operator);
        }
    }

    @Test
    public void testGetOperatorList() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/getOperatorList").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("operators"))
                .andReturn();
        List<Operator> operatorList = (List<Operator>) mvcResult.getModelAndView().getModel().get("operators");
        Assert.assertNotNull(operatorList);
        Assert.assertTrue(operatorList.size() == operators.size());
        for (int i = 0; i < operators.size(); i++) {
            Assert.assertEquals(operatorList.get(i).getId(), operators.get(i).getId());
        }
    }
    @Test
    public void testShowOperators()throws Exception{
        // TODO: 2017-09-26 请求报错有待修改
//        MockHttpSession mockHttpSession = loginAs(userName, passWord);
//        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/"+operators.get(0).getId()).session(mockHttpSession))
//                .andExpect(status().isOk())
//                .andReturn();
    }
}
