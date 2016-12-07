package com.tongbanjie.tevent.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {
	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = { Exception.class })
    public void exceptionHandle(Exception e) {
        LOGGER.error("", e);
    }
}
