package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Entity
@Table(name="Mall_Goods_Type_Spec")
@IdClass(HbmGoodsTypeSpecPK.class)
@Cacheable(false)
@Getter
@Setter
public class HbmGoodsTypeSpec {
    /**
     * 类型主键
     */
    @Id
    @Column(name = "Type_Id")
    private int typeId;

    @Id
    @Column(name = "Spec_Id")
    private int specId;

    @Id
    @Column(name = "Spec_Value_Id")
    private int specValueId;

    @Column(name = "GTS_Customer_Id")
    private int customerId;

}

