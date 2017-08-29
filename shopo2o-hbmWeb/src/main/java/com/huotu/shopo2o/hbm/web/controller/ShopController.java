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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

/**
 * Created by helloztt on 2017-08-22.
 */
@Controller
@RequestMapping("/hbmWeb/shop")
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

    @GetMapping("/edit")
    public String edit(@ModelAttribute("customerId") Long customerId
            , @RequestParam(value = "shopId", required = false, defaultValue = "0") Long shopId
            , Model model) {
        Shop shop = null;
        if (shopId != null && shopId != 0) {
            shop = shopService.findOne(shopId, customerId);
        }
        if (shop == null) {
            shop = new Shop();
        }
        model.addAttribute("currentData", shop);
        return "shop/add_shop";
    }

    @PostMapping("/save")
    @ResponseBody
    @Transactional
    public ApiResult save(@ModelAttribute("customerId") Long customerId
            , @RequestParam(required = false, defaultValue = "0") Long shopId, @RequestParam String loginName, @RequestParam String name
            , @RequestParam String areaCode, @RequestParam String telephone
            , @RequestParam String provinceCode, @RequestParam String cityCode, @RequestParam String districtCode
            , @RequestParam String address, @RequestParam Double lan, @RequestParam Double lat
            , @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime openTime, @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime closeTime
            , @DateTimeFormat(pattern = "HH:mm") @RequestParam LocalTime deadlineTime, @RequestParam String logo
            , @RequestParam String erpId) {
        Shop shop;
        if (shopId != null && shopId != 0) {
            shop = shopService.findOne(shopId, customerId);
            if (shop == null) {
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
            }
        } else {
            shop = new Shop();
        }
        shop.setName(name);
        shop.setAreaCode(areaCode);
        shop.setTelephone(telephone);
        shop.setProvinceCode(provinceCode);
        shop.setCityCode(cityCode);
        shop.setDistrictCode(districtCode);
        shop.setAddress(address);
        shop.setLan(lan);
        shop.setLat(lat);
        shop.setOpenTime(openTime);
        shop.setCloseTime(closeTime);
        shop.setDeadlineTime(deadlineTime);
        shop.setLogo(logo);
        shop.setErpId(erpId);
        return shopService.newShop(customerId, shop, loginName);
    }

    @PostMapping("/changeOption")
    @ResponseBody
    public ApiResult changeOption(@ModelAttribute("customerId") Long customerId
            , @RequestParam Long shopId, @RequestParam boolean isDisabled) {
        Shop shop = shopService.findOne(shopId, customerId);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
        }
        if (shop.isDisabled() != isDisabled) {
            shopService.disableShop(shop, isDisabled);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @PostMapping("/remove")
    @ResponseBody
    public ApiResult remove(@ModelAttribute("customerId") Long customerId
            , @RequestParam Long shopId) {
        Shop shop = shopService.findOne(shopId, customerId);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR, "门店不存在");
        }
        if (!shop.isDeleted()) {
            shopService.deleteShop(shop);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
