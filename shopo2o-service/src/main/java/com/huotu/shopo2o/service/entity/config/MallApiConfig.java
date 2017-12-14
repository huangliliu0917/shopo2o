package com.huotu.shopo2o.service.entity.config;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author helloztt
 */
@Entity
@Setter
@Getter
@Cacheable(value = false)
@Table(name = "Mall_ApiConfig")
public class MallApiConfig {

    @Id
    @Column(name = "Customer_Id")
    private Long customerId;

    @Column(name = "Kd_apiServerUrl")
    private String apiServerUrl;

    @Column(name = "Kd_dbId")
    private String dbId;

    @Column(name = "Kd_userName")
    private String userName;

    @Column(name = "Kd_password")
    private String password;

}
