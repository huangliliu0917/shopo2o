package com.huotu.shopo2o.hbm.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.hbm.web.service.K3Service;
import com.huotu.shopo2o.service.entity.config.MallApiConfig;
import com.huotu.shopo2o.service.repository.config.MallApiConfigRepository;
import kingdee.bos.webapi.client.K3CloudApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author helloztt
 */
@Service
public class K3ServiceImpl implements K3Service {

    @Autowired
    private MallApiConfigRepository mallApiConfigRepository;

    @Override
    public  boolean getOrganizations(Long customerId,String erpId) throws Exception {
        MallApiConfig mallApiConfig = mallApiConfigRepository.findOne(customerId);
        String dbId = mallApiConfig.getDbId();
        String userName = mallApiConfig.getUserName();
        String password = mallApiConfig.getPassword();
        String apiServerUrl = mallApiConfig.getApiServerUrl();
        String data = "{\"CreateOrgId\":\"0\",\"Number\":\""+erpId+"\",\"Id\":\"\"}";
        K3CloudApiClient apiClient = new K3CloudApiClient(apiServerUrl);
        if(apiClient.login(dbId,userName,password,2052)){
            String resultStr = apiClient.view("ORG_Organizations", data);
            JSONObject obj = (JSONObject) JSONObject.parse(resultStr);
            JSONObject result = (JSONObject) obj.get("Result");
            JSONObject responseStatus = (JSONObject) result.get("ResponseStatus");
            if(responseStatus == null){
                return true;
            }
        }
        return false;
    }

}
