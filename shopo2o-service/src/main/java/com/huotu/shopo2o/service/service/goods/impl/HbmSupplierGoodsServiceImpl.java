package com.huotu.shopo2o.service.service.goods.impl;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.good.HbmSupplierGoods;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.good.MallGood;
import com.huotu.shopo2o.service.entity.store.SupShopCat;
import com.huotu.shopo2o.service.enums.StoreGoodsStatusEnum;
import com.huotu.shopo2o.service.model.HbmSupplierGoodsSearcher;
import com.huotu.shopo2o.service.repository.good.HbmSupplierGoodsRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.repository.good.MallGoodRepository;
import com.huotu.shopo2o.service.service.goods.HbmSupplierGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
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
}
