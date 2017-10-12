package com.huotu.shopo2o.web.controller.goods;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import com.huotu.shopo2o.service.service.shop.SupShopCatService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xyr on 2017/10/11.
 */
@Controller
@RequestMapping(value = "/store")
public class StoreManagerController {

    @Autowired
    private HbmSupplierGoodsService hbmSupplierGoodsService;
    @Autowired
    private HbmSupplierProductsService hbmSupplierProductsService;
    @Autowired
    private SupShopCatService shopCatService;

    /**
     * 显示商品产品及库存信息
     *
     * @param goodsId
     * @return
     * @throws Exception
     */
    @RequestMapping("/showProductsStore")
    public ModelAndView showProductsStore(@LoginUser MallCustomer customer, String goodsId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("goods/editStore");
        HbmSupplierGoods hbmSupplierGoods = null;
        List<HbmSupplierProducts> productList = null;
        List<HbmSpecification> specList = null;
        List<SupShopCat> shopCatList = null;
        //获取供应商店铺分类
        shopCatList = shopCatService.findByStoreId(customer.getCustomerId());
        if (goodsId != null && goodsId.length() > 0) {
            hbmSupplierGoods = hbmSupplierGoodsService.findWithBrandAndSpecBySupplierGoodId(Integer.parseInt(goodsId), customer.getStore().getCustomer().getCustomerId());
            if (hbmSupplierGoods != null) {
                productList = hbmSupplierProductsService.getProductListByGoodId(hbmSupplierGoods.getSupplierGoodsId());
                if (hbmSupplierGoods.getType() != null) {
                    specList = hbmSupplierGoods.getType().getSpecList();
                }
            }
        }
        modelAndView.addObject("hbmSupplierGoods", hbmSupplierGoods);
        modelAndView.addObject("productList", productList);
        modelAndView.addObject("specList", specList);
        modelAndView.addObject("shopCatList", shopCatList);
        return modelAndView;
    }

    /**
     * 设置产品库存信息 和 店铺分类
     *
     * @param supplierGoodsId
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/storeManager", produces = "application/json")
    @ResponseBody
    public ApiResult modifyStore(String supplierGoodsId, HttpServletRequest request) throws Exception {
        ApiResult apiResult;
        try {
            String shopCatStr = request.getParameter("shopCatId");
            String[] productId = request.getParameterValues("productId[]");
            String[] productStore = request.getParameterValues("productStore[]");
            if (productId == null) {
                productId = request.getParameterValues("productId");
            }
            if (productStore == null) {
                productStore = request.getParameterValues("productStore");
            }
            if (supplierGoodsId == null || productId == null || productStore == null) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
            }
            if (productStore.length != productId.length) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_BAD_PARSER);
            }

            apiResult = hbmSupplierGoodsService.updateGoodsAndProductsStore(Integer.parseInt(supplierGoodsId),
                    shopCatStr == null ? null:Integer.parseInt(shopCatStr), productId, productStore);
            return apiResult;
        } catch (Exception e) {
            return new ApiResult("保存失败！");
        }
    }
}
