package com.huotu.shopo2o.service.entity.user;

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
 * Created by hxh on 2017-09-15.
 */
@Entity
@Table(name = "Hot_UserBaseInfo")
@Setter
@Getter
@Cacheable(value = false)
public class UserBaseInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UB_UserID")
    private Integer userId;
    @Column(name = "UB_UserLoginName")
    private String loginName;
    @Column(name = "UB_UserNickName")
    private String nickname;
    @Column(name = "UB_UserRealName")
    private String realName;
    @Column(name = "UB_UserMobile")
    private String mobile;
}
