package com.huotu.shopo2o.service.entity.good;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Mall_Freight_Template_Detail")
@Setter
@Getter
public class FreightTemplateDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Freight_Template_Id")
    private Long freightTemplateId;

    @Column(name = "Delivery_Type")
    private int deliveryType;

    @Column(name = "Is_Default")
    private boolean isDefault;

    @Column(name = "Area_Desc")
    @Lob
    private String areaDesc;

    @Column(name = "AreaId_Group")
    @Lob
    private String areaIdGroup;

    @Column(name = "First_Item")
    private int firstItem;

    @Column(name = "First_Freight")
    private double firstFreight;

    @Column(name = "Next_Item")
    private int nextItem;

    @Column(name = "Next_Freight")
    private double nextFreight;
}
