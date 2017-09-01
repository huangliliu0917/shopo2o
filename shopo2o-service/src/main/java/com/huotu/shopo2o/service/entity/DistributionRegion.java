package com.huotu.shopo2o.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by helloztt on 2017-08-31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRegion {
    @JsonProperty("lan")
    private Double lan;
    @JsonProperty("lat")
    private Double lat;
}
