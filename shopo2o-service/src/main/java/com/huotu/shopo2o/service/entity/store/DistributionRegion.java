package com.huotu.shopo2o.service.entity.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huotu.shopo2o.service.entity.support.LngLatConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * 配送区域划分
 * Created by helloztt on 2017-09-07.
 */
@Entity
@Table(name= "ST_DistributionRegion")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistributionRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Store_Id")
    @JsonIgnore
    private Store store;

    @Column(name = "Name")
    private String name;

    /**
     * 点坐标
     */
    @Column(name = "MarkerNum")
    private String markerNum;

    /**
     * 显示颜色
     */
    @Column(name = "Color")
    private String color;


    @Column(name = "Distribution_Regions",columnDefinition = "varchar(MAX)")
    @Convert(converter = LngLatConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private List<LngLat> distributionRegions;
}
