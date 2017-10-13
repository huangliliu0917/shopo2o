package com.huotu.shopo2o.web.controller.operator;

import com.alibaba.fastjson.JSONObject;
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
import java.util.Random;
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
    public void testShowOperators() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/" + operators.get(0).getId()).session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        JSONObject object = JSONObject.parseObject(content);
        Assert.assertEquals(200, object.get("code"));
        Assert.assertEquals("请求成功", object.get("msg"));
        //operatorId不存在
        MvcResult mvcResult1 = mockMvc.perform(post(BASE_URL + "/" + String.valueOf(new Random().nextInt(100))).session(mockHttpSession))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult1.getResponse().getContentAsString();
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        Assert.assertEquals(500, jsonObject.get("code"));
        Assert.assertEquals("没有传输数据", jsonObject.get("msg"));
    }

    @Test
    public void testDelete() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/delete").session(mockHttpSession)
                .param("operatorId", String.valueOf(operators.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        Assert.assertEquals(200, jsonObject.get("code"));
        Assert.assertEquals("请求成功", jsonObject.get("msg"));
    }

    @Test
    public void testSave() throws Exception {
        //编辑
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/save").session(mockHttpSession)
                .param("operatorId", String.valueOf(operators.get(0).getId()))
                .param("username", String.valueOf(operators.get(0).getUsername()))
                .param("realName", operators.get(0).getRealName())
                .param("roleName", operators.get(0).getRoleName())
                .param("authorities", "ROLE_ROOT"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        Assert.assertEquals(200, jsonObject.get("code"));
        Assert.assertEquals("请求成功", jsonObject.get("msg"));
        //新增(用户名已存在)
        MvcResult mvcResult1 = mockMvc.perform(post(BASE_URL + "/save").session(mockHttpSession)
                .param("username", String.valueOf(operators.get(0).getUsername()))
                .param("realName", operators.get(0).getRealName())
                .param("roleName", operators.get(0).getRoleName())
                .param("authorities", "ROLE_ROOT"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString1 = mvcResult1.getResponse().getContentAsString();
        JSONObject jsonObject1 = JSONObject.parseObject(contentAsString1);
        Assert.assertEquals(100, jsonObject1.get("code"));
        Assert.assertEquals("用户名已存在", jsonObject1.get("msg"));
        //新增
        MvcResult mvcResult2 = mockMvc.perform(post(BASE_URL + "/save").session(mockHttpSession)
                .param("username", UUID.randomUUID().toString())
                .param("realName", UUID.randomUUID().toString())
                .param("roleName", UUID.randomUUID().toString())
                .param("authorities", "ROLE_ROOT"))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString2 = mvcResult2.getResponse().getContentAsString();
        JSONObject jsonObject2 = JSONObject.parseObject(contentAsString2);
        Assert.assertEquals(200, jsonObject2.get("code"));
        Assert.assertEquals("请求成功", jsonObject2.get("msg"));
    }

    @Test
    public void testDisabledOperator() throws Exception {
        MockHttpSession mockHttpSession = loginAs(userName, passWord);
        //operatorId不存在
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/disabled").session(mockHttpSession)
                .param("operatorId", String.valueOf(new Random().nextInt(10000))))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        Assert.assertEquals(500, jsonObject.get("code"));
        Assert.assertEquals("系统请求失败", jsonObject.get("msg"));
        //operatorId存在
        MvcResult mvcResult1 = mockMvc.perform(post(BASE_URL + "/disabled").session(mockHttpSession)
                .param("operatorId", String.valueOf(operators.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString1 = mvcResult1.getResponse().getContentAsString();
        JSONObject jsonObject1 = JSONObject.parseObject(contentAsString1);
        Assert.assertEquals(200, jsonObject1.get("code"));
        Assert.assertEquals("请求成功", jsonObject1.get("msg"));
    }
}
