package com.huotu.shopo2o.service.service.goods.impl;

import com.huotu.shopo2o.service.entity.good.HbmImage;
import com.huotu.shopo2o.service.repository.good.HbmImageRepository;
import com.huotu.shopo2o.service.service.goods.HbmImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Service
public class HbmImageServiceImpl implements HbmImageService {

    @Autowired
    private HbmImageRepository imageRepository;

    @Override
    public void batchUpdateImg(List<HbmImage> imgList, Integer supplierGoodId) {
        if (imgList == null || imgList.size() == 0) {
            return;
        }
        //遍历图片列表，获取图片ID
        List<Integer> imgIds = new ArrayList<>();
        for (HbmImage img : imgList) {
            if (img.getGimageId() != null && img.getGimageId() != 0) {
                imgIds.add(img.getGimageId());
            }
        }
        //根据supplierGoodsId,删除不存在editIds中的图片
        if (imgIds != null && imgIds.size() > 0) {
            imageRepository.deleteBySupplierGoodIdAndGimageIdNotIn(supplierGoodId, imgIds);
        } else {
            imageRepository.deleteBySupplierGoodId(supplierGoodId);
        }
        //遍历图片，根据ID查找，若存在则更新，若不存在则新增
        imgList.forEach(p -> {
            HbmImage old;
            if (p.getGimageId() == null || p.getGimageId() == 0) {
                //图片ID不存在，新增图片
                old = p;
                old.setGimageId(null);
                old.setSupplierGoodId(supplierGoodId);
                old.setUpTime(new Date());
            } else {
                //图片ID存在，修改图片
                old = imageRepository.findOne(p.getGimageId());
                if (old == null) {
                    old = new HbmImage();
                    old.setSupplierGoodId(supplierGoodId);
                    old.setSupplierId(p.getSupplierId());
                    old.setUpTime(new Date());
                }
                old.setSmall(p.getSmall());
                old.setBig(p.getBig());
                old.setThumbnail(p.getThumbnail());
                old.setOrderBy(p.getOrderBy());
                old.setSrcWidth(p.getSrcWidth());
                old.setSrcHeight(p.getSrcHeight());
            }
            imageRepository.saveAndFlush(old);
        });
    }

    @Override
    public List<HbmImage> findBySupplierGoodId(Integer supplierGoodsId) {
        return imageRepository.findBySupplierGoodId(supplierGoodsId);
    }


}