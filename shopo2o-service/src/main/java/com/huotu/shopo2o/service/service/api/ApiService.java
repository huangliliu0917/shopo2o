package com.huotu.shopo2o.service.service.api;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;

import java.io.UnsupportedEncodingException;

/**
 * Created by helloztt on 2017-08-22.
 */
public interface ApiService {

    /**
     * 调用商城接口增加商户
     *
     * @param userName         登录名
     * @param password         密码
     * @param customerTypeEnum 商户类型
     * @return 调用接口
     */
    ApiResult newCustomer(String userName, String password, CustomerTypeEnum customerTypeEnum) throws UnsupportedEncodingException;
}
