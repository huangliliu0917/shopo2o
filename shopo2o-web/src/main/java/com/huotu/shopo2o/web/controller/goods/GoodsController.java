package com.huotu.shopo2o.web.controller.goods;

import com.alibaba.fastjson.JSONObject;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.ienum.UserTypeEnum;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.config.MallCustomerConfig;
import com.huotu.shopo2o.service.entity.good.FreightTemplate;
import com.huotu.shopo2o.service.entity.good.HbmBrand;
import com.huotu.shopo2o.service.entity.good.HbmGoodsType;
import com.huotu.shopo2o.service.entity.good.HbmImage;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    private static final Log log = LogFactory.getLog(GoodsController.class);

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

    /**
     * 更新商品状态
     *
     * @param customer 登录的门店信息
     * @param goodsId  商品ID
     * @param comment  提交审核或回收备注
     * @param status   操作状态
     * @return 更新结果 {@link ApiResult}
     */
    @RequestMapping(value = "/updateGoodStatus", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @SuppressWarnings("UnusedDeclaration")
    public ApiResult updateGoodStatus(@LoginUser MallCustomer customer, Integer goodsId,
                                      String comment, Integer status) {
        ApiResult apiResult;
        try {
            if (goodsId == null || status == null) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
            }
            if (EnumHelper.getEnumType(StoreGoodsStatusEnum.CheckStatusEnum.class, status) == null) {
                return new ApiResult("商品状态错误！");
            }
            apiResult = hbmSupplierGoodsService.updateSupplierGoodsStatus(goodsId,
                    EnumHelper.getEnumType(StoreGoodsStatusEnum.CheckStatusEnum.class, status), comment);
        } catch (Exception e) {
            apiResult = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            log.error("更新商品状态失败", e);
        }
        return apiResult;
    }

    /**
     * 保存或更新商品
     *
     * @param customer         供应商登录信息
     * @param hbmSupplierGoods 商品信息
     * @param request          demo
     *                         hidGoodsImagePath:  [{bigPic:'image/invoice/6139/20160104/201601042007322.jpg',smallPic:'image/invoice/6139/20160104/201601042007322.jpg',thumbPic:''}]
     *                         specList:  [{"productId":"88","price":"60","cost":"50","mktPrice":"100","bn":"6139KGGq3EQ2-1","weight":"100","store":"100","pdtDesc":"乳白色,160\\/80(XS)","props":"[{\"SpecId\":\"2158\",\"SpecValueId\":\"9265\",\"SpecValue\":\"乳白色\"},{\"SpecId\":\"2159\",\"SpecValueId\":\"9281\",\"SpecValue\":\"160\\\\/80(XS)\"}]"}]
     * @return 保存结果 {@link ApiResult}
     * @throws Exception
     */
    @RequestMapping(value = "/updateGood", produces = "application/json")
    @ResponseBody
    public ApiResult updateGood(@LoginUser MallCustomer customer, HbmSupplierGoods hbmSupplierGoods, HttpServletRequest request) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        String[] imgPath = request.getParameterValues("hidGoodsImagePath[]");
        String[] imgId = request.getParameterValues("hidGoodsImageId[]");
        if (imgPath == null) {
            imgPath = request.getParameterValues("hidGoodsImagePath");
            imgId = request.getParameterValues("hidGoodsImageId");
        }
        //校验商品名字，规格名序列化，规格内容序列化
        if (hbmSupplierGoods.getName() == null || hbmSupplierGoods.getName().length() == 0) {
            return new ApiResult("请输入商品名称！");
        }
        if (hbmSupplierGoods.getSpecDesc() == null || hbmSupplierGoods.getSpecDesc().length() == 0) {
            return new ApiResult("请选择规格！");
        }
        if (imgPath == null || imgPath.length == 0) {
            return new ApiResult("请上传图片！");
        }
        //货品信息
        String specListStr = request.getParameter("specList");
        if (specListStr == null || specListStr.length() == 0) {
            return new ApiResult("货品未录入！");
        }
        //货品状态 0表示仅保存，1表示保存并提交
        String goodStatus = request.getParameter("goodStatus");
        if (!("0".equals(goodStatus) || "1".equals(goodStatus))) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_BAD_PARSER);
        }
        //如果商品没有设置返利，则默认为0
        if(hbmSupplierGoods.getDisRebatePercent() == null){
            hbmSupplierGoods.setDisRebatePercent(0D);
        }
        int status = Integer.parseInt(request.getParameter("goodStatus"));
        try {
            //属性及属性值
            List<HbmSupplierProducts> specList = JSONObject.parseArray(specListStr, HbmSupplierProducts.class);
            //主图
            List<HbmImage> imgList = new ArrayList<>();
            if (specList == null || specList.size() == 0) {
                return new ApiResult("货品未录入！");
            }
            /*specList.stream().filter(p->StringUtil.isNotEmpty(p.getRebateLayerConfig())).forEach(p -> {
                try {
                    p.setRebateLayerConfig(URLDecoder.decode(p.getRebateLayerConfig(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });*/
            hbmSupplierGoods.setStoreId(customer.getCustomerId());
            //设置缩略图，小图，大图
            if (imgPath.length > 0) {
                JSONObject imgObj = JSONObject.parseObject(imgPath[0]);
                hbmSupplierGoods.setThumbnailPic(imgObj.getString("thumbPic"));
                hbmSupplierGoods.setSmallPic(imgObj.getString("smallPic"));
                hbmSupplierGoods.setBigPic(imgObj.getString("bigPic"));
                for (int i = 0; i < imgPath.length; i++) {
                    HbmImage tempImg = new HbmImage();
                    if (imgId != null && imgId.length > i && imgId[i] != null && imgId[i].length() > 0) {
                        tempImg.setGimageId(Integer.parseInt(imgId[i]));
                    }
                    imgObj = JSONObject.parseObject(imgPath[i]);
                    tempImg.setThumbnail(imgObj.getString("thumbPic"));
                    tempImg.setSmall(imgObj.getString("smallPic"));
                    tempImg.setBig(imgObj.getString("bigPic"));
                    tempImg.setStoreId(customer.getCustomerId());
                    imgList.add(tempImg);
                }
            }

            //设置品牌
            if (hbmSupplierGoods.getBrand() == null || hbmSupplierGoods.getBrand().getBrandId() == 0) {
                hbmSupplierGoods.setBrand(null);
            }
            //设置运费模板
            if (hbmSupplierGoods.getFreightTemplate() == null || hbmSupplierGoods.getFreightTemplate().getId() == 0) {
                hbmSupplierGoods.setFreightTemplate(null);
            }
            //设置类目
            if (hbmSupplierGoods.getType() != null && hbmSupplierGoods.getType().getTypeId() != 0) {
                HbmGoodsType type = hbmGoodsTypeService.getGoodsTypeWithBrandAndSpecByStandardTypeId(hbmSupplierGoods.getType().getTypeId(), customer.getStore().getCustomer().getCustomerId());
                hbmSupplierGoods.setType(type);
            }
            //设置店铺分类
            if (hbmSupplierGoods.getShopCat() != null && hbmSupplierGoods.getShopCat().getCatId() > 0) {
                SupShopCat shopCat = shopCatService.findByCatId(hbmSupplierGoods.getShopCat().getCatId());
                hbmSupplierGoods.setShopCat(shopCat);
            } else {
                hbmSupplierGoods.setShopCat(null);
            }
            result = hbmSupplierGoodsService.updateGood(hbmSupplierGoods, specList, imgList, status);
        } catch (Exception e) {
            log.error("保存或更改商品失败", e);
        }
        return result;
    }
}
