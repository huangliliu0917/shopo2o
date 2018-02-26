package com.huotu.shopo2o.service.repository.good;

import com.huotu.shopo2o.service.entity.good.MallProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by luyuanyuan on 2017/10/11.
 */
@Repository
public interface MallProductRepository extends JpaRepository<MallProduct, Integer> {
}

