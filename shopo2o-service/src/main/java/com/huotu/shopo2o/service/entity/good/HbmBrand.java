package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name="Mall_Brand")
@Cacheable(false)
@Getter
@Setter
public class HbmBrand {
    /**
     * 品牌主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Brand_Id")
    private Integer brandId;
    /**
     * 品牌名称
     */
    @Column(name = "Brand_Name")
    private String brandName;
    /**
     * 分销商编号
     */
    @Column(name = "Customer_Id")
    private int customerId;
    /**
     * 标准品牌ID
     */
    @Column(name = "Standard_Brand_Id")
    private String standardBrandId;
    /**
     * 排序
     */
    private int orderNum;

    @Column(name = "Disabled")
    private boolean isDisabled;
}
