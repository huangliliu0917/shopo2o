package com.huotu.shopo2o.service.entity.author;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.Authority;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 操作员信息
 * Created by hxh on 2017-09-11.
 */
@Entity
@Table(name = "Mall_Operator")
@Setter
@Getter
public class Operator implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    @JsonIgnore
    private MallCustomer customer;

    @Column(name = "CustomerType")
    private CustomerTypeEnum customerType;

    @Column(name = "CreateTime")
    private Date createTime;
    /**
     * 真实姓名
     */
    @Column(name = "RealName")
    private String realName;
    /**
     * 角色名称
     */
    @Column(name = "RoleName")
    private String roleName;
    /**
     * 手机号
     */
    @Column(name = "Mobile")
    private String mobile;

    @Lob
    @Column(name = "AuthoritySet")
    private Set<Authority> authoritySet = new HashSet<>();

    @Transient
    private String authoritiesStr;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        authoritySet.forEach(authority -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getCode()));
        });
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return customer != null
                && this.customerType == CustomerTypeEnum.STORE && customer.getCustomerType() == this.customerType
                && customer.getStore() != null && !customer.getStore().isDisabled() && !this.isDisabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return customer != null
                && this.customerType == CustomerTypeEnum.STORE && customer.getCustomerType() == this.customerType
                && customer.getStore() != null && !customer.getStore().isDeleted() && !this.isDeleted();
    }
}
