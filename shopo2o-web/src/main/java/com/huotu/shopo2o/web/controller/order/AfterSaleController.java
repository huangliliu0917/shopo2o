package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.entity.order.MallAfterSalesItem;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.entity.user.UserBaseInfo;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.searchable.AfterSaleSearch;
import com.huotu.shopo2o.service.service.order.MallAfterSalesItemService;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import com.huotu.shopo2o.service.service.user.UserBaseInfoService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by hxh on 2017-09-14.
 */
@Controller
@RequestMapping("/afterSale")
public class AfterSaleController {

    @Autowired
    private MallAfterSalesService mallAfterSalesService;
    @Autowired
    private MallAfterSalesItemService mallAfterSalesItemService;
    @Autowired
    private MallOrderService mallOrderService;
    @Autowired
    private UserBaseInfoService userBaseInfoService;
    @Autowired
    private StaticResourceService resourceService;
    private static final int PAGE_SIZE = 40;

    @RequestMapping("/afterSaleList")
    public String afterSaleList(
            @LoginUser MallCustomer mallCustomer,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @ModelAttribute("afterSaleSearch") AfterSaleSearch afterSaleSearch,
            Model model
    ) {
        if (mallCustomer.getCustomerId() > 0) {
            Page<MallAfterSales> afterSales = mallAfterSalesService.findAll(pageIndex, PAGE_SIZE, mallCustomer.getCustomerId(), afterSaleSearch);
            if (afterSales != null && afterSales.getContent() != null && afterSales.getContent().size() > 0) {
                afterSales.getContent().forEach(afterSale -> {
                    URI imgUri = null;
                    if (!StringUtils.isEmpty(afterSale.getProductImg())) {
                        try {
                            imgUri = resourceService.getResource(StaticResourceService.huobanmallMode, afterSale.getProductImg());
                            afterSale.setImgUri(imgUri);
                        } catch (URISyntaxException e) {
                        }
                    }
                });
            }
            model.addAttribute("afterSales", afterSales.getContent());
            model.addAttribute("pageSize", PAGE_SIZE);
            model.addAttribute("totalRecords", afterSales.getTotalElements());
            model.addAttribute("totalPages", afterSales.getTotalPages());
            model.addAttribute("afterSaleSearch", afterSaleSearch);
            model.addAttribute("pageIndex", pageIndex);
            model.addAttribute("afterSaleStatusList", AfterSaleEnum.AfterSaleStatus.values());
        }
        return "order/afterSales/after_sales_list";
    }
    @RequestMapping("/afterSalesDetail")
    public String afterSaleDetail(
            String afterId,
            Model model
    ) {
        MallAfterSales afterSales = mallAfterSalesService.findByAfterId(afterId);
        List<MallAfterSalesItem> afterSalesItems = mallAfterSalesItemService.findByAfterId(afterId);
        UserBaseInfo userBaseInfo = userBaseInfoService.findById(afterSales.getMemberId());
        MallOrder order = mallOrderService.findByOrderId(afterSales.getOrderId());

        model.addAttribute("afterSales", afterSales);
        model.addAttribute("afterSalesItems", afterSalesItems);
        model.addAttribute("hbResourceHost", SysConstant.HUOBANMALL_RESOURCE_HOST);
        model.addAttribute("userInfo", userBaseInfo);
        model.addAttribute("order", order);

        return "order/afterSales/after_sales_detail";
    }
}
