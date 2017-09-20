package com.huotu.shopo2o.service.entity.config;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by hxh on 2017-09-18.
 */
@Embeddable
@Getter
@Setter
public class SupSettlementConfig implements Serializable{
    /**
     * 是否开启结算功能
     */
    @Column(name = "IsSettlement")
    private boolean isSettlement = false;
    /**
     * 结算起始日期
     */
    @Column(name = "StartDate")
    private Date startDate;
}
