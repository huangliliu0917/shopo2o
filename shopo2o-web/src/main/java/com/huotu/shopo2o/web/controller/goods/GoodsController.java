package com.huotu.shopo2o.web.controller.goods;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.service.goods.HbmGoodsTypeService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.shop.SupShopCatService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/10.
 */
@Controller
@RequestMapping(value = "/good")
public class GoodsController {

    @Autowired
    private HbmGoodsTypeService hbmGoodsTypeService;
    @Autowired
    private SupShopCatService shopCatService;
    @Autowired
    private HbmSupplierGoodsService hbmSupplierGoodsService;
    @Autowired
    private StaticResourceService resourceServer;

    /**
     * 新增商品时，显示选择类型界面中的一级类型,并根据tOrder排序
     * 显示用户最近10个新增的商品类型
     *
     * @param customer 用户登录
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showType")
    public ModelAndView showType(@LoginUser MallCustomer customer) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("goods/goodType");
        //获取一级标准LIST 第一级的标准父类目ID为 "0"
        List<HbmGoodsType> typeList = hbmGoodsTypeService.getGoodsTypeByParentId("0");
        //获取该用户最近10个新增的商品类型
        if (customer != null && customer.getCustomerId() != 0) {
            List<HbmGoodsType> lastUsedType = hbmGoodsTypeService.getGoodsTypeLastUsed(customer.getCustomerId());
            modelAndView.addObject("lastUsedType", lastUsedType);
        }
        modelAndView.addObject("typeList", typeList);
        return modelAndView;
    }

    /**
     * 根据搜索条件获取商品列表
     *
     * @param customer                 登录信息
     * @param hbmSupplierGoodsSearcher 商品列表搜索条件
     * @param showCheckedOnly          true：只显示已审核通过的商品（给库存管理界面使用）
     *                                 others：显示全部商品（给商品列表界面使用）
     * @return {@link ModelAndView}
     * @throws Exception
     */
    @RequestMapping("/showGoodsList")
    public ModelAndView showGoodsList(@LoginUser MallCustomer customer, HbmSupplierGoodsSearcher hbmSupplierGoodsSearcher,
                                      String showCheckedOnly) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        if ("true".equals(showCheckedOnly)) {   //只查询已上架的商品
            modelAndView.setViewName("goods/storeManager");
            hbmSupplierGoodsSearcher.setMarketable(true);
        } else {
            modelAndView.setViewName("goods/goodsList");
        }
        List<HbmGoodsType> typeList = hbmGoodsTypeService.getGoodsTypeLastUsed(customer.getCustomerId());
        List<SupShopCat> shopCatList = shopCatService.findByStoreId(customer.getCustomerId());
        Page<HbmSupplierGoods> supplierGoodsPage = hbmSupplierGoodsService.getGoodList(customer.getCustomerId(), hbmSupplierGoodsSearcher);
        if (supplierGoodsPage != null) {
            supplierGoodsPage.getContent().forEach(goods -> {
                if (!StringUtils.isEmpty(goods.getThumbnailPic())) {
                    try {
                        URI uri = resourceServer.getResource(StaticResourceService.huobanmallMode, goods.getThumbnailPic());
                        goods.setPicUri(uri);
                    } catch (URISyntaxException e) {
                    }
                }
            });
            modelAndView.addObject("goodsList", supplierGoodsPage.getContent());
            modelAndView.addObject("totalRecords", supplierGoodsPage.getTotalElements());
            modelAndView.addObject("totalPages", supplierGoodsPage.getTotalPages());
        }
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("shopCatList", shopCatList);
        modelAndView.addObject("pageSize", hbmSupplierGoodsSearcher.getPageSize());
        modelAndView.addObject("pageIndex", hbmSupplierGoodsSearcher.getPageNo());
        modelAndView.addObject("checkStatusEnums", StoreGoodsStatusEnum.CheckStatusEnum.values());
        return modelAndView;
    }
}
