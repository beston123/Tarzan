package com.tongbanjie.tarzan.admin.controller;

import com.tongbanjie.tarzan.admin.service.RocketMQManageService;
import com.tongbanjie.tarzan.admin.common.Controllers;
import com.tongbanjie.tarzan.admin.common.Response;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * 〈RocketMQ消息管理Controller〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/21
 */
@RequestMapping(Controllers.MESSAGE_ROCKETMQ)
@Controller
public class RocketMQManageController extends BaseController {

    @Autowired
    private RocketMQManageService rocketMQManageService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(){
        return search();
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String search(){
        return Controllers.MESSAGE_ROCKETMQ+"/search";
    }

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Object queryById(@PathVariable Long id){
        Response response = Response.newInstance();
        try {
            Result<RocketMQMessage> result = rocketMQManageService.queryById(id);
            ResultValidate.isTrue(result);
            response.putSuccess("成功", result.getData());
        } catch (Exception e) {
            LOGGER.error("",e);
            response.putFail("查询失败.", e);
        }
        return response.toJSON();
    }

    @RequestMapping(value = "/key/{key}", method = RequestMethod.GET)
    public @ResponseBody Object queryByMessageKey(@PathVariable String key){
        Response response = Response.newInstance();
        try {
            Result<List<RocketMQMessage>> result = rocketMQManageService.queryByMessageKey(key);
            ResultValidate.isTrue(result);
            response.putSuccess("成功", result.getData());
        } catch (Exception e) {
            LOGGER.error("",e);
            response.putFail("查询失败.", e);
        }
        return response.toJSON();
    }
}
