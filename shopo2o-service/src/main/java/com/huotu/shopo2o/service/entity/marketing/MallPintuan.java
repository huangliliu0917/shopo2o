package com.huotu.shopo2o.service.entity.marketing;

import com.huotu.shopo2o.service.enums.ActEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Pintuan_Promoter")
@Getter
@Setter
@Cacheable(false)
public class MallPintuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromoterId")
    private Integer id;
    @Column(name = "MemberId")
    private int memberId;
    @Column(name = "OrderId")
    private String orderId;
    @Column(name = "Status")
    private ActEnum.OrderPintuanStatusOption orderPintuanStatusOption;
    @Column(name = "CustomerId")
    private int customerId;
    @OneToMany(mappedBy = "pintuan")
    private List<MallPintuanJoinLog> joinLogs;
}
