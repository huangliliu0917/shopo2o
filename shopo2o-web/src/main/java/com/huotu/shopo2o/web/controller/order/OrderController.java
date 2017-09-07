package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import com.huotu.shopo2o.service.service.order.OrderService;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hxh on 2017-09-07.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private StaticResourceService staticResourceService;

    @RequestMapping("/getOrderList")
    public String getOrderList(@AuthenticationPrincipal MallCustomer customer, @ModelAttribute("orderSearchCondition") OrderSearchCondition searchCondition, @RequestParam(required = false, defaultValue = "1") int pageIndex, Model model) {
        searchCondition.setSupplierId(customer.getCustomerId());
        Page<MallOrder> orderPageList = orderService.findAll(pageIndex, searchCondition);
        if (orderPageList != null) {
            orderPageList.forEach(order -> {
                order.getOrderItems().forEach(item -> {
                    URI picUri = null;
                    try {
                        if (!StringUtils.isEmpty(item.getThumbnailPic())) {
                            picUri = staticResourceService.getResource(StaticResourceService.huobanmallMode, item.getThumbnailPic());
                        }
                    } catch (URISyntaxException e) {
                    }
                    item.setPicUri(picUri);
                });
            });
        }
        model.addAttribute("payStatusEnums", OrderEnum.PayStatus.values());
        model.addAttribute("shipStatusEnums", OrderEnum.ShipStatus.values());
        model.addAttribute("orderStatusEnums", OrderEnum.OrderStatus.values());
        model.addAttribute("paymentTypeEnums", OrderEnum.PaymentOptions.values());
        model.addAttribute("sourceTypeEnums", OrderEnum.OrderSourceType.values());
        model.addAttribute("ordersList", orderPageList.getContent());
        model.addAttribute("totalPages", orderPageList.getTotalPages());
        model.addAttribute("supplierId", customer.getCustomerId());
        model.addAttribute("totalRecords", orderPageList.getTotalElements());
        model.addAttribute("pageSize", orderPageList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        return "order/order_list";
    }
}
