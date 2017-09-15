package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by hxh on 2017-09-14.
 */
@Controller
@RequestMapping("/order")
public class DeliveryController {
    @Autowired
    private MallOrderService orderService;
    @Autowired
    private MallDeliveryService mallDeliveryService;

    @RequestMapping("/deliveries")
    public ModelAndView showDeliveryList(
            @LoginUser MallCustomer customer,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @RequestParam(required = false, defaultValue = "delivery") String type,
            @ModelAttribute("deliverySearcher") DeliverySearcher deliverySearcher
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        if (type.equals("delivery"))
            modelAndView.setViewName("order/delivery/delivery_list");
        else
            modelAndView.setViewName("order/delivery/return_list");

        Page<MallDelivery> ordersDeliveryPage = mallDeliveryService.getPage(new PageRequest(pageIndex - 1, Constant.PAGE_SIZE), customer.getStore(), deliverySearcher, type);
        modelAndView.addObject("deliveryList", ordersDeliveryPage.getContent());
        modelAndView.addObject("totalRecords", ordersDeliveryPage.getTotalElements());
        modelAndView.addObject("totalPages", ordersDeliveryPage.getTotalPages());
        modelAndView.addObject("pageSize", ordersDeliveryPage.getSize());
        modelAndView.addObject("pageIndex", pageIndex);
        modelAndView.addObject("deliverySearcher", deliverySearcher);
        return modelAndView;
    }

    /**
     * 加载发货单页面
     *
     * @param orderId
     * @param model
     * @param pageIndex
     * @return
     */
    @RequestMapping("/deliveryView")
    public String showConsignFlow(
            String orderId, Model model,
            @RequestParam(required = false, defaultValue = "1") int pageIndex
    ) {
        MallOrder order = orderService.findByOrderId(orderId);
        model.addAttribute("order", order);
        model.addAttribute("pageIndex", pageIndex);
        return "order/order_delivery";
    }

    /**
     * 发货单保存接口
     */
    @RequestMapping(value = "/delivery")
    @ResponseBody
    public ApiResult addDelivery(
            @LoginUser MallCustomer mallCustomer,
            DeliveryInfo deliveryInfo
    ) throws Exception {
        return mallDeliveryService.pushDelivery(deliveryInfo);
    }
    @RequestMapping("/deliveryInfo")
    public String deliveryInfo(
            String deliveryId,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            Model model
    ) {
        MallDelivery delivery = mallDeliveryService.findById(deliveryId);
        model.addAttribute("deliveryInfo", delivery);
        model.addAttribute("parentPageIndex", pageIndex);
        return "order/delivery/delivery_info";
    }
}
