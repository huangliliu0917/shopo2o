package com.huotu.shopo2o.web.controller.shop;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.config.SupBasicConfig;
import com.huotu.shopo2o.service.service.config.SupBasicConfigService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hxh on 2017-09-18.
 */
@Controller
@RequestMapping("/supConfig")
public class SupConfigController {
    @Autowired
    private SupBasicConfigService supBasicConfigService;
    @Autowired
    private StaticResourceService staticResourceService;
    @RequestMapping("/basicConfig")
    public String basicConfig(@LoginUser MallCustomer customer, Model model) throws URISyntaxException {

        SupBasicConfig config = supBasicConfigService.findByStoreId(customer.getCustomerId());
        URI logoUrl = null;
        if(!StringUtils.isEmpty(config.getShopConfig().getShopLogo())){
            logoUrl = staticResourceService.getResource(StaticResourceService.huobanmallMode,config.getShopConfig().getShopLogo());
        }
        model.addAttribute("config", config);
        model.addAttribute("supplierName", customer.getNickName());
        model.addAttribute("logoUrl", logoUrl);
        return "config/basicConfig";
    }
    @RequestMapping(value = "/saveBasicConfig", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveBasicConfig(@LoginUser MallCustomer customer, @RequestBody SupBasicConfig supBasicConfig) {
        if(StringUtils.isEmpty(supBasicConfig.getMobile())){
            return new ApiResult("请输入手机号");
        }
        /*if (basicConfigService.findByMobile(customer.getCustomerId(),supBasicConfig.getMobile()) != 0) {
            return new ApiResult("该手机号已被占用");
        }*/
        supBasicConfig = supBasicConfigService.save(supBasicConfig,customer.getCustomerId());
        if (supBasicConfig == null) {
            ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
