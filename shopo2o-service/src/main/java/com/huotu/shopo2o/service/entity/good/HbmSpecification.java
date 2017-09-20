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
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Mall_Specification")
@Cacheable(false)
@Getter
@Setter
public class HbmSpecification {
    /**
     * 类型主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Spec_Id")
    private Integer specId;

    /**
     * 规格名
     */
    @Column(name = "Spec_Name")
    private String specName;

    /**
     * 排序字段
     */
    @Column(name = "P_Order")
    private int order;

    /**
     * 是否禁用
     */
    private boolean disabled;

    /**
     * 布局方式
     * demo flat
     */
    @Column(name = "Spec_Show_Type")
    private String specShowType = "flat";

    /**
     * 显示方式(图片|文字)
     * demo   image text
     */
    @Column(name = "Spec_Type")
    private String specType = "text";

    @Column(name = "Spec_Memo")
    private String specMemo = "";

    /**
     * 标准规格Id
     */
    @Column(name = "Standard_Spec_Id")
    private String standardSpecId;

    /**
     * 商户Id
     */
    @Column(name = "Customer_Id")
    private int customerId = -1;

    @Column(name = "Supplier_Spec_Id")
    private int supplierSpecId = 0;

    @Column(name = "Supplier_Id")
    private int supplierId = 0;

    /**
     * 规格值List
     */
    @Transient
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "Spec_Id")
//    @OrderBy("order ASC")
    private List<HbmSpecValues> specValues;
    private Date lastmodify;
}
