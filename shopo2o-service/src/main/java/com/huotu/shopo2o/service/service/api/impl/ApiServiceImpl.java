package com.huotu.shopo2o.service.service.api.impl;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.config.MallPasswordEncoder;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.service.api.ApiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class ApiServiceImpl implements ApiService {
    private static final Log log = LogFactory.getLog(ApiServiceImpl.class);
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;
    private Random random = new Random();

    @Override
    public ApiResult newCustomer(String userName, String password, CustomerTypeEnum customerTypeEnum) {
        // TODO: 2017-08-22 这个接口待明哥测试
        /*Map<String, Object> requestMap = new TreeMap<>();
        requestMap.put("loginname", userName);
        requestMap.put("password", password);
        requestMap.put("customertype", customerTypeEnum.getCode());
        requestMap.put("appid", SysConstant.HUOBANMALL_PUSH_APPID);
        String sign = null;
        try {
            sign = SignBuilder.buildSignIgnoreEmpty(requestMap, null, SysConstant.HUOBANMALL_PUSH_APP_SECRET);
        } catch (UnsupportedEncodingException e) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        requestMap.put("sign", sign);
        ApiResult apiResult;
        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/Account/sendCode", requestMap);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
        } else {
            apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        return apiResult;*/
        //接口提供前先自己加账号
        String key = UUID.randomUUID().toString().substring(0,5);
        Integer token = new Random().nextInt(900000) + 100000;
        //COOKIE_DOMAIN start with .
        String mainDomain = SysConstant.COOKIE_DOMAIN;
        String url = String.format("http://distribute%s/index.aspx?key=%s&t=huotu", mainDomain, key);
        MallCustomer mallCustomer = new MallCustomer();
        mallCustomer.setUsername(userName);
        mallCustomer.setPassword(passwordEncoder.encode(password));
        mallCustomer.setIndustryType(0);
        mallCustomer.setUserActivate(1);
        mallCustomer.setRoleID(-2);
        mallCustomer.setBelongManagerID(3);
        mallCustomer.setEmail("");
        mallCustomer.setIsOld(1);
        mallCustomer.setDeveloperUrl(url);
        mallCustomer.setDeveloperToken(String.valueOf(token));
        mallCustomer.setScType(1);
        mallCustomer.setScore(0.0);
        mallCustomer.setCityID(0);
        mallCustomer.setCustomerType(CustomerTypeEnum.STORE);
        customerRepository.saveAndFlush(mallCustomer);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
