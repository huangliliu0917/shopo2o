package com.huotu.shopo2o.web.controller.goods;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.ienum.UserTypeEnum;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.config.MallCustomerConfig;
import com.huotu.shopo2o.service.entity.good.FreightTemplate;
import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.entity.user.UserLevel;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.repository.config.MallCustomerConfigRepository;
import com.huotu.shopo2o.service.repository.good.FreightTemplateRepository;
import com.huotu.shopo2o.service.repository.user.UserLevelRepository;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

    @Autowired
    private MallCustomerConfigRepository customerConfigRepository;

    @Autowired
    private FreightTemplateRepository freightTemplateRepository;

    @Autowired
    private UserLevelRepository userLevelRepository;

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
     * 根据标准类目ID，获取其子类目LIST
     *
     * @param standardTypeId 父类目ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getType", produces = "application/json")
    @ResponseBody
    public ApiResult showGoodsType(@LoginUser MallCustomer customer, String standardTypeId) throws Exception {
        if (standardTypeId == null || standardTypeId.length() == 0) {
            standardTypeId = "0";
        }
        List<HbmGoodsType> typeList = hbmGoodsTypeService.getGoodsTypeByParentId(standardTypeId);
        if (typeList != null && typeList.size() > 0) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS, typeList);
        } else {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
    }

    /**
     * 新增商品时，根据标准类目ID，获取品牌LIST，规格和规格值
     *
     * @param customer
     * @param standardTypeId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addGood")
    public ModelAndView addGood(@LoginUser MallCustomer customer, String standardTypeId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("goods/addGood");
        Long customerId = customer.getStore().getCustomer().getCustomerId();
        if (standardTypeId != null && standardTypeId.length() > 0) {
            List<HbmSpecification> specList;
            List<HbmBrand> brandList;
            //根据标准类目ID获取类目PATH和品牌LIST
            HbmGoodsType hbmGoodsType = hbmGoodsTypeService.getGoodsTypeWithBrandAndSpecByStandardTypeId(standardTypeId, customerId);
            //如果类目下存在子类目，则重新跳转到商品选择界面
            if (hbmGoodsType == null || hbmGoodsType.isParent()) {
                return new ModelAndView("redirect:/good/showType");
            }
            goodsBasicInfo(modelAndView, customerId, customer.getCustomerId());
            String typePath = hbmGoodsTypeService.getTypePath(hbmGoodsType);
            brandList = hbmGoodsType.getBrandList();
            specList = hbmGoodsType.getSpecList();
            modelAndView.addObject("typeId", hbmGoodsType.getTypeId());
            modelAndView.addObject("typePath", typePath);
            modelAndView.addObject("specList", specList);
            modelAndView.addObject("brandList", brandList);

        } else {
            return new ModelAndView("redirect:/good/showType");
        }
        return modelAndView;
    }

    private void goodsBasicInfo(ModelAndView model, Long customerId, Long storeId) {
        List<UserLevel> userLevelList = new ArrayList<>();
        StringBuilder levelIdList = new StringBuilder("");
        //商城基本配置
        MallCustomerConfig customerConfig = customerConfigRepository.findByCustomerId(customerId);
//        SisConfig sisConfig = sisConfigRepository.findByCustomerId(customerId);
        model.addObject("customerConfig", customerConfig);
//        model.addObject("sisConfig", sisConfig);
        //获取供应商运费模板列表
//        List<FreightTemplate> freightTemplateList = freightTemplateRepository.findByCustomerId(supplierId);
        List<FreightTemplate> freightTemplateList = freightTemplateRepository.findByStoreIdAndCustomerId(storeId, customerId);
        //获取供应商店铺分类
        List<SupShopCat> shopCatList = shopCatService.findByStoreId(storeId);
        //随机生成货号
        String productBn = storeId + StringUtil.createRandomStr(8);
        model.addObject("productBn", productBn);
        model.addObject("freightTemplateList", freightTemplateList);
        model.addObject("shopCatList", shopCatList);
        //主域名
        model.addObject("domain", SysConstant.COOKIE_DOMAIN);
        model.addObject("storeId", storeId);
        //小伙伴等级，会员等级
        List<UserLevel> buddyList = userLevelRepository.findByCustomerIdAndTypeOrderByLevel(customerId, UserTypeEnum.buddy);
        List<UserLevel> userList = userLevelRepository.findByCustomerIdAndTypeOrderByLevel(customerId, UserTypeEnum.normal);
        if (buddyList != null && buddyList.size() > 0) {
            userLevelList.addAll(buddyList);
        }
        if (userList != null && userList.size() > 0) {
            userLevelList.addAll(userList);
        }
        model.addObject("levelList", userLevelList);
        if (userLevelList != null) {
            userLevelList.forEach(level -> levelIdList.append(level.getId()).append(","));
        }
        model.addObject("levelIdList", levelIdList.length() > 0 ? levelIdList.substring(0, levelIdList.length() - 1) : levelIdList.toString());
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
            modelAndView.setViewName("goods/goodsListV2");
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
