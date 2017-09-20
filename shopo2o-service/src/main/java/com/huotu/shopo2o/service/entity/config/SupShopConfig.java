package com.huotu.shopo2o.service.entity.config;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by hxh on 2017-09-18.
 */
@Embeddable
@Getter
@Setter
public class SupShopConfig implements Serializable{
    /**
     * 是否开通店铺
     */
    @Column(name = "Shop_IsOpen")
    private boolean shopIsOpen;

    /**
     * 店铺名称
     */
    @Column(name = "Shop_Name")
    private String shopName;

    /**
     * 店铺LOGO
     * 资源保存在伙伴商城中
     */
    @Column(name = "Shop_Logo")
    private String shopLogo;

    /**
     * 平台方是否冻结
     */
    @Column(name="Disabled")
    private boolean isDisabled;
}
