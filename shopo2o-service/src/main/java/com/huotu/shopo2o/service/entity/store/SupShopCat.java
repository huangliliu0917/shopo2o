package com.huotu.shopo2o.service.entity.store;

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
@Table(name = "Sup_ShopCat")
@Cacheable(value = false)
@Getter
@Setter
public class SupShopCat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Store_Cat_Id")
    private Integer catId;
    /**
     * 分类名称
     */
    @Column(name = "Supplier_Cat_Name")
    private String catName;
    /**
     * 分类图标
     */
    @Column(name = "Supplier_Cat_Img")
    private String catImg;
    @Transient
    private String uri;
    /**
     * 父分类ID
     */
    @Column(name = "Supplier_Parent_Id")
    private Integer parentId;

    /**
     * 子类目
     */
    @Transient
    private List<SupShopCat> subShopCat;
    /**
     * 分类路径
     */
    @Column(name = "Supplier_Cat_Path")
    private String catPath;
    /**
     * 是否是叶子节点
     */
//    @Column(name = "Is_Leaf")
//    private boolean isLeaf;
    /**
     * 是否不可用
     */
    @Column(name = "Disabled")
    private boolean disabled = false;
    /**
     * 排序
     */
    @Column(name = "P_Order")
    private Integer order;

    @Column(name = "Store_Id")
    private Long storeId;
    /**
     * 分销商ID
     */
    @Column(name = "Customer_Id")
    private Integer customerId;
}
