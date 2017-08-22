package com.huotu.shopo2o.hbm.web.controller;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.Shop;
import com.huotu.shopo2o.service.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by helloztt on 2017-08-22.
 */
@Controller("/hbm/shop")
public class ShopController extends MallBaseController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/list")
    public String shopList(@ModelAttribute("customerId") Long customerId
            , @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo
            , Model model) {
        Pageable pageable = new PageRequest(pageNo - 1, Constant.PAGE_SIZE);
        Page<Shop> shopPage = shopService.findAll(customerId, pageable);
        model.addAttribute("shopPage", shopPage);
        model.addAttribute("pageSize", Constant.PAGE_SIZE);
        model.addAttribute("pageNo", pageNo);
        return "shop/shop_manager";
    }

    @PostMapping("/changeOption")
    @ResponseBody
    public ApiResult changeOption(@ModelAttribute("customerId") Long customerId
            , @RequestParam Long shopId, @RequestParam boolean isDisabled) {
        Shop shop = shopService.findOne(shopId,customerId);
        if(shop == null){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR,"门店不存在");
        }
        if(shop.isDisabled() != isDisabled){
            shopService.disableShop(shop,isDisabled);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/remove")
    @ResponseBody
    public ApiResult remove(@ModelAttribute("customerId")Long customerId
            , @RequestParam Long shopId){
        Shop shop = shopService.findOne(shopId,customerId);
        if(shop == null){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR,"门店不存在");
        }
        if(!shop.isDeleted()){
            shopService.deleteShop(shop);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
