package com.huotu.shopo2o.service.entity.store;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.entity.support.LngLatConverter;
import com.huotu.shopo2o.service.entity.support.LngLatListConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * 线下门店。非代理商门店
 * Created by helloztt on 2017-08-21.
 */
@Entity
@Table(name = "ST_Store")
@Getter
@Setter
@Cacheable(false)
public class  Store {

    @Id
    @Column(name = "Store_Id")
    private Long id;

    /**
     * 平台方
     */
    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    private MallCustomer customer;

    /**
     * 名称
     */
    @Column(name = "Name",length = 64)
    private String name;

    /**
     * 区号
     */
    @Column(name = "Area_Code")
    private String areaCode;

    /**
     * 电话号码
     */
    @Column(name = "Telephone")
    private String telephone;

    /**
     * 省code
     */
    @Column(name = "Province_Code")
    private String provinceCode;

    /**
     * 市code
     */
    @Column(name = "City_Code")
    private String cityCode;

    /**
     * 区code
     */
    @Column(name = "District_Code")
    private String districtCode;

    /**
     * 地址
     *
     * @return
     */
    @Column(name = "Address",length = 256)
    private String address;

    /**
     * 坐标
     */
    @Embedded
    private LngLat lngLat;

    /**
     * 营业起始时间
     */
    @Column(name = "Open_Time",columnDefinition = "time")
    private LocalTime openTime;

    /**
     * 营业结束时间
     */
    @Column(name = "Close_Time",columnDefinition = "time")
    private LocalTime closeTime;

    /**
     * 截单时间
      */
    @Column(name = "Deadline_Time",columnDefinition = "time")
    private LocalTime deadlineTime;

    /**
     * 图标
     */
    @Column(name = "Logo")
    private String logo;

    @Transient
    private String mallLogoUri;

    /**
     * ERP门店ID
     */
    @Column(name = "Erp_Id")
    private String erpId;

    /**
     * 配送范围坐标
     */
    @Column(name = "Distribution_Regions",columnDefinition = "varchar(MAX)")
    @Convert(converter = LngLatConverter.class)
    @SuppressWarnings("JpaAttributeTypeInspection")
    private List<LngLat> distributionRegions;

    /**
     * 配送区域点坐标
     */
    @OneToMany(mappedBy = "store")
    private List<DistributionMarker> distributionMarkers;


    /**
     * 配送区域
     */
    @OneToMany(mappedBy = "store")
    private List<DistributionRegion> distributionDivisionRegions;

    @Convert(converter = LngLatListConverter.class)
    @Column(name = "Region_ListStr",columnDefinition = "varchar(MAX)")
    @SuppressWarnings("JpaAttributeTypeInspection")
    private List<LngLat[]> divisionList;
    /**
     * 配送费
     */
    @Column(name = "Delivery_Cost",scale = 2, precision = 12)
    private BigDecimal deliveryCost;

    /**
     * 起送金额
     */
    @Column(name = "Min_Cost",scale = 2,precision = 12)
    private BigDecimal minCost;

    /**
     * 免邮金额
     */
    @Column(name = "Free_Cost",scale = 2,precision = 12)
    private BigDecimal freeCost;

    /**
     * 是否冻结
     */
    @Column(name = "Disabled")
    private boolean isDisabled;

    /**
     * 是否删除
     */
    @Column(name = "Deleted")
    private boolean isDeleted;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createTime;


}
