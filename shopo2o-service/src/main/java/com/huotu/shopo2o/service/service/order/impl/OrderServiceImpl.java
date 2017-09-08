package com.huotu.shopo2o.service.service.order.impl;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.repository.MallOrderRepository;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import com.huotu.shopo2o.service.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private MallOrderRepository mallOrderRepository;

    @Override
    public Page<MallOrder> findAll(int pageIndex, OrderSearchCondition searchCondition) {
        Specification<MallOrder> specification = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("supplierId").as(Long.class), searchCondition.getSupplierId()));
            predicates.add(cb.or(cb.isNull(root.get("shipDisabled").as(Boolean.class)),
                    cb.equal(root.get("shipDisabled").as(Boolean.class), false)));
            if (!StringUtils.isEmpty(searchCondition.getOrderId())) {
                predicates.add(cb.like(root.get("orderId").as(String.class), searchCondition.getOrderId() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getOrderItemName())) {
                predicates.add(cb.like(root.get("orderItems").get("name").as(String.class), "%" + searchCondition.getOrderItemName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipName())) {
                predicates.add(cb.like(root.get("shipName").as(String.class), "%" + searchCondition.getShipName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipMobile())) {
                predicates.add(cb.equal(root.get("shipMobile").as(String.class), searchCondition.getShipMobile()));
            }
            if (searchCondition.getOrderStatus() != -2) {
                predicates.add(cb.equal(root.get("orderStatus").as(OrderEnum.OrderStatus.class),
                        EnumHelper.getEnumType(OrderEnum.OrderStatus.class, searchCondition.getOrderStatus())));
            }
            if (searchCondition.getPayStatus() != -1) {
                predicates.add(cb.equal(root.get("payStatus").as(OrderEnum.PayStatus.class),
                        EnumHelper.getEnumType(OrderEnum.PayStatus.class, searchCondition.getPayStatus())));
            }
            if (searchCondition.getShipStatus() != -1) {
                predicates.add(cb.equal(root.get("shipStatus").as(OrderEnum.ShipStatus.class),
                        EnumHelper.getEnumType(OrderEnum.ShipStatus.class, searchCondition.getShipStatus())));
            }
            if (searchCondition.getPaymentTypeStatus() != -1) {
                predicates.add(cb.equal(root.get("paymentType").as(OrderEnum.PaymentOptions.class),
                        EnumHelper.getEnumType(OrderEnum.PaymentOptions.class, searchCondition.getPaymentTypeStatus())));
            }
            if (searchCondition.getSettleStatus() != -1) {
                predicates.add(cb.equal(root.get("settleStatus").as(Integer.class), searchCondition.getSettleStatus()));
            }
            if (searchCondition.getSourceTypeStatus() != -1) {
                predicates.add(cb.equal(root.get("orderSourceType").as(OrderEnum.OrderSourceType.class),
                        EnumHelper.getEnumType(OrderEnum.OrderSourceType.class, searchCondition.getSourceTypeStatus())));
            }
            if (!StringUtils.isEmpty(searchCondition.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        getDate(searchCondition.getBeginTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        getDate(searchCondition.getEndTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getBeginPayTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("payTime").as(Date.class), getDate(searchCondition.getBeginPayTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
            }
            if (!StringUtils.isEmpty(searchCondition.getEndPayTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("payTime").as(Date.class),
                        getDate(searchCondition.getEndPayTime(), Constant.TIME_WITHOUT_SECOND_PATTERN)));
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
        Pageable pageable = new PageRequest(pageIndex - 1, Constant.PAGE_SIZE, sort);
        return mallOrderRepository.findAll(specification, pageable);
    }

    /**
     * 根据字符串格式化成时?
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date getDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
        }

        return date;
    }
}
