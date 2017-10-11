package com.huotu.shopo2o.service.service.goods.impl;

import com.alibaba.fastjson.JSONArray;
import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.DoubleUtil;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.good.HbmGoodsSpecIndex;
import com.huotu.shopo2o.service.entity.good.HbmImage;
import com.huotu.shopo2o.service.entity.good.HbmSpecification;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.good.MallGood;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.repository.good.HbmGoodsSpecIndexRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.repository.good.MallGoodRepository;
import com.huotu.shopo2o.service.service.goods.HbmImageService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xyr on 2017/10/10.
 */
@Service
public class HbmSupplierGoodsServiceImpl implements HbmSupplierGoodsService {

    @Autowired
    private HbmSupplierGoodsRepository supplierGoodsRepository;
    @Autowired
    private MallGoodRepository goodsRepository;
    @Autowired
    private HbmSupplierProductsRepository productsRepository;
    @Autowired
    private HbmImageService imgService;
    @Autowired
    private HbmSupplierProductsService productsService;
    @Autowired
    private HbmGoodsSpecIndexRepository goodsSpecIndexRepository;

    @Override
    public Page<HbmSupplierGoods> getGoodList(long storeId, HbmSupplierGoodsSearcher searcher) {
        Specification<HbmSupplierGoods> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("storeId").as(Long.class), storeId));
            if (!StringUtils.isEmpty(searcher.getStandardTypeId())) {
                predicates.add(criteriaBuilder.like(root.get("type").get("path").as(String.class), "%" + "|" + searcher.getStandardTypeId() + "|" + "%"));// "|30|"
            }
            if (!StringUtils.isEmpty(searcher.getTypeName())) {
                predicates.add(criteriaBuilder.like(root.get("type").get("name").as(String.class), "%" + searcher.getTypeName() + "%"));
            }
            if (searcher.getBrandId() != 0) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("brandId").as(Integer.class), searcher.getBrandId()));
            }
            if (!StringUtils.isEmpty(searcher.getBrandName())) {
                predicates.add(criteriaBuilder.like(root.get("brand").get("brandName").as(String.class), "%" + searcher.getBrandName() + "%"));
            }
            if (searcher.getName() != null && searcher.getName().length() > 0) {
                predicates.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + searcher.getName() + "%"));
            }
            if (searcher.getBn() != null && searcher.getBn().length() > 0) {
                predicates.add(criteriaBuilder.like(root.get("bn").as(String.class), "%" + searcher.getBn() + "%"));
            }
            if (searcher.getMinStore() != null && searcher.getMinStore() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("store").as(Integer.class), searcher.getMinStore()));
            }
            if (searcher.getMaxStore() != null && searcher.getMaxStore() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("store").as(Integer.class), searcher.getMaxStore()));
            }
            if (searcher.getStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("status").as(StoreGoodsStatusEnum.CheckStatusEnum.class), EnumHelper.getEnumType(StoreGoodsStatusEnum.CheckStatusEnum.class, searcher.getStatus())));
            }
            if (searcher.getMarketable() != null) {
                predicates.add(criteriaBuilder.equal(root.get("marketable").as(Boolean.class), searcher.getMarketable()));
            }
            if (searcher.getSupplierCatId() > 0) {
                predicates.add(criteriaBuilder.equal(root.get("shopCat").get("catId").as(Integer.class), searcher.getSupplierCatId()));
            } else if (searcher.getSupplierCatId() == 0) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get("shopCat").as(SupShopCat.class)),
                        criteriaBuilder.equal(root.get("shopCat").get("catId").as(Integer.class), searcher.getSupplierCatId())));
            }
            predicates.add(criteriaBuilder.equal(root.get("disabled").as(Boolean.class), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<HbmSupplierGoods> goodsPage = supplierGoodsRepository.findAll(specification, new PageRequest(searcher.getPageNo() - 1, searcher.getPageSize(), new Sort(Sort.Direction.DESC, "lastModify")));
        List<HbmSupplierGoods> goodsList = goodsPage.getContent();
        //如果商品已上架，读取实时库存
        goodsList.forEach(p -> {
            if (p.getMallGoodsId() != null && !p.getMallGoodsId().equals(0) && p.isMarketable()) {
                MallGood good = goodsRepository.findOne(p.getMallGoodsId());
                p.setUsableStore(good.getProducts().stream().mapToInt(product -> product.getStore() - product.getFreez()).sum());
            } else {
                p.setUsableStore(p.getStore());
            }
        });
        return goodsPage;
    }

    @Override
    @Transactional
    public ApiResult updateSupplierGoodsStatus(int storeGoodsId,
                                               StoreGoodsStatusEnum.CheckStatusEnum storeGoodsStatus,
                                               String remark) throws Exception {
        //找到该编号的商品
        HbmSupplierGoods supplierGoods = supplierGoodsRepository.findOne(storeGoodsId);
        if (supplierGoods == null) { //为空，则返回
            return new ApiResult("商品编号错误！");
        }
        //判断操作是否有权执行
        if ((storeGoodsStatus == StoreGoodsStatusEnum.CheckStatusEnum.CHECKING && supplierGoods.editable())
                || (storeGoodsStatus == StoreGoodsStatusEnum.CheckStatusEnum.RECYCLING && supplierGoods.operable())) {
            //修改商品状态
            supplierGoods.setStatus(storeGoodsStatus);
            supplierGoods.setLastModify(new Date());
            if (remark != null) {
                supplierGoods.setRemark(remark);
            }
            supplierGoodsRepository.save(supplierGoods);
            List<HbmSupplierProducts> productsList = productsRepository.findBySupplierGoodsId(storeGoodsId);
            productsList.forEach(p -> {
                p.setStatus(storeGoodsStatus);
            });
            productsRepository.save(productsList);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return new ApiResult("该商品已审核或已回收，无法操作！");
    }

    /**
     * 保存或更新商品
     * @param goods
     * @param productsList
     * @param status       0表示 保存草稿，1表示 提交审核
     * @return
     */
    @Override
    @Transactional
    public ApiResult updateGood(HbmSupplierGoods goods, List<HbmSupplierProducts> productsList, List<HbmImage> imgList, int status) throws Exception {
        HbmSupplierGoods old = null;
        //当前货品ID
        String productIdStr = "";
        //当前图片ID
        String imgIdStr = "";
        if (goods != null && goods.getSupplierGoodsId() != null) {
            old = supplierGoodsRepository.findOne(goods.getSupplierGoodsId());
            //若为修改，先判断是否有编辑权限并且是否未删除
            if(old != null && old.getStatus() == StoreGoodsStatusEnum.CheckStatusEnum.CHECKED && status == 0){
                return new ApiResult("商品已审核，无法保存草稿");
            }
            if (old != null && old.isDisabled()) {
                return new ApiResult("商品已删除，无法编辑！");
            }
        }
        if (goods.getType() == null || goods.getType().getTypeId() == 0) {
            return new ApiResult("请先选择商品类型！");
        }

        if (old == null) {
            old = new HbmSupplierGoods();
            if(goods.getCreateTime() == null){
                old.setCreateTime(new Date());
            }else{
                old.setCreateTime(goods.getCreateTime());
                old.setMarketable(goods.isMarketable());
                old.setCustomerPrice(goods.getPrice());
            }
        }

        double minMktPrice = Integer.MAX_VALUE, minCost = Integer.MAX_VALUE, minPrice = Integer.MAX_VALUE, minWeight = Integer.MAX_VALUE;
        int totalStore = 0;
        StoreGoodsStatusEnum.CheckStatusEnum statusEnum = EnumHelper.getEnumType(StoreGoodsStatusEnum.CheckStatusEnum.class, status);
        //遍历货品列表，计算平均市场价，平均销售价，平均成本价,拼接货品ID字符串
        //若货品ID不为空，则查询货品，判断库存是否大于或等于预占库存
        //若货品列表中存在货品编号为空，则跑单元测试时保存货品后需要刷新
        if (productsList != null) {
            for (HbmSupplierProducts products : productsList) {
                //不允许商品ID为0，货品ID不为0的情况
                if ((goods.getSupplierGoodsId() == null || goods.getSupplierGoodsId() == 0) && (products.getSupplierProductId() != null && products.getSupplierProductId() != 0)) {
                    return new ApiResult("货品非法，请检查！");
                }
                if (products.getSupplierProductId() != null && products.getSupplierProductId() != 0) {
                    productIdStr += "," + products.getSupplierProductId();
                    //判断库存是否合法
                    HbmSupplierProducts existProduct = productsService.getProductByProductId(products.getSupplierProductId());
                    if (existProduct.getFreez() > products.getStore()) {
                        return new ApiResult("商品库存不能小于预占库存，请检查！");
                    }
                }
                //如果没有设置返利百分比，则默认为未个性化
                if(products.getDisRebateCustomPercent() == null || products.getDisRebateCustomPercent() == -1){
                    products.setDisRebateCustomPercent(goods.getDisRebatePercent());
                }
                minMktPrice = Math.min(minMktPrice, products.getMktPrice());
                minCost = Math.min(minCost, products.getCost());
                minPrice = Math.min(minPrice, products.getMinPrice());
                minWeight = Math.min(minWeight, products.getWeight());
                totalStore += products.getStore();
                products.setName(goods.getName());
                products.setUnit(goods.getUnit());
                if (goods.getSupplierGoodsId() != null && goods.getSupplierGoodsId() != 0) {
                    products.setSupplierGoodsId(goods.getSupplierGoodsId());
                }
                products.setStatus(statusEnum);
            }
            //如果没有规格时，保持货品和商品的返利一致
            if("[]".equals(goods.getSpecDesc()) && productsList.size() == 1){
                goods.setDisRebatePercent(productsList.get(0).getDisRebateCustomPercent());
            }
        }
        //删除该商品中不存在productIdStr中的货品
        //清空中间表数据数据
        List<HbmSupplierProducts> oldProduct = null;
        if (goods.getSupplierGoodsId() != null && goods.getSupplierGoodsId() != 0) {
            oldProduct = productsService.getProductListByGoodId(goods.getSupplierGoodsId());
        }
        if (oldProduct != null) {
            for (HbmSupplierProducts products : oldProduct) {
                if (productIdStr.length() == 0 || productIdStr.indexOf("," + products.getSupplierProductId()) <= -1) {
                    productsService.removeProduct(products);
                    goodsSpecIndexRepository.deleteByProductId(products.getSupplierProductId());
                }
            }
        }
        //保存或更新货品
        productsList = productsService.saveList(productsList);
        productsRepository.flush();
        List<HbmSpecification> supplierProductses = goods.getType().getSpecList();
        //遍历货品列表，拼接规格名序列化,货品集合序列化，返利个性化集合序列化
        // TODO: 2017-04-28 以下序列化应该交由jackson处理，有时间再改吧
        StringBuilder spec = new StringBuilder("{"),pdtDesc = new StringBuilder("["),disRebateCustomPercentDesc = new StringBuilder("[");
//        String spec = "{", pdtDesc = "[",disRebateCustomPercentDesc = "[";
        if (supplierProductses != null && supplierProductses.size() > 0) {
            for (HbmSpecification specification : supplierProductses) {
                if (spec.length() > 1) {
                    spec.append(",");
                }
                spec.append("\"").append(specification.getSpecId()).append("\":\"").append(specification.getSpecName()).append("\"");
            }

        }
        spec.append("}");
        if (productsList != null && productsList.size() > 0) {
            for (int i = 0; i < productsList.size(); i++) {
                if (pdtDesc.length() > 1) {
                    pdtDesc.append(",");
                }
                if(disRebateCustomPercentDesc.length() > 1){
                    disRebateCustomPercentDesc.append(",");
                }
                HbmSupplierProducts product = productsList.get(i);
                pdtDesc.append("{\"ProductId\":").append(product.getSupplierProductId()).append(",\"Desc\":\"").append(product.getPdtDesc()).append("\"}");
                if(product.getDisRebateCustomPercent() != null){
                    disRebateCustomPercentDesc.append("{proid:").append(product.getSupplierProductId())
                            .append(",per:").append(DoubleUtil.format(product.getDisRebateCustomPercent())).append("}");
                }
            }
        }
        pdtDesc.append("]");
        disRebateCustomPercentDesc.append("]");
        old.setType(goods.getType());
        old.setStoreId(goods.getStoreId());
        old.setBrand(goods.getBrand());
        old.setThumbnailPic(goods.getThumbnailPic());
        old.setSmallPic(goods.getSmallPic());
        old.setBigPic(goods.getBigPic());
        old.setBrief(goods.getBrief());
        old.setIntro(goods.getIntro());
        old.setMktPrice(new BigDecimal(minMktPrice).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
        old.setCost(new BigDecimal(minCost).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
        old.setPrice(new BigDecimal(minPrice).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
        old.setDisRebatePercent(new BigDecimal(goods.getDisRebatePercent()).setScale(2,BigDecimal.ROUND_HALF_EVEN).doubleValue());
        old.setBn(goods.getBn());
        old.setName(goods.getName());
        old.setSubTitle(goods.getSubTitle());
        old.setWeight(new BigDecimal(minWeight).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue());
        old.setUnit(goods.getUnit());
        old.setStore(totalStore);
        old.setSpec(spec.toString());
        old.setPdtDesc(pdtDesc.toString());
        old.setDisRebateCustomPercentDesc(disRebateCustomPercentDesc.toString());
        old.setSpecDesc(goods.getSpecDesc());
        old.setNotifyNum(goods.getNotifyNum());
        old.setLimitBuyNum(goods.getLimitBuyNum());
        old.setLastModify(new Date());
        old.setStatus(statusEnum);
        old.setRemark(goods.getRemark());
        old.setFreightTemplate(goods.getFreightTemplate());
        old.setShopCat(goods.getShopCat());
        if(goods.getMallGoodsId() != null && goods.getMallGoodsId() > 0){
            //平台同步商品数据到供应商
            old.setMallGoodsId(goods.getMallGoodsId());
        }
        //5.保存商品
        goods = supplierGoodsRepository.save(old);
        supplierGoodsRepository.flush();
        //6.若货品中的商品ID为0时，修改货品列表
        if (productsList != null && productsList.get(0).getSupplierGoodsId() == 0) {
            for (HbmSupplierProducts product : productsList) {
                product.setSupplierGoodsId(goods.getSupplierGoodsId());
            }
            productsService.saveList(productsList);
            productsRepository.flush();
        }
        //保存或更新Mall_Goods_Spec_Index关系表中
        List<HbmGoodsSpecIndex> specIndexList = new ArrayList<>();
        final int typeId = goods.getType().getTypeId();
        if (productsList != null && productsList.size() > 0) {
            productsList.forEach(p -> {
                if (p.getProps() != null && p.getProps().length() > 0) {
                    List<HbmGoodsSpecIndex> tempSpecIndexList = JSONArray.parseArray(p.getProps(), HbmGoodsSpecIndex.class);
                    tempSpecIndexList.forEach(index -> {
                        index.setTypeId(typeId);
                        index.setGoodsId(p.getSupplierGoodsId());
                        index.setProductId(p.getSupplierProductId());
                        index.setSpecValue(index.getSpecValue().substring(0,Math.min(index.getSpecValue().length(),100)));
                    });
                    specIndexList.addAll(tempSpecIndexList);
                }
            });
            productsService.saveSpecIndex(specIndexList);
        }
        //保存商品图片
        imgService.batchUpdateImg(imgList, goods.getSupplierGoodsId());
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
