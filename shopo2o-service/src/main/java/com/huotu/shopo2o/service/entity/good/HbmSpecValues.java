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
import javax.persistence.Transient;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name="Mall_Spec_Values")
@Cacheable(false)
@Getter
@Setter
public class HbmSpecValues {
    /**
     * 类型主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Spec_Value_Id")
    private Integer id;

    @Column(name = "Spec_Id")
    private int specId;

    /**
     * 规格值
     */
    @Column(name = "Spec_Value")
    private String value;

    /**
     * 规格值别名
     */
    private String alias;

    /**
     * 规格图片
     */
    @Column(name = "Spec_Image")
    private String image = "";

    /**
     * 排序字段
     */
    @Column(name = "P_Order")
    private int order;

    /**
     * 商户Id
     */
    @Column(name = "SV_Customer_Id")
    private int customerId;

    /**
     * 标准规格值Id
     */
    @Column(name = "Standard_Spec_Value_Id")
    private String standardSpecValueId;

    @Column(name = "Supplier_Id")
    private int supplierId = 0;

    @Column(name = "Supplier_Spec_Value_Id")
    private int supplierSpecValueId = 0 ;

    @Transient
    private String specValueImg;

    @Transient
    private String firstSpecValueImg;
}
