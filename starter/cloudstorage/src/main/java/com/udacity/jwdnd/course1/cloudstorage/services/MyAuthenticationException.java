package com.udacity.jwdnd.course1.cloudstorage.services;

import org.springframework.security.core.AuthenticationException;

public class MyAuthenticationException extends AuthenticationException {
    public MyAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public MyAuthenticationException(String msg) {
        super(msg);
    }
}
