package com.huotu.shopo2o.service.entity.store;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by helloztt on 2017-09-07.
 */
@Getter
@Setter
@Embeddable
public class LngLat {

    /**
     * 经度
     */
    @Column(name = "Lng")
    private Double lng;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    private Double lat;
}
