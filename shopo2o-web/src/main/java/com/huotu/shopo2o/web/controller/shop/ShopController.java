package com.huotu.shopo2o.web.controller.shop;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.service.shop.SupShopCatService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
@RequestMapping("/shop")
@Controller
public class ShopController {
    @Autowired
    private SupShopCatService supShopCatService;
    @Autowired
    private StaticResourceService staticResourceService;

    @RequestMapping("/getCatList")
    public String getCatList(@LoginUser MallCustomer customer, Model model) {
        List<SupShopCat> catList;
            catList = supShopCatService.findByStoreId(customer.getCustomerId());
            catList.forEach(cat -> {
                cat = setImgUri(cat);
                if (cat.getSubShopCat() != null) {
                    cat.getSubShopCat().forEach(subCat -> {
                        setImgUri(subCat);
                    });
                }
            });
        model.addAttribute("catList", catList);
        return "shop/shop_cat_list";
    }

    private SupShopCat setImgUri(SupShopCat supShopCat) {
        if (!StringUtils.isEmpty(supShopCat.getCatImg())) {
            try {
                URI imgUri = staticResourceService.getResource(null, supShopCat.getCatImg());
                supShopCat.setUri(imgUri.toString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return supShopCat;
    }
    @RequestMapping("/addCat")
    @ResponseBody
    public ApiResult addCat(@LoginUser MallCustomer customer, @RequestBody SupShopCat supShopCat) {
        if (supShopCat != null) {
            supShopCat.setStoreId(customer.getStore().getId());
        }
        supShopCat = supShopCatService.saveCat(supShopCat);
        if (supShopCat == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
    @RequestMapping("/modifyCat/{catId}")
    public String  modifyCat(@LoginUser MallCustomer customer, @PathVariable Integer catId) {
        ModelAndView modelAndView = new ModelAndView();
        List<SupShopCat> catList = supShopCatService.findTopCatByStoreId(customer.getCustomerId());
        SupShopCat supShopCat = supShopCatService.findByCatId(catId);
        supShopCat = setImgUri(supShopCat);
        modelAndView.addObject("catList", catList);
        modelAndView.addObject("updateCat", supShopCat);
        return "shop/shop_add";
    }
    @RequestMapping("/removeCat/{catId}")
    @ResponseBody
    public ApiResult removeCat(@PathVariable Integer catId) {
        if (catId == null || catId == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return supShopCatService.deleteCat(catId);
    }
    @RequestMapping("/catDetail/{catId}")
    @ResponseBody
    public ApiResult catDetail(@PathVariable Integer catId){
        SupShopCat supShopCat = supShopCatService.findByCatId(catId);
        if(supShopCat == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        supShopCat = setImgUri(supShopCat);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS,supShopCat);
    }
}
