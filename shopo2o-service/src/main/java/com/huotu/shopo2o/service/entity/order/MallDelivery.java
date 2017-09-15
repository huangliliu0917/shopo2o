package com.huotu.shopo2o.service.entity.order;

import com.alibaba.fastjson.annotation.JSONField;
import com.huotu.shopo2o.service.entity.store.Store;
import com.huotu.shopo2o.service.entity.user.UserBaseInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

/**
 * 发货表,退货表
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Delivery")
@Setter
@Getter
@Cacheable(value = false)
public class MallDelivery {
    /**
     * 模糊查询时前面不要%，不然索引不起效果，查询速度会很慢
     */
    @Id
    @Column(name = "Delivery_Id")
    private String deliveryId;

    @ManyToOne
    @JoinColumn(name = "Order_Id")
    @JSONField(deserialize = false)
    private MallOrder order;
    @ManyToOne
    @JoinColumn(name = "Member_Id", referencedColumnName = "UB_UserID")
    private UserBaseInfo userBaseInfo;
    @Column(name = "Type")
    private String type;
    //    @Transient
//    private String logisticsCode;//物流公司 编码
    @Column(name = "Logi_Name")
    private String logisticsName;//物流公司
    @Column(name = "Logi_No")
    private String logisticsNo;//物流单号
    @Column(name = "Money")
    private double freight;//物流费用
    @ManyToOne
    @JoinColumn(name="Store_Id")
    private Store store;

    @Column(name = "T_Begin")
    private Date createTime;//单据生成时间

    @Column(name = "Ship_Addr")
    private String shipAddr;
    @Column(name = "Ship_Name")
    private String shipName;
    @Column(name = "Ship_Mobile")
    private String shipMobile;
    @Column(name = "Ship_Zip")
    private String shipZip;
    @Column(name = "Ship_Tel")
    private String shipTel;
    @Column(name = "Memo")
    private String memo;

    @OneToMany(mappedBy = "delivery")
    private List<MallDeliveryItem> deliveryItems;
}
