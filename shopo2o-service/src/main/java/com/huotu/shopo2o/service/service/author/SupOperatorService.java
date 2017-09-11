package com.huotu.shopo2o.service.service.author;

import com.huotu.shopo2o.service.entity.author.SupOperator;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface SupOperatorService {
    List<SupOperator> findByCustomerId(Long customerId);

    SupOperator findById(Long id);

    long countByUsername(String username);

    SupOperator save(SupOperator supOperator);

    void delete(Long id);
}
