package com.huotu.shopo2o.service.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.httputil.HttpClientUtil;
import com.huotu.shopo2o.common.httputil.HttpResult;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.ExcelHelper;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.SignBuilder;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallDelivery_;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.order.MallOrderItem;
import com.huotu.shopo2o.service.entity.order.MallOrder_;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.jsonformat.BatchDeliverResult;
import com.huotu.shopo2o.service.jsonformat.LogiModel;
import com.huotu.shopo2o.service.jsonformat.OrderForDelivery;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.repository.OffsetBasedPageRequest;
import com.huotu.shopo2o.service.repository.order.MallDeliveryRepository;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class MallDeliveryServiceImpl implements MallDeliveryService {
    private static final Log log = LogFactory.getLog(MallDeliveryServiceImpl.class);
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private MallDeliveryRepository mallDeliveryRepository;


    @Override
    public MallDelivery save(MallDelivery delivery) {
        return mallDeliveryRepository.save(delivery);
    }

    @Override
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo, Long customerId) throws UnsupportedEncodingException {
        MallOrder order = mallOrderService.findByOrderId(deliveryInfo.getOrderId());
        //判断订单是否发货
        if (order.deliveryable()) {
            String dicDeliverItemsStr = "";
            for (MallOrderItem orderItem : order.getOrderItems()) {
                //判断货品是否发货
                if (orderItem.deliverable()) {
                    if (!StringUtils.isEmpty(deliveryInfo.getSendItems()) &&
                            deliveryInfo.getSendItems().contains("|" + orderItem.getItemId() + "|")) {
                        dicDeliverItemsStr += orderItem.getItemId() + "," + orderItem.getNums() + "|";
                    }
                }
            }
            if (StringUtils.isEmpty(dicDeliverItemsStr)) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_NULL, "未找到发货信息", null);
            }
            //推送分销商进行发货
            Map<String, Object> map = new TreeMap<>();
            map.put("orderId", deliveryInfo.getOrderId());
            map.put("logisticsName", deliveryInfo.getLogiName());
            map.put("logisticsNo", deliveryInfo.getLogiNo());
            map.put("logiCode", deliveryInfo.getLogiCode());
            map.put("freight", Double.toString(deliveryInfo.getFreight()));
            if (deliveryInfo.getRemark() != null && !"".equals(deliveryInfo.getRemark())) {
                map.put("remark", deliveryInfo.getRemark());
            }
            map.put("supplierId", customerId);
            map.put("dicDeliverItemsStr", dicDeliverItemsStr.substring(0, dicDeliverItemsStr.length() - 1));
            String sign = SignBuilder.buildSignIgnoreEmpty(map, null, SysConstant.SUPPLIER_KEY);
            map.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/OrderApi/Deliver", map);
            ApiResult apiResult;
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
            } else {
                apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
            }
            return apiResult;
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "该订单无法发货", null);
    }

    @Override
    public Page<MallDelivery> getPage(Pageable pageable, Store store, DeliverySearcher deliverySearcher, String type) {
        Specification<MallDelivery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(MallDelivery_.store), store));
            predicates.add(cb.equal(cb.lower(root.get(MallDelivery_.type).as(String.class)), type.toLowerCase()));
            if (!StringUtils.isEmpty(deliverySearcher.getOrderId())) {
                predicates.add(cb.equal(root.get(MallDelivery_.order).get(MallOrder_.orderId).as(String.class), deliverySearcher.getOrderId()));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getDeliveryNo())) {
                predicates.add(cb.like(root.get(MallDelivery_.deliveryId),
                        "%" + deliverySearcher.getDeliveryNo() + "%"));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(MallDelivery_.createTime),
                        StringUtil.DateFormat(deliverySearcher.getBeginTime(), StringUtil.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get(MallDelivery_.createTime),
                        StringUtil.DateFormat(deliverySearcher.getEndTime(), StringUtil.TIME_WITHOUT_SECOND_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        pageable = new OffsetBasedPageRequest(pageable.getOffset(), pageable.getPageSize(), new Sort(Sort.Direction.DESC, "createTime"));
        return mallDeliveryRepository.findAll(specification, pageable);
    }

    @Override
    public MallDelivery findById(String id) {
        return mallDeliveryRepository.findOne(id);
    }

    @Override
    public ApiResult pushBatchDelivery(List<OrderForDelivery> orderForDeliveries, Long customerId) throws UnsupportedEncodingException {
        Map<String, Object> params = new TreeMap<>();

        params.put("lstDeliveryInfoJson", JSON.toJSONString(orderForDeliveries));
        params.put("customerId", customerId);
        String sign = SignBuilder.buildSignIgnoreEmpty(params, null, SysConstant.SUPPLIER_KEY);
        params.put("sign", sign);
        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/OrderApi/BatchDeliver", params);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            ApiResult<BatchDeliverResult> result = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<BatchDeliverResult>>() {
            });
            log.info("post batch delivery code:" + result.getCode() + " msg: " + result.getMsg());
            log.debug("data:" + result.getData());
            return result;
        }else{
            log.error("post batch delivery：" + httpResult.getHttpContent());
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<MallDelivery> deliveryList, String type) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        deliveryList.forEach(delivery->{
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getDeliveryId())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getOrder().getOrderId())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(StringUtil.DateFormat(delivery.getCreateTime(),StringUtil.TIME_PATTERN))));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getUserBaseInfo() == null ? null : delivery.getUserBaseInfo().getLoginName())));
            cellDescList.add(ExcelHelper.asCell(delivery.getFreight(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getShipName())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getShipTel()) + "/" + StringUtil.getNullStr(delivery.getShipMobile())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getShipAddr())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getShipZip())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getLogisticsName())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getLogisticsNo())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(delivery.getMemo())));
            rowAndCells.add(cellDescList);
        });
        String sheetName;
        if("delivery".equals(type)){
            sheetName = "发货单";
        }else{
            sheetName = "退货单";
        }
        return ExcelHelper.createWorkbook(sheetName, SysConstant.DELIVERY_ORDER_EXPORT_HEADER, rowAndCells);
    }

    @Override
    public ApiResult pushRefund(String orderId, LogiModel logiModel, int supplierId, String dicReturnItemsStr) throws UnsupportedEncodingException {
        Map<String, Object> param = new TreeMap<>();
        param.put("orderId", orderId);
        param.put("supplierId", supplierId);
        param.put("logiName", logiModel.getLogiCompanyChina());
        param.put("logiNo", logiModel.getLogiNo());
        param.put("logiMobile", logiModel.getLogiMobile());
        param.put("remark", logiModel.getLogiRemark());
        param.put("dicReturnItemsStr", dicReturnItemsStr);
        String sign = SignBuilder.buildSignIgnoreEmpty(param, null, SysConstant.SUPPLIER_KEY);
        param.put("sign", sign);

        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/OrderApi/ReturnProduct", param);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            return JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
        } else {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
    }
}
