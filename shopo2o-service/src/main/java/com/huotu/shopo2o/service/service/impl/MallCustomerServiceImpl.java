package com.huotu.shopo2o.service.service.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import com.huotu.shopo2o.service.repository.MallCustomerRepository;
import com.huotu.shopo2o.service.service.MallCustomerService;
import com.huotu.shopo2o.service.service.api.ApiService;
import com.huotu.shopo2o.service.service.api.impl.ApiServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by helloztt on 2017-08-22.
 */
@Service
public class MallCustomerServiceImpl implements MallCustomerService {
    private static final Log log = LogFactory.getLog(ApiServiceImpl.class);
    @Autowired
    private Environment env;
    @Autowired
    private MallCustomerRepository mallCustomerRepository;
    @Autowired
    private ApiService apiService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MallCustomer mallCustomer = mallCustomerRepository.findByUsername(userName);
        if (mallCustomer == null)
            throw new UsernameNotFoundException("没有该账号");
        return mallCustomer;
    }

    @Override
    public ApiResult newCustomer(String userName, String password, CustomerTypeEnum customerTypeEnum) {
        if(env.acceptsProfiles("test")){
            //测试环境
            MallCustomer mallCustomer = new MallCustomer();
            mallCustomer.setUsername(userName);
            mallCustomer.setPassword(password);
            mallCustomer.setCustomerType(customerTypeEnum);
            mallCustomer = mallCustomerRepository.saveAndFlush(mallCustomer);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS,mallCustomer);
        }else{
            //调用商城接口
            ApiResult result = apiService.newCustomer(userName, password, customerTypeEnum);
            if(result.getCode() == 200){
                MallCustomer mallCustomer = mallCustomerRepository.findByUsername(userName);
                if(mallCustomer != null){
                    return ApiResult.resultWith(ResultCodeEnum.SUCCESS,mallCustomer);
                }
            }else{
                return result;
            }
        }
        return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
    }

    @Override
    public MallCustomer findOne(Long customerId) {
        return mallCustomerRepository.findOne(customerId);
    }

    @Override
    public boolean isExist(String userName) {
        return mallCustomerRepository.findByUsername(userName) != null;
    }
}
