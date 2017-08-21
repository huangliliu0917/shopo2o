package com.huotu.shopo2o.service.entity;

import com.huotu.shopo2o.service.enums.AuthorityEnum;
import com.huotu.shopo2o.service.enums.CustomerTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by helloztt on 2017-08-21.
 */
@Entity
@Table(name = "Swt_CustomerManage")
@Setter
@Getter
@Cacheable(false)
public class MallCustomer implements UserDetails , Serializable {

    private static final long serialVersionUID = 6646387732988707963L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SC_UserID")
    private Integer customerId;

    @Column(name = "SC_UserLoginName")
    private String username;

    @Column(name = "SC_UserNickName")
    private String nickName;

    @Column(name = "SC_UserLoginPassword")
    private String password;

    @Column(name = "SC_IndustryType")
    private Integer industryType;

    @Column(name = "SC_UserActivate")
    private Integer userActivate;

    @Column(name = "SC_RoleID")
    private Integer roleID;

    @Column(name = "SC_BelongManagerID")
    private Integer belongManagerID;

    @Column(name = "SC_Email")
    private String email;

    @Column(name = "SC_IsOld")
    private Integer isOld;

    @Column(name = "SC_DeveloperUrl")
    private String developerUrl;

    @Column(name = "SC_developerToken")
    private String developerToken;

    @Column(name = "SC_TYPE")
    private Integer scType;

    @Column(name = "SC_Score")
    private Double score;

    @Column(name = "SC_CityID")
    private Integer cityID;

    @Column(name = "SC_CustomerType")
    private CustomerTypeEnum customerType;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(referencedColumnName = "Shop_Id")
    private Shop shop;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(customerType == CustomerTypeEnum.SHOP){
            return Arrays.asList(
                    new SimpleGrantedAuthority(AuthorityEnum.MANAGER_ROOT.getCode())
            );
        }
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if(this.customerType == CustomerTypeEnum.SHOP){
            return shop != null && !shop.isDisabled();
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        if(this.customerType == CustomerTypeEnum.SHOP){
            return shop != null && !shop.isDeleted();
        }
        return false;
    }
}
