package com.huotu.shopo2o.service.service.order.impl;

import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.shopo2o.service.searchable.AfterSaleSearch;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
@Service
public class MallAfterSalesServiceImpl implements MallAfterSalesService{
    @Autowired
    private MallAfterSalesRepository mallAfterSalesRepository;

    @Override
    public Page<MallAfterSales> findAll(int pageIndex, int pageSize, Long supplierId, AfterSaleSearch afterSaleSearch) {
        Specification<MallAfterSales> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("supplierId").as(Integer.class), supplierId));
            if (!StringUtils.isEmpty(afterSaleSearch.getBeginTime())) {
                Date beginTime = StringUtil.DateFormat(afterSaleSearch.getBeginTime(), StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), beginTime));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getEndTime())) {
                Date endTime = StringUtil.DateFormat(afterSaleSearch.getEndTime(), StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), endTime));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getAfterId())) {
//                predicates.add(criteriaBuilder.like(root.get("afterId").as(String.class), afterSaleSearch.getAfterId() + "%"));
                predicates.add(criteriaBuilder.equal(root.get("afterId").as(String.class), afterSaleSearch.getAfterId()));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getOrderId())) {
//                predicates.add(criteriaBuilder.like(root.get("orderId").as(String.class), afterSaleSearch.getOrderId() + "%"));
                predicates.add(criteriaBuilder.equal(root.get("orderId").as(String.class), afterSaleSearch.getOrderId()));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getMobile())) {
                predicates.add(criteriaBuilder.equal(root.get("applyMobile").as(String.class), afterSaleSearch.getMobile()));
            }
            if (afterSaleSearch.getAfterSaleStatus() != null && afterSaleSearch.getAfterSaleStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("afterSaleStatus").as(AfterSaleEnum.AfterSaleStatus.class),
                        EnumHelper.getEnumType(AfterSaleEnum.AfterSaleStatus.class, afterSaleSearch.getAfterSaleStatus())));
            }
            if(!StringUtils.isEmpty(afterSaleSearch.getCreateBeginTime())){
                Date createBeginTime = StringUtil.DateFormat(afterSaleSearch.getCreateBeginTime(),StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderCreateTime").as(Date.class),createBeginTime));
            }
            if(!StringUtils.isEmpty(afterSaleSearch.getCreateEndTime())){
                Date createEndTime = StringUtil.DateFormat(afterSaleSearch.getCreateEndTime(),StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderCreateTime").as(Date.class),createEndTime));
            }
            if(!StringUtils.isEmpty(afterSaleSearch.getPayBeginTime())){
                Date payBeginTime = StringUtil.DateFormat(afterSaleSearch.getPayBeginTime(),StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderPayTime").as(Date.class),payBeginTime));
            }
            if(!StringUtils.isEmpty(afterSaleSearch.getPayEndTime())){
                Date payEndTime = StringUtil.DateFormat(afterSaleSearch.getPayEndTime(),StringUtil.TIME_WITHOUT_SECOND_PATTERN);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderPayTime").as(Date.class),payEndTime));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return mallAfterSalesRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize,
                new Sort(Sort.Direction.DESC, "createTime")));
    }

    @Override
    public int UnhandledCount(int storeId) {
        List<AfterSaleEnum.AfterSaleStatus> afterSaleStatuses = new ArrayList<>();
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.CANCELED);
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.REFUND_SUCCESS);
        afterSaleStatuses.add(AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED);
        return mallAfterSalesRepository.countByStoreIdAndAfterSaleStatusNotIn(storeId, afterSaleStatuses);
    }
}
