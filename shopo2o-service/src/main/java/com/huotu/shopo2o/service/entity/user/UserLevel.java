package com.huotu.shopo2o.service.entity.user;

import com.huotu.shopo2o.common.ienum.UserTypeEnum;
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
 * Created by luyuanyuan on 2017/10/11.
 */
@Entity
@Setter
@Getter
@Cacheable(value = false)
@Table(name = "Mall_UserLevel")
public class UserLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UL_ID")
    private Long id;

    @Column(name = "UL_CustomerID")
    private Integer customerId;
    /**
     * 等级
     */
    @Column(name = "UL_Level")
    private int level;
    /**
     * 等级名称
     */
    @Column(name = "UL_LevelName", length = 50)
    private String levelName;
    /**
     * 等级类型
     */
    @Column(name = "UL_Type")
    private UserTypeEnum type;
    /**
     * 对应返利积分
     */
    @Column(name = "UL_Integral")
    private int integral;
}
