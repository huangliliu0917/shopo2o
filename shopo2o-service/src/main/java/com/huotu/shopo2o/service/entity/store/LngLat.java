package com.huotu.shopo2o.service.entity.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
public class LngLat {

    /**
     * 经度
     */
    @Column(name = "Lng")
    @JsonProperty("lng")
    private Double lng;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    @JsonProperty("lat")
    private Double lat;
}
