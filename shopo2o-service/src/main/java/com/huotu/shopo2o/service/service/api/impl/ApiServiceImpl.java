package com.huotu.shopo2o.service.service.api.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.httputil.HttpClientUtil;
import com.huotu.shopo2o.common.httputil.HttpResult;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.SignBuilder;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.service.api.ApiService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class ApiServiceImpl implements ApiService {
    private static final Log log = LogFactory.getLog(ApiServiceImpl.class);

    @Override
    public ApiResult newCustomer(String userName, String password, CustomerTypeEnum customerTypeEnum) throws UnsupportedEncodingException {
        // TODO: 2017-08-22 这个接口待明哥测试
        Map<String, Object> requestMap = new TreeMap<>();
        requestMap.put("loginname", userName);
        requestMap.put("password", password);
        requestMap.put("customertype", customerTypeEnum.getCode());
        requestMap.put("appid", SysConstant.HUOBANMALL_PUSH_APPID);
        String sign = SignBuilder.buildSignIgnoreEmpty(requestMap, null, SysConstant.HUOBANMALL_PUSH_APP_SECRET);
        requestMap.put("sign", sign);
        ApiResult apiResult;
        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/Account/sendCode", requestMap);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
        } else {
            apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
        return apiResult;
    }
}
