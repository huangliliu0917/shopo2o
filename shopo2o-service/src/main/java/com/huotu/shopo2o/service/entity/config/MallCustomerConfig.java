package com.huotu.shopo2o.service.entity.config;

/**
 * Created by luyuanyuan on 2017/10/11.
 */

import com.huotu.shopo2o.common.ienum.RebateCompatibleEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 商城商户基本配置
 * Created by helloztt on 2017-04-21.
 */
@Entity
@Setter
@Getter
@Cacheable(value = false)
@Table(name = "Mall_UserBaseConfig")
public class MallCustomerConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConfigID")
    private Long id;

    @Column(name = "CustomerID")
    private Integer customerId;
    /**
     * 返利计算模型
     */
    @Column(name = "RebateCompatible")
    private RebateCompatibleEnum rebateCompatibleEnum;

    /**
     * 供应商结算方式：0|Null->按成本;1->按平台提点
     */
    @Column(name = "SupplierSettleMode")
    private Integer supplierSettleMode;

    /**
     * 供应商设置返利方式：0->固定金额；1->百分比形式
     */
    @Column(name = "DisRebateCalculateMode")
    private Integer disRebateCalculateMode;

    /**
     * 返利设置形式:true:百分比;false:固定值
     * @return
     */
    public boolean rebateMode(){
        return disRebateCalculateMode != null && disRebateCalculateMode == 1;
    }

    /**
     * 结算方式：0|Null->成本价；false;1->平台提点：true
     * @return
     */
    public boolean settlementMode(){
        return supplierSettleMode != null && supplierSettleMode == 1;
    }

    /**
     * 是否允许设置返利：结算方式为平台提点并且返利形式为百分比
     * （固定值形式就不允许供应商设置了，因为涉及到经营者模式和八级返利的各层级的设置）
     * @return
     */
    public boolean isRebate(){
        return settlementMode() && rebateMode();
    }



}
