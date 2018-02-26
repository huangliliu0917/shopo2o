package com.huotu.shopo2o.service.repository.user;

import com.huotu.shopo2o.common.ienum.UserTypeEnum;
import com.huotu.shopo2o.service.entity.user.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel,Long> {
    List<UserLevel> findByCustomerIdAndTypeOrderByLevel(Long customerId, UserTypeEnum type);
}