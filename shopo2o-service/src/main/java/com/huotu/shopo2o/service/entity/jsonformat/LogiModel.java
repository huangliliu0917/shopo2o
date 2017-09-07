package com.huotu.shopo2o.service.entity.jsonformat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 买家提交物流单
 * Created by hxh on 2017-09-07.
 */
@Setter
@Getter
public class LogiModel {
    @JSONField(name = "LogiCompany")
    private String logiCompany;
    @JSONField(name = "LogiCompanyChina")
    private String logiCompanyChina;
    @JSONField(name = "LogiNo")
    private String logiNo;
    @JSONField(name = "LogicMoblie")
    private String logiMobile;
    @JSONField(name = "LogiRemark")
    private String logiRemark;
    @JSONField(name = "LogiImg")
    private String logiImg;
    @JSONField(deserialize = false)
    private List<String> logiImgs;
}
