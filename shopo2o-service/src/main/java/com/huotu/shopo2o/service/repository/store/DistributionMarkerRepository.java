package com.huotu.shopo2o.service.repository.store;

import com.huotu.shopo2o.service.entity.store.DistributionMarker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by helloztt on 2017-09-08.
 */
public interface DistributionMarkerRepository extends JpaRepository<DistributionMarker,Long> {
    DistributionMarker findByStore_IdAndNumber(Long storeId,Long number);
}
