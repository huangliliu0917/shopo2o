package com.huotu.shopo2o.service.repository.author;

import com.huotu.shopo2o.service.entity.author.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface OperatorRepository extends JpaRepository<Operator, Long> {

    List<Operator> findByCustomer_customerIdAndIsDeletedFalse(Long customerId);

    long countByUsername(String username);
}
