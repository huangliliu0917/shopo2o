package com.huotu.shopo2o.hbm.web.service.impl;

import com.huotu.shopo2o.hbm.web.service.K3Service;
import kingdee.bos.webapi.client.ApiClient;
import kingdee.bos.webapi.client.K3CloudApiClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author helloztt
 */
@Service
public class K3ServiceImpl implements K3Service {


    @Override
    public List getOrganizations() throws Exception {
        K3CloudApiClient apiClient = new K3CloudApiClient("http://101.68.67.178:8001/K3Cloud/");
        if(apiClient.login("5a255c142df6d9","Administrator","888888",2052)){
//            apiClient.view()
        }

        return null;
    }
}
