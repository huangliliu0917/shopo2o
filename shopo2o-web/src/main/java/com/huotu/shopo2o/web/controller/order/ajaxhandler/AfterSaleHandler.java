package com.huotu.shopo2o.web.controller.order.ajaxhandler;

import com.alibaba.fastjson.JSON;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.jsonformat.LogiModel;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hxh on 2017-09-15.
 */
@Controller
@RequestMapping("/afterSale/ajax")
public class AfterSaleHandler {
    @Autowired
    private MallAfterSalesService mallAfterSalesService;
    @Autowired
    private MallDeliveryService mallDeliveryService;
    /**
     * 同意退款
     *
     * @param afterId
     * @return
     */
    @RequestMapping("/refundAgree")
    @ResponseBody
    public ApiResult refundAgree(String afterId) throws UnsupportedEncodingException {
        MallAfterSales afterSales = mallAfterSalesService.findByAfterId(afterId);
        if (afterSales.refundable()) {
            //先到伙伴商城那里执行退货
            ApiResult apiResult = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            if (afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM &&
                    afterSales.getAfterSaleType() == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND) {
                //生成退货单
                List<MallAfterSalesItem> forReturn = afterSales.getAfterSalesItems().stream()
                        .filter(item -> item.getIsLogic() == 1)
                        .collect(Collectors.toList());
                if (forReturn.size() > 0) {
                    LogiModel logiModel = JSON.parseObject(forReturn.get(0).getAfterContext(), LogiModel.class);
                    String dicReturnItemsStr = afterSales.getOrderItem().getItemId() + "," + afterSales.getOrderItem().getNums();
                    apiResult = mallDeliveryService.pushRefund(afterSales.getOrderId(), logiModel, afterSales.getSupplierId(), dicReturnItemsStr);
                }
            }
            if (apiResult.getCode() == ResultCodeEnum.SUCCESS.getResultCode()) {
                //更改售后表状态,更改协商表
                mallAfterSalesService.afterSaleAgree(afterSales, "同意退款，等待退款！",
                        AfterSaleEnum.AfterSaleStatus.REFUNDING, AfterSaleEnum.AfterItemsStatus.REFUNDING);
            }
            return apiResult;
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "操作失败,请检查售后状态", null);
    }
    @RequestMapping("/batchRefundAgree")
    @ResponseBody
    public ApiResult batchRefundAgree(
            @LoginUser MallCustomer mallCustomer, @RequestParam("afterId") String... afterIdList) throws UnsupportedEncodingException {
        // TODO: 2016/9/11 暂时循环操作，以后增加批量同意退款接口
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "操作失败,请检查售后状态", null);
        if(afterIdList != null && afterIdList.length > 0){
            for(int i = 0 ; i < afterIdList.length ; i ++){
                ApiResult refundAgreeResult = refundAgree(afterIdList[i]);
                if(refundAgreeResult.getCode() == 200){
                    result =  refundAgreeResult;
                }
            }
        }
        return result;
    }
}
