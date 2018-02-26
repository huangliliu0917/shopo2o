package com.huotu.shopo2o.service.service.goods.impl;

import com.huotu.shopo2o.service.entity.good.HbmGoodsSpecIndex;
import com.huotu.shopo2o.service.entity.good.HbmSupplierProducts;
import com.huotu.shopo2o.service.entity.good.MallProduct;
import com.huotu.shopo2o.service.repository.good.HbmGoodsSpecIndexRepository;
import com.huotu.shopo2o.service.repository.good.HbmSupplierProductsRepository;
import com.huotu.shopo2o.service.repository.good.MallProductRepository;
import com.huotu.shopo2o.service.service.goods.HbmSupplierProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by xyr on 2017/10/11.
 */
@Service
public class HbmSupplierProductsServiceImpl implements HbmSupplierProductsService {

    @Autowired
    private HbmSupplierProductsRepository productsRepository;

    @Autowired
    private MallProductRepository mallProductRepository;

    @Autowired
    private HbmGoodsSpecIndexRepository goodsSpecIndexRepository;

    @Override
    public HbmSupplierProducts getProductByProductId(Integer supplierProductId) {
        return productsRepository.findOne(supplierProductId);
    }

    /**
     * 根据商品ID获取货品列表
     */
    @Override
    public List<HbmSupplierProducts> getProductListByGoodId(int goodId) {
        List<HbmSupplierProducts> productsList = productsRepository.findBySupplierGoodsId(goodId);
        productsList.forEach(product -> {
            if (product.getMallProductId() != null && !product.getMallProductId().equals(0)) {
                MallProduct mallProduct = mallProductRepository.findOne(product.getMallProductId());
                if(mallProduct != null){
                    product.setStore(mallProduct.getStore());
                    product.setFreez(mallProduct.getFreez());
                    product.setMarketable(mallProduct.getMarketable());
                }
            }
        });
        return productsList;
    }

    @Override
    public void removeProduct(HbmSupplierProducts products) {
        //删除货品和中间表
        goodsSpecIndexRepository.deleteByProductId(products.getSupplierProductId());
        productsRepository.delete(products);
    }

    @Override
    public List<HbmSupplierProducts> saveList(List<HbmSupplierProducts> productsList) {
        if (productsList != null) {
            productsList.forEach(p -> {
                HbmSupplierProducts old;
                if (p.getSupplierProductId() == null || p.getSupplierProductId() == 0) {
                    //货品ID为0时，新建货品和中间表
                    old = p;
                    old.setSupplierProductId(null);
                    old.setSupplierGoodsId(p.getSupplierGoodsId());
                } else {
                    //货品ID不为0是，查找货品和中间表
                    old = productsRepository.findBySupplierProductId(p.getSupplierProductId());
                    if (old == null) {
                        old = p;
                        old.setSupplierProductId(null);
                    }
                    old.setSupplierGoodsId(p.getSupplierGoodsId());
                }
                old.setBn(p.getBn());
                old.setBarcode(p.getBarcode());
                old.setTitle(p.getTitle());
                old.setMinPrice(p.getMinPrice());
                old.setMaxPrice(p.getMaxPrice());
                old.setCost(p.getCost());
                old.setMktPrice(p.getMktPrice());
                old.setName(p.getName());
                old.setWeight(p.getWeight());
                old.setUnit(p.getUnit());
                old.setStore(p.getStore());
                old.setPdtDesc(p.getPdtDesc());
                old.setProps(p.getProps());
                old.setUserPriceInfo(p.getUserPriceInfo());
                old.setUserIntegralInfo(p.getUserIntegralInfo());
                old.setMinUserPrice(p.getMinUserPrice());
                old.setDisRebateCustomPercent(p.getDisRebateCustomPercent());
//                old.setRebateLayerConfig(p.getRebateLayerConfig());
                old.setStatus(p.getStatus());
                old.setLastModify(new Date());
                productsRepository.save(old);
                productsRepository.flush();
            });
        }
        return productsList;
    }

    @Override
    public List<HbmGoodsSpecIndex> saveSpecIndex(List<HbmGoodsSpecIndex> specIndexList) {
        if (specIndexList != null) {
            specIndexList.forEach(index -> {
                HbmGoodsSpecIndex old = goodsSpecIndexRepository.findByProductIdAndSpecValueId(index.getProductId(), index.getSpecValueId());
                if (old == null) {
                    index = goodsSpecIndexRepository.save(index);
                } else {
                    if (old.getSpecValue() == null || !old.getSpecValue().equals(index.getSpecValue())) {
                        //修改模式下只能修改规格值别名
                        old.setSpecValue(index.getSpecValue());
                        index = goodsSpecIndexRepository.save(old);
                    }
                }
            });
        }
        return specIndexList;
    }
}
