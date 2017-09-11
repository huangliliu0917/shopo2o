package com.huotu.shopo2o.service.entity.order;

import com.huotu.shopo2o.service.enums.OrderEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * Created by hxh on 2017-09-07.
 */
@Entity
@Table(name = "Mall_Orders")
@Setter
@Getter
@Cacheable(value = false)
public class MallOrder {
    /**
     * 模糊查询时前面不要%，不然索引不起效果，查询速度会很慢
     */
    @Id
    @Column(name = "Order_Id")
    private String orderId;
    @Column(name = "Member_Id")
    private int memberId;
    @Column(name = "Confirm")
    private int confirm;
    @Column(name = "Status")
    private OrderEnum.OrderStatus orderStatus;
    @Column(name = "Pay_Status")
    private OrderEnum.PayStatus payStatus;
    @Column(name = "Ship_Status")
    private OrderEnum.ShipStatus shipStatus;
    @Column(name = "Createtime")
    private Date createTime;
    @Column(name = "Weight")
    private float weight;
    @Column(name = "Tostr")
    private String orderName;
    @Column(name = "Itemnum")
    private int itemNum;
    @Column(name = "Acttime")
    private Date lastUpdateTime;
    @Column(name = "Ship_Name")
    private String shipName;
    @Column(name = "Ship_Area")
    private String shipArea;
    //    private String province;
//    private String city;
//    private String district;
    @Column(name = "Ship_Addr")
    private String shipAddr;
    @Column(name = "Ship_Zip")
    private String shipZip;
    @Column(name = "Ship_Tel")
    private String shipTel;
    @Column(name = "Ship_Email")
    private String shipEmail;
    @Column(name = "Ship_Mobile")
    private String shipMobile;
    @Column(name = "Cost_Item")
    private double costItem;
    @Column(name = "Online_Amount")
    private double onlineAmount;
    /**
     * 运费
     */
    @Column(name = "Cost_Freight")
    private double costFreight;
    /**
     * 货币
     */
    @Column(name = "Currency")
    private String currency;
    /**
     * 订单金额
     */
    @Column(name = "Final_Amount")
    private double finalAmount;
    /**
     * 订单优惠金额
     */
    @Column(name = "Pmt_Amount")
    private double pmtAmount;
    /**
     * 订单附言（用户留言）
     */
    @Column(name = "Memo")
    private String memo;
    /**
     * 订单备注（商家留言）
     */
    @Column(name = "Mark_Text")
    private String remark;
    @Column(name = "Print_Status")
    private int printStatus;

    /**
     * 是否需要开票
     */
    @Column(name = "Is_Tax")
    private int isTax;
    /**
     * 开票公司抬头
     */
    @Column(name = "Tax_Company")
    private String taxCompany;

    /**
     * 支付方式名称
     */
    @Column(name = "Online_PayType")
    private OrderEnum.PaymentOptions paymentType;
    /**
     * 分销商id
     */
    @Column(name = "Customer_Id")
    private Integer customerId;
    /**
     * 供应商id
     */
    @Column(name = "StoreId_Id")
    private Integer storeId;
    // TODO: 2017-09-11 先不删（为了能显示订单）
    @Column(name = "Supplier_Id")
    private Integer supplierId;
    /**
     * 支付时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Paytime")
    private Date payTime;
    /**
     * 主订单号
     */
    @Column(name = "Union_Order_Id")
    private String unionOrderId;
    /**
     * 签收状态
     */
    @Column(name = "Rel_receiveStatus")
    private int receiveStatus;
    @Column(name = "BNList")
    private String bnList;

//    /**
//     * 结算状态
//     */
//    @Column(name = "SettleStatus")
//    private Integer settleStatus;
//    /**
//     * 预计结算时间
//     */
//    @Column(name = "PreSettleDate")
//    private Date preSettleDate;

    @Column(name = "SourceType")
    private OrderEnum.OrderSourceType orderSourceType;

//    @OneToOne(mappedBy = "order")
//    private MallPintuan pintuan;

    /**
     * 该订单是否能发货：为空或者为0时表示可发货，为1是表示不可发货
     */
    @Column(name = "Ship_Disabled")
    private Boolean shipDisabled;

    @OneToMany(mappedBy = "order")
    private List<MallOrderItem> orderItems;

    @Column(name = "IdentityCard")
    private String identityCard;

    //发货状态为 未发货，部分发货，部分退货
    //支付状态为 已支付，部分退款
    //可发货状态 为空或为false
    //为可发货
    public boolean deliveryable() {
        return (shipStatus == OrderEnum.ShipStatus.NOT_DELIVER || shipStatus == OrderEnum.ShipStatus.PARTY_DELIVER || shipStatus == OrderEnum.ShipStatus.PARTY_RETURN) &&
                (payStatus == OrderEnum.PayStatus.PAYED || payStatus == OrderEnum.PayStatus.PARTY_REFUND) &&
                (shipDisabled == null || !shipDisabled);
    }

    //发货状态为 已发货，部分发货，部分退货
    //支付状态为 售后退款中
    //为可退货
    public boolean returnable() {
        return (shipStatus == OrderEnum.ShipStatus.DELIVERED || shipStatus == OrderEnum.ShipStatus.PARTY_DELIVER || shipStatus == OrderEnum.ShipStatus.PARTY_RETURN) &&
                (payStatus == OrderEnum.PayStatus.REFUNDING);
    }
}
