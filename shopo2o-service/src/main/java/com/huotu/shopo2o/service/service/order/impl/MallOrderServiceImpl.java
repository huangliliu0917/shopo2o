package com.huotu.shopo2o.service.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.common.utils.DoubleUtil;
import com.huotu.shopo2o.common.utils.ExcelHelper;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.order.MallOrderItem;
import com.huotu.shopo2o.service.entity.order.MallOrderItem_;
import com.huotu.shopo2o.service.entity.order.MallOrder_;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.jsonformat.GoodCustomField;
import com.huotu.shopo2o.service.model.OrderDetailModel;
import com.huotu.shopo2o.service.repository.OffsetBasedPageRequest;
import com.huotu.shopo2o.service.repository.order.MallDeliveryRepository;
import com.huotu.shopo2o.service.repository.order.MallOrderRepository;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
@Service
public class MallOrderServiceImpl implements MallOrderService {
    @Autowired
    private MallOrderRepository mallOrderRepository;
    @Autowired
    private MallDeliveryRepository mallDeliveryRepository;

    @Override
    public MallOrder findByOrderId(String orderId) {
        return mallOrderRepository.findOne(orderId);
    }

    @Override
    public Page<MallOrder> findAll(Pageable pageable, OrderSearchCondition searchCondition) {
        Specification<MallOrder> specification = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get(MallOrder_.storeId), searchCondition.getStoreId()));
            //去除不可发货的订单（如拼团未成功的订单）
            predicates.add(cb.or(cb.isNull(root.get(MallOrder_.shipDisabled))
                    , cb.isFalse(root.get(MallOrder_.shipDisabled))));
            if (!StringUtils.isEmpty(searchCondition.getOrderId())) {
                predicates.add(cb.like(root.get(MallOrder_.orderId), "%" + searchCondition.getOrderId() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getOrderItemName())) {
                Join<MallOrder, MallOrderItem> orderItemJoin = root.join(MallOrder_.orderItems, JoinType.LEFT);
                predicates.add(cb.like(orderItemJoin.get(MallOrderItem_.name), "%" + searchCondition.getOrderItemName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipName())) {
                predicates.add(cb.like(root.get(MallOrder_.shipName), "%" + searchCondition.getShipName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipMobile())) {
                predicates.add(cb.equal(root.get(MallOrder_.shipMobile), searchCondition.getShipMobile()));
            }
            if (searchCondition.getOrderStatus() != -2) {
                predicates.add(cb.equal(root.get(MallOrder_.orderStatus)
                        , EnumHelper.getEnumType(OrderEnum.OrderStatus.class, searchCondition.getOrderStatus())));
            }
            if (searchCondition.getPayStatus() != -1) {
                predicates.add(cb.equal(root.get(MallOrder_.payStatus)
                        , EnumHelper.getEnumType(OrderEnum.PayStatus.class, searchCondition.getPayStatus())));
            }
            if (searchCondition.getShipStatus() != -1) {
                predicates.add(cb.equal(root.get(MallOrder_.shipStatus)
                        , EnumHelper.getEnumType(OrderEnum.ShipStatus.class, searchCondition.getShipStatus())));
            }
            if (searchCondition.getPaymentTypeStatus() != -1) {
                predicates.add(cb.equal(root.get(MallOrder_.paymentType)
                        , EnumHelper.getEnumType(OrderEnum.PaymentOptions.class, searchCondition.getPaymentTypeStatus())));
            }
            /*if (searchCondition.getSettleStatus() != -1) {
                predicates.add(cb.equal(root.get("settleStatus"), searchCondition.getSettleStatus()));
            }*/
            if (searchCondition.getSourceTypeStatus() != -1) {
                predicates.add(cb.equal(root.get(MallOrder_.orderSourceType)
                        , EnumHelper.getEnumType(OrderEnum.OrderSourceType.class, searchCondition.getSourceTypeStatus())));
            }
            if (!StringUtils.isEmpty(searchCondition.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(MallOrder_.createTime)
                        , StringUtil.DateFormat(searchCondition.getBeginTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get(MallOrder_.createTime)
                        , StringUtil.DateFormat(searchCondition.getEndTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getBeginPayTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(MallOrder_.payTime)
                        , StringUtil.DateFormat(searchCondition.getBeginPayTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getEndPayTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get(MallOrder_.payTime),
                        StringUtil.DateFormat(searchCondition.getEndPayTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序
        Sort sort;
        Sort.Direction direction = searchCondition.getOrderRule() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
        switch (searchCondition.getOrderType()) {
            case 1:
                //按支付时间
                sort = new Sort(direction, "payTime");
                break;
            case 2:
                sort = new Sort(direction, "finalAmount");
                break;
            default:
                sort = new Sort(direction, "createTime");
                break;
        }
        pageable = new OffsetBasedPageRequest(pageable.getOffset(),pageable.getPageSize(),sort);
        return mallOrderRepository.findAll(specification, pageable);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<MallOrder> orders) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        orders.forEach(order -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getOrderId())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getOrderName())));
            cellDescList.add(ExcelHelper.asCell(order.getOrderStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getItemNum(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getCreateTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getPayTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(order.getPayStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getShipStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getFinalAmount(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getCostFreight(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getPmtAmount(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getShipName())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getIdentityCard())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getShipMobile())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getShipAddr())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getBnList())));
            List<GoodCustomField> goodCustomFields = new ArrayList<>();
            order.getOrderItems().forEach(item -> {
                if (!StringUtils.isEmpty(item.getCustomFieldValues())) {
                    GoodCustomField goodCustomField = JSON.parseObject(item.getCustomFieldValues(), GoodCustomField.class);
                    goodCustomFields.add(goodCustomField);
                }
            });
            cellDescList.add(ExcelHelper.asCell(JSON.toJSONString(goodCustomFields)));

            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("订单列表", SysConstant.ORDER_EXPORT_HEADER, rowAndCells);
    }

    @Override
    public OrderDetailModel findOrderDetail(String orderId) {
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        MallOrder orders = mallOrderRepository.findOne(orderId);
        List<MallOrderItem> mallOrderItem = orders.getOrderItems();
        List<MallDelivery> deliveryList = mallDeliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.DEVERY.getCode());
        List<MallDelivery> refundList = mallDeliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.RETURN.getCode());
        orderDetailModel.setOrderId(orders.getOrderId());
        orderDetailModel.setOrderStatus(orders.getOrderStatus());
        orderDetailModel.setShipStatus(orders.getShipStatus());
        orderDetailModel.setPayStatus(orders.getPayStatus());
        orderDetailModel.setOrderSourceType(orders.getOrderSourceType());
        orderDetailModel.setDeliveryable(orders.deliveryable());
        if (deliveryList != null && deliveryList.size() > 0) {
            orderDetailModel.setDeliveryList(deliveryList);
        }
        if (refundList != null && refundList.size() > 0) {
            orderDetailModel.setRefundsList(refundList);
        }
        orderDetailModel.setShipName(orders.getShipName());
        orderDetailModel.setShipTel(orders.getShipTel());
        orderDetailModel.setShipMobile(orders.getShipMobile());
        orderDetailModel.setShipArea(orders.getShipArea());
        orderDetailModel.setShipAddr(orders.getShipAddr());
        orderDetailModel.setFinalAmount(orders.getFinalAmount());
        orderDetailModel.setCostFreight(orders.getCostFreight());
        orderDetailModel.setCreateTime(StringUtil.DateFormat(orders.getCreateTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setPayTime(StringUtil.DateFormat(orders.getPayTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setSupOrderItemList(mallOrderItem);
        orderDetailModel.setRemark(orders.getRemark());
        orderDetailModel.setMemo(orders.getMemo());
        orderDetailModel.setIdentityCard(orders.getIdentityCard());
        double costPrice = mallOrderItem.stream().mapToDouble(p -> p.getCost() * p.getNums()).sum();
        double disRebatePrice = mallOrderItem.stream().filter(p -> p.getDisRebateAssigned() != null).mapToDouble(MallOrderItem::getDisRebateAssigned).sum();
        double commissionPrice = mallOrderItem.stream().filter(p -> p.getCommissionAssigned() != null).mapToDouble(MallOrderItem::getCommissionAssigned).sum();
        orderDetailModel.setCostPrice(DoubleUtil.format(costPrice));
        orderDetailModel.setDisRebate(DoubleUtil.format(disRebatePrice));
        orderDetailModel.setCommission(DoubleUtil.format(commissionPrice));
        return orderDetailModel;
    }

}
