package com.tongbanjie.tevent.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class HTTPErrorController extends BaseController {

    @RequestMapping(value="/404")
    public String handle404() {
        return "/error/404";
    }

    @RequestMapping(value="/500")
    public String handle500() {
        return "/error/500";
    }
}