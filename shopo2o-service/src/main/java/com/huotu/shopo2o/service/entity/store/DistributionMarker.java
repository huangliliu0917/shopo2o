package com.huotu.shopo2o.service.entity.store;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 配送区域点
 * Created by helloztt on 2017-09-07.
 */
@Entity
@Table(name = "ST_DistributionMarker")
@Getter
@Setter
public class DistributionMarker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    /**
     * 门店ID
     */
    @Column(name = "Store_Id")
    private Long storeId;

    /**
     * 序号
     */
    @Column(name = "Number")
    private Long number;

    /**
     * 坐标
     */
    @Embedded
    private LngLat lngLat;

}
