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
import java.util.List;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Mall_Goods_Type")
@Cacheable(false)
@Getter
@Setter
public class HbmGoodsType {
    /**
     * 类型主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Type_Id")
    private Integer typeId;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 是否是实体商品类型
     */
    @Column(name = "Is_Physical")
    private boolean isPhysical;
    /**
     * 标准商品类目ID
     */
    @Column(name = "Standard_Type_Id")
    private String standardTypeId;
    /**
     * 是否有子类目
     */
    @Column(name = "Is_Parent")
    private boolean isParent;
    /**
     * 父类目ID
     */
    @Column(name = "Parent_Standard_Type_Id")
    private String parentStandardTypeId;
    /**
     * 标准类目路径
     */
    private String path;
    /**
     * 是否有效
     */
    private boolean disabled;
    /**
     * 分销商ID
     */
    @Column(name = "Customer_Id")
    private int customerId;

    @Column(name = "T_Order")
    private int tOrder;

    @Transient
    private List<HbmBrand> brandList;

    @Transient
    private List<HbmSpecification> specList;

    /**
     * 类目名路径
     */
    @Transient
    private String pathStr;
}
