package com.huotu.shopo2o.service.entity.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionMarker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    /**
     * 门店ID
     */
    @ManyToOne
    @JoinColumn(name = "Store_Id")
    @JsonIgnore
    private Store store;

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

    @Column(name = "Deletable")
    private boolean deletable = true;

}
