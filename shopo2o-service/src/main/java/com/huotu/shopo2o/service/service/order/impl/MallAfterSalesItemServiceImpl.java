package com.huotu.shopo2o.service.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.jsonformat.LogiModel;
import com.huotu.shopo2o.service.repository.order.MallAfterSalesItemRepository;
import com.huotu.shopo2o.service.service.order.MallAfterSalesItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
@Service
public class MallAfterSalesItemServiceImpl implements MallAfterSalesItemService{

    @Autowired
    private MallAfterSalesItemRepository mallAfterSalesItemRepository;
    @Override
    public List<MallAfterSalesItem> findByAfterId(String afterId) {
        List<MallAfterSalesItem> afterSalesItems = mallAfterSalesItemRepository.findByAfterSales_AfterIdOrderByItemIdDesc(afterId);
        afterSalesItems.forEach(item -> {
            if (!StringUtils.isEmpty(item.getImgFiles())) {
                String[] imgFileArrays = item.getImgFiles().split(",");
                List<String> imgFileList = new ArrayList<>();
                for (String file : imgFileArrays) {
                    imgFileList.add(SysConstant.HUOBANMALL_RESOURCE_HOST + file);
                }
                item.setImgFileList(imgFileList);
            }

            if (item.getIsLogic() == 1) {
                LogiModel logiModel = JSON.parseObject(item.getAfterContext(), LogiModel.class);
                if (!StringUtils.isEmpty(logiModel.getLogiImg())) {
                    String[] logiImgArray = logiModel.getLogiImg().split(",");
                    List<String> logiImgs = new ArrayList<>();
                    for (String img : logiImgArray) {
                        logiImgs.add(SysConstant.HUOBANMALL_RESOURCE_HOST + img);
                    }
                    logiModel.setLogiImgs(logiImgs);
                }
                item.setLogiModel(logiModel);
            }
            if (item.getAfterItemsStatus() == AfterSaleEnum.AfterItemsStatus.REFUNDING_FINISH) {
                List<String> refundInfo = new ArrayList<>();
                String[] refundInfoInfoArray = item.getReply().split("\\|");
                for (String info : refundInfoInfoArray) {
                    refundInfo.add(info);
                }
                item.setRefundInfos(refundInfo);
            }
        });
        return afterSalesItems;    }

    @Override
    public MallAfterSalesItem findTopByIsLogic(MallAfterSales afterSales, int isLogic) {
        return mallAfterSalesItemRepository.findTopByAfterSalesAndIsLogicNotOrderByItemIdDesc(afterSales, isLogic);

    }

    @Override
    @Transactional
    public MallAfterSalesItem save(MallAfterSalesItem afterSalesItem) {
        return mallAfterSalesItemRepository.save(afterSalesItem);
    }
}
