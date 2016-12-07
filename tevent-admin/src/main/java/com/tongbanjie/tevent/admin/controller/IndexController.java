package com.tongbanjie.tevent.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/index")
    public String toIndex(Model model) {
        return "index";
    }

    @RequestMapping(value = "/user/logout")
    public String logOut() {
        return "index";
    }

}
