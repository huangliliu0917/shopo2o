package com.huotu.shopo2o.web.common;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by hxh on 2017-09-12.
 */
public class VerifyCodeErrorException extends AccountStatusException {
    public VerifyCodeErrorException(String msg) {
        super(msg);
    }

    public VerifyCodeErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}
