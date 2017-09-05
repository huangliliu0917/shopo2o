package com.huotu.shopo2o.web.common;

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by hxh on 2017-09-04.
 */
public class UserNameAndPasswordNullException extends AccountStatusException {
    public UserNameAndPasswordNullException(String msg) {
        super(msg);
    }

    public UserNameAndPasswordNullException(String msg, Throwable t) {
        super(msg, t);
    }
}
