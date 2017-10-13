package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.order.MallDelivery;
import com.huotu.shopo2o.service.model.DeliveryInfo;
import com.huotu.shopo2o.service.repository.OffsetBasedPageRequest;
import com.huotu.shopo2o.service.searchable.DeliverySearcher;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

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
     * 发货单保存接口
     */
    @RequestMapping(value = "/delivery")
    @ResponseBody
    public ApiResult addDelivery(
            @LoginUser MallCustomer mallCustomer,
            DeliveryInfo deliveryInfo
    ) throws Exception {
        // TODO: 2017-10-09 发货提示签名无效
        return mallDeliveryService.pushDelivery(deliveryInfo,mallCustomer.getCustomerId());
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
    @RequestMapping("/exportDelivery")
    @SuppressWarnings("Duplicates")
    public void exportExcel(DeliverySearcher deliverySearcher,
                            @RequestParam(required = false, defaultValue = "delivery") String type,
                            @LoginUser MallCustomer customer,
                            int txtBeginPage,
                            int txtEndPage,
                            HttpSession session,
                            HttpServletResponse response) {
        int pageSize = Constant.PAGE_SIZE * (txtEndPage - txtBeginPage + 1);
        int offset = (txtBeginPage - 1) * Constant.PAGE_SIZE;
        Page<MallDelivery> ordersDeliveryPage = mallDeliveryService.getPage(
                new OffsetBasedPageRequest(offset, pageSize), customer.getStore(), deliverySearcher, type);
        List<MallDelivery> deliveryList = ordersDeliveryPage.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "order-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = mallDeliveryService.createWorkBook(deliveryList, type);
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }
    @RequestMapping( "/editDelivery")
    @ResponseBody
    public ApiResult editDelivery(String deliveryId, DeliveryInfo deliveryInfo) {
        MallDelivery delivery = mallDeliveryService.findById(deliveryId);
        if(delivery==null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        delivery.setLogisticsNo(deliveryInfo.getLogiNo());
        delivery.setMemo(deliveryInfo.getRemark());
        delivery.setFreight(deliveryInfo.getFreight());
        mallDeliveryService.save(delivery);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
