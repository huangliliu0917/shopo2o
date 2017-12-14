package com.huotu.shopo2o.hbm.web.service;

import com.huotu.shopo2o.hbm.web.CommonTestBase;
import com.huotu.shopo2o.service.entity.config.MallApiConfig;
import com.huotu.shopo2o.service.repository.config.MallApiConfigRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author luyuanyuan on 2017/12/14.
 */
public class K3ServiceTest extends CommonTestBase{
    @Autowired
    private K3Service k3Service;
    @Autowired
    private MallApiConfigRepository mallApiConfigRepository;
    @Test
    public void getOrganizations() throws Exception {
        long customerId = 7031L;
        String dbId = "5a255c142df6d9";
        String userName = "Administrator";
        String password = "888888";
        String apiServerUrl = "http://101.68.67.178:8001/K3Cloud/";
        MallApiConfig mallApiConfig = new MallApiConfig();
        mallApiConfig.setCustomerId(customerId);
        mallApiConfig.setApiServerUrl(apiServerUrl);
        mallApiConfig.setDbId(dbId);
        mallApiConfig.setUserName(userName);
        mallApiConfig.setPassword(password);
        mallApiConfigRepository.saveAndFlush(mallApiConfig);

        boolean flag = k3Service.getOrganizations(customerId, "01");
        Assert.assertTrue(flag);
        boolean flag1 = k3Service.getOrganizations(customerId, "00");
        Assert.assertFalse(flag1);
    }

}