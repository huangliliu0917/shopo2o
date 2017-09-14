package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallAfterSales;
import com.huotu.shopo2o.service.enums.AfterSaleEnum;
import com.huotu.shopo2o.service.searchable.AfterSaleSearch;
import com.huotu.shopo2o.service.service.order.MallAfterSalesService;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by hxh on 2017-09-14.
 */
@Controller
@RequestMapping("/afterSale")
public class AfterSaleController {

    @Autowired
    private MallAfterSalesService mallAfterSalesService;
    @Autowired
    private StaticResourceService resourceService;
    private static final int PAGE_SIZE = 40;

    @RequestMapping("/afterSaleList")
    public String afterSaleList(
            @AuthenticationPrincipal MallCustomer mallCustomer,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            AfterSaleSearch afterSaleSearch
    ) {
        ModelAndView modelAndView = new ModelAndView();
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
            modelAndView.addObject("afterSales", afterSales.getContent());
            modelAndView.addObject("pageSize", PAGE_SIZE);
            modelAndView.addObject("totalRecords", afterSales.getTotalElements());
            modelAndView.addObject("totalPages", afterSales.getTotalPages());
            modelAndView.addObject("afterSaleSearch", afterSaleSearch);
            modelAndView.addObject("pageIndex", pageIndex);
            modelAndView.addObject("afterSaleStatusList", AfterSaleEnum.AfterSaleStatus.values());
        }
        return "order/afterSales/after_sales_list";
    }
}
