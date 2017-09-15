package com.huotu.shopo2o.service.jsonformat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by hxh on 2017-09-15.
 */
@Getter
@Setter
public class BatchDeliverResult {
    List<String> lstFailedMsg;
    private int successCount;
    private int failedCount;
}
