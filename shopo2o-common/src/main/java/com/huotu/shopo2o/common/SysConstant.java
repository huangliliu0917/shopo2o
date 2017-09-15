package com.huotu.shopo2o.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by helloztt on 2017-06-27.
 */
@Component("sysConstant")
public class SysConstant {
    public static String COOKIE_DOMAIN;
    public static String HUOBANMALL_RESOURCE_HOST;
    public static String HUOBANMALL_PUSH_URL;
    public static String SUPPLIER_KEY = "1232228433";
    public static final String ORDER_BATCH_DELIVER_SHEET_NAME = "OrderToDelivery";
    public static String HUOBANMALL_PUSH_APPID = "huotuacf89c9231848c9f49";
    public static String HUOBANMALL_PUSH_APP_SECRET = "0ad8abe244331aacf89c9231848c9f49";
    public static String DAY_SORT_NUM;
    public static String[] ORDER_EXPORT_HEADER = {
            "订单编号",
            "订单名称",
            "状态",
            "货品数量",
            "下单时间",
            "支付时间",
            "支付状态",
            "发货状态",
            "订单金额",
            "运费",
            "优惠金额",
            "收货人",
            "收货人身份证",
            "收货人手机",
            "收货人地址",
            "货品列表",
            "自定义字段"
    };

    @Autowired
    public SysConstant(Environment env){
        COOKIE_DOMAIN = env.getProperty("cookie.domain", ".pdmall.com");
        HUOBANMALL_RESOURCE_HOST = env.getProperty("huobanmall.resourceUrl", "http://res.pdmall.com");
        HUOBANMALL_PUSH_URL = env.getProperty("huobanmall.pushUrl", "http://mallapi.pdmall.com");
        DAY_SORT_NUM = env.getProperty("com.scrm.daySortNum","200");
    }
}
