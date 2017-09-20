package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 运费模板实体
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Mall_Freight_Template")
@Setter
@Getter
public class FreightTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;
    /**
     * 模板名称
     */
    @Column(name = "Name")
    private String name;
    /**
     * 商户ID
     */
    @Column(name = "Customer_Id", updatable = false)
    private int customerId;
    /**
     * 商户类型
     * 0:伙伴商城
     * 1:供应商
     * 2:代理商
     */
    @Column(name = "Template_Type", updatable = false)
    private int freightTemplateType;

    /**
     * 计价方式
     * 0：按件计价
     * 1：按重计价
     */
    @Column(name = "Valuation_Way")
    private int valuationWay;

    @Transient
    private List<FreightTemplateDetail> freightTemplateDetailList;
}
