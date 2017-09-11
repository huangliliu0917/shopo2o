package com.huotu.shopo2o.common.utils;

import java.math.BigDecimal;

/**
 * Created by hxh on 2017-09-11.
 */
public class DoubleUtil {
    /**
     * double数据格式化，保留2位小数，默认为4舍6入，如果是5看前一位，奇入偶不入
     *
     * @param value 需要格式化的数据
     * @return format value
     */
    public static double format(double value) {
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }


    /**
     * double数据格式化，默认为4舍6入，如果是5看前一位，奇入偶不入
     *
     * @param value 需要格式化的数据
     * @param scale 小数精度
     * @return format value
     */
    public static double format(double value, int scale) {
        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
}
