package com.huotu.shopo2o.service.entity.marketing;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Pintuan_JoinLog")
@Cacheable(false)
@Setter
@Getter
public class MallPintuanJoinLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JoinId")
    private Integer joinId;
    @ManyToOne
    @JoinColumn(name = "PomoterId")
    private MallPintuan pintuan;
    @Column(name = "MemberId")
    private Integer memberId;
    @Column(name = "OrderId")
    private String orderId;
    @Column(name = "PayStatus")
    private int payStatus;
}
