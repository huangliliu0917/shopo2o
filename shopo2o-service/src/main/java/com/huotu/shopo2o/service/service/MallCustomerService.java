package com.huotu.shopo2o.service.service;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2017-08-22.
 */
public interface MallCustomerService extends UserDetailsService {

    /**
     * 根据登录信息创建一个账号，单元测试直接save,其他模式调用商城接口增加账号
     *
     * @param userName         登录名
     * @param password         密码
     * @param customerTypeEnum 账号类型
     * @return {@link MallCustomer}
     */
    @Transactional
    ApiResult newCustomer(String userName, String password, CustomerTypeEnum customerTypeEnum);

    /**
     * 根据主键查找商户信息
     *
     * @param customerId 商户ID
     * @return {@link MallCustomer}
     */
    @Transactional(readOnly = true)
    MallCustomer findOne(Long customerId);

    boolean isExist(String userName);
}
