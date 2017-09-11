package com.huotu.shopo2o.service.entity.author;

import com.huotu.shopo2o.service.entity.MallCustomer;
import com.huotu.shopo2o.service.enums.Authority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Table(name = "Sup_Operator")
@Setter
@Getter
public class SupOperator implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name = "customerId")
    private MallCustomer customer;
    private Date createTime;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 手机号
     */
    private String mobile;

    @Lob
    private Set<Authority> authoritySet = new HashSet<>();

    @Transient
    private String authoritiesStr;

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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
