package com.huotu.shopo2o.service.entity.config;

import com.huotu.shopo2o.service.entity.store.Store;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * Created by hxh on 2017-09-18.
 */
@Entity
@Table(name = "Sup_BasicConfig", uniqueConstraints = @UniqueConstraint(columnNames = {"supplierId"}))
@Cacheable(value = false)
@Getter
@Setter
public class SupBasicConfig implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Config_Id")
    private Integer id;

    /**
     * 供应商ID
     */
    @Column(name = "Supplier_Id")
    private Integer supplierId;
    @Column(name = "Store_Id")
    private Long storeId;

    /**
     * 联系人
     */
    @Column(name = "Contact")
    private String contact;

    /**
     * 联系人电话
     */
    @Column(name = "Mobile")
    private String mobile;

    /**
     * 邮箱
     */
    @Column(name = "Email")
    private String email;

    /**
     * 地址
     */
    @Column(name = "Addr")
    private String addr;

    /**
     * 省code
     *
     * @return
     */
    @Column(name = "ProvinceCode")
    private String provinceCode;

    /**
     * 市code
     *
     * @return
     */
    @Column(name = "CityCode")
    private String cityCode;

    /**
     * 区code
     *
     * @return
     */
    @Column(name = "DistrictCode")
    private String districtCode;

    /**
     * 省/市/区
     *
     * @return
     */
    @Column(name = "Address_Area")
    private String addressArea;
    /**
     * 经度
     */
    @Column(name = "Lan")
    private Double lan;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    private Double lat;

    /**
     * 客服电话
     */
    @Column(name = "Service_Tel")
    private String serviceTel;

    /**
     * 售后电话
     */
    @Column(name = "AfterSal_Tel")
    private String afterSalTel;

    /**
     * 售后QQ
     */
    @Column(name = "AfterSal_QQ")
    private String afterSalQQ;

    /**
     * 供应商系统名称
     */
    @Column(name = "Logo_Name")
    private String logoName;

    @Embedded
    private SupSettlementConfig settleConfig;

    @Embedded
    private SupShopConfig shopConfig;

    /**
     * 新增基本配置时初始化，新增前必须保证storeId唯一
     * @param storeId
     * @return
     */
    public static SupBasicConfig init(Long storeId){
        SupBasicConfig supBasicConfig = new SupBasicConfig();
        supBasicConfig.setStoreId(storeId);
        SupSettlementConfig supSettlementConfig= new SupSettlementConfig();
        supSettlementConfig.setSettlement(false);
        SupShopConfig shopConfig = new SupShopConfig();
        shopConfig.setShopIsOpen(false);
        shopConfig.setDisabled(false);
        supBasicConfig.setSettleConfig(supSettlementConfig);
        supBasicConfig.setShopConfig(shopConfig);
        return supBasicConfig;
    }
}
