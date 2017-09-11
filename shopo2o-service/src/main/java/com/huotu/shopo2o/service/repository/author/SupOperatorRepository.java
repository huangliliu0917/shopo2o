package com.huotu.shopo2o.service.repository.author;

import com.huotu.shopo2o.service.entity.author.SupOperator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface SupOperatorRepository extends JpaRepository<SupOperator, Long> {
    List<SupOperator> findByCustomer_customerId(Long customerId);
    long countByUsername(String username);
}
