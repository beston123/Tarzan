package com.tongbanjie.tarzan.admin.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tongbanjie.tarzan.admin.spring.CustomTimestampEditor;
import com.tongbanjie.tarzan.admin.spring.IntegerEditor;
import com.tongbanjie.tarzan.admin.spring.StringEscapeEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;


@ControllerAdvice
public class GlobalController extends BaseController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        datetimeFormat.setLenient(false);
        // 自动转换日期类型的字段格式
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(java.sql.Timestamp.class, new CustomTimestampEditor(datetimeFormat, true));
        // 防止XSS攻击
        binder.registerCustomEditor(String.class, new StringEscapeEditor(false, false, false));
        // Integer转换器
        binder.registerCustomEditor(Integer.class, new IntegerEditor());
    }
}
