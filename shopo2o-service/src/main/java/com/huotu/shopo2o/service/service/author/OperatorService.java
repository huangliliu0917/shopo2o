package com.huotu.shopo2o.service.service.author;

import com.huotu.shopo2o.service.entity.author.Operator;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by hxh on 2017-09-11.
 */
public interface OperatorService extends UserDetailsService {
    List<Operator> findByCustomerId(Long customerId);

    Operator findById(Long id);

    long countByUsername(String username);

    Operator save(Operator operator);

    void delete(Long id);
}
