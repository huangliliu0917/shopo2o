package com.huotu.shopo2o.service.entity.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Delivery_item")
@Setter
@Getter
@Cacheable(value = false)
public class MallDeliveryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_Id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "Delivery_Id")
    private MallDelivery delivery;

    @Column(name = "Item_Type")
    private String type;

    @Column(name = "Product_Id")
    private Integer productId;

    @Column(name = "Product_Bn")
    private String productBn;

    @Column(name = "Product_Name")
    private String productName;

    @Column(name = "Number")
    private int num;

}
