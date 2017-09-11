package com.huotu.shopo2o.service.jsonformat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商品自定义字段
 * Created by hxh on 2017-09-11.
 */
@Getter
@Setter
public class GoodCustomField {
    @JSONField(name = "goodsId")
    private int goodId;
    private int productId;
    @JSONField(name = "data")
    private List<FiledAndValue> filedAndValues;
}
