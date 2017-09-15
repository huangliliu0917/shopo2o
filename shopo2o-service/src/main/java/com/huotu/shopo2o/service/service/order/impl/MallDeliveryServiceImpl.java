package com.huotu.shopo2o.service.service.order.impl;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallDelivery_;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.order.MallOrder_;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.repository.OffsetBasedPageRequest;
import com.huotu.shopo2o.service.repository.order.MallDeliveryRepository;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
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

/**
 * Created by hxh on 2017-09-11.
 */
@Service
public class MallDeliveryServiceImpl implements MallDeliveryService {
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private MallDeliveryRepository mallDeliveryRepository;



    @Override
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo) throws UnsupportedEncodingException {
        MallOrder order = mallOrderService.findByOrderId(deliveryInfo.getOrderId());
        //判断订单是否发货
        // TODO: 2017-09-11 发货（不清楚怎么实现）
        return null;
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
                        "%"+deliverySearcher.getDeliveryNo() + "%"));
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
        pageable = new OffsetBasedPageRequest(pageable.getOffset(),pageable.getPageSize(),new Sort(Sort.Direction.DESC, "createTime"));
        return mallDeliveryRepository.findAll(specification, pageable);
    }

    @Override
    public MallDelivery findById(String id) {
        return mallDeliveryRepository.findOne(id);
    }
}
