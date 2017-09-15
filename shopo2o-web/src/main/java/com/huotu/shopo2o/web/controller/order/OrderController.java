package com.huotu.shopo2o.web.controller.order;

import com.huotu.shopo2o.common.SysConstant;
import com.huotu.shopo2o.common.ienum.EnumHelper;
import com.huotu.shopo2o.common.utils.ApiResult;
import com.huotu.shopo2o.common.utils.Constant;
import com.huotu.shopo2o.common.utils.ExcelHelper;
import com.huotu.shopo2o.common.utils.ResultCodeEnum;
import com.huotu.shopo2o.common.utils.StringUtil;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.marketing.MallPintuan;
import com.huotu.shopo2o.service.entity.order.MallOrder;
import com.huotu.shopo2o.service.enums.ActEnum;
import com.huotu.shopo2o.service.enums.OrderEnum;
import com.huotu.shopo2o.service.jsonformat.OrderForDelivery;
import com.huotu.shopo2o.service.model.OrderDetailModel;
import com.huotu.shopo2o.service.searchable.OrderSearchCondition;
import com.huotu.shopo2o.service.service.marketing.MallPintuanService;
import com.huotu.shopo2o.service.service.order.MallDeliveryService;
import com.huotu.shopo2o.service.service.order.MallOrderService;
import com.huotu.shopo2o.web.config.security.annotations.LoginUser;
import com.huotu.shopo2o.web.service.StaticResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    private static final Log log = LogFactory.getLog(OrderController.class);
    @Autowired
    private MallOrderService orderService;
    @Autowired
    private StaticResourceService staticResourceService;
    @Autowired
    private MallDeliveryService mallDeliveryService;
    @Autowired
    private MallPintuanService mallPintuanService;


    @RequestMapping("/getOrderList")
    public String getOrderList(@LoginUser MallCustomer customer
            , @ModelAttribute("orderSearchCondition") OrderSearchCondition searchCondition
            , @RequestParam(required = false, defaultValue = "1") int pageIndex, Model model) {
        searchCondition.setStoreId(customer.getCustomerId());
        Pageable pageable = new PageRequest(pageIndex - 1, Constant.PAGE_SIZE);
        Page<MallOrder> orderPageList = orderService.findAll(pageable, searchCondition);
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

    /**
     * 导出订单
     *
     * @param searchCondition
     * @param txtBeginPage
     * @param txtEndPage
     * @param response
     */
    @RequestMapping("/exportExcel")
    @SuppressWarnings("Duplicates")
    public void exportExcel(@LoginUser MallCustomer customer
            ,OrderSearchCondition searchCondition, int txtBeginPage, int txtEndPage, HttpServletResponse response) {
        searchCondition.setStoreId(customer.getCustomerId());
        int pageSize = Constant.PAGE_SIZE * (txtEndPage - txtBeginPage + 1);
        int offset = (txtBeginPage - 1) * Constant.PAGE_SIZE;
        Pageable pageable = new PageRequest(offset, pageSize);
        Page<MallOrder> pageInfo = orderService.findAll(pageable, searchCondition);
        List<MallOrder> orderList = pageInfo.getContent();
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "order-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = orderService.createWorkBook(orderList);
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
        }
    }

    /**
     * 检查订单（拼团）是否可以发货
     *
     * @param orderId
     * @param orderSource
     * @return
     */
    @RequestMapping("/pintuanCheck")
    @ResponseBody
    public ApiResult pintuanCheck(String orderId, int orderSource) {
        OrderEnum.OrderSourceType orderSourceType = EnumHelper.getEnumType(OrderEnum.OrderSourceType.class, orderSource);
        if (orderSourceType == OrderEnum.OrderSourceType.PIN_TUAN) {
            MallPintuan pintuan = mallPintuanService.findByOrderId(orderId);
            if (pintuan != null && pintuan.getJoinLogs() != null && pintuan.getJoinLogs().size() > 0) {
                if (pintuan.getOrderPintuanStatusOption() != ActEnum.OrderPintuanStatusOption.GROUPED) {
                    //拼团未成功,无法发货
                    return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
                }
            }
        }
        //不是拼团或者已成功,可以发货
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
    }

    @RequestMapping("/showOrderDetail")
    public String showOrderDetail(@RequestParam("orderId") String orderId,
                                  @RequestParam(defaultValue = "1") int pageIndex, Model model) throws Exception {
        OrderDetailModel orderDetailModel = orderService.findOrderDetail(orderId);
        model.addAttribute("orderDetail", orderDetailModel);
        model.addAttribute("pageIndex", pageIndex);
        return "order/order_detail";
    }


    /**
     * 批量发货
     *
     * @param mallCustomer
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/batchDeliver", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult batchDeliver(
            @LoginUser MallCustomer mallCustomer,
            HttpServletRequest request
    ) throws IOException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("FileData");
        ApiResult apiResult;
        if (file == null) {
            apiResult = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        } else {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            List<List<ExcelHelper.CellDesc>> deliveryInfo = ExcelHelper.readWorkbook(workbook, SysConstant.ORDER_BATCH_DELIVER_SHEET_NAME, 1, 6, 0);
            List<OrderForDelivery> orderForDeliveries = new ArrayList<>();
            deliveryInfo.forEach(itemInfo -> {
                OrderForDelivery orderForDelivery = new OrderForDelivery();
                orderForDelivery.setOrderNo((String) itemInfo.get(0).getValue());
                orderForDelivery.setLogiName((String) itemInfo.get(1).getValue());
                orderForDelivery.setLogiNo((String) itemInfo.get(2).getValue());
                orderForDelivery.setLogiCode((String) itemInfo.get(3).getValue());
                String logiMoneyStr = (String) itemInfo.get(4).getValue();
                double logiMoney = 0;
                if (!StringUtils.isEmpty(logiMoneyStr)) logiMoney = Double.parseDouble(logiMoneyStr);
                orderForDelivery.setLogiMoney(logiMoney);
                orderForDelivery.setRemark((String) itemInfo.get(5).getValue());
                orderForDeliveries.add(orderForDelivery);
            });
            apiResult = mallDeliveryService.pushBatchDelivery(orderForDeliveries, mallCustomer.getCustomerId());
        }
        log.info("code:" + apiResult.getCode() + ";msg:" + apiResult.getMsg());
        return apiResult;
    }

}
