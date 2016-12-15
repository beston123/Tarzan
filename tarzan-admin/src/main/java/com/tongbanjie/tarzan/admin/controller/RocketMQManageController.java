package com.tongbanjie.tarzan.admin.controller;

import com.tongbanjie.tarzan.admin.common.Controllers;
import com.tongbanjie.tarzan.admin.common.Response;
import com.tongbanjie.tarzan.admin.service.RocketMQManageService;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.message.RocketMQMessage;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
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
    public String toSearch(){
        return Controllers.MESSAGE_ROCKETMQ+"/query";
    }

    @RequestMapping(value = "query")
    public String query(Model model, Long tid, String key){
        Response response = Response.newInstance();
        try {
            if(tid != null){
                model.addAttribute("tid", tid);
                Result<RocketMQMessage> result = rocketMQManageService.queryById(tid);
                ResultValidate.isTrue(result);
                if(result.getData() != null){
                    List<RocketMQMessage> list = new ArrayList<RocketMQMessage>(1);
                    list.add(result.getData());
                    response.putSuccess("成功", list);
                }else{
                    response.putSuccess("成功", result.getData());
                }
            }else if(StringUtils.isNotEmpty(key)){
                model.addAttribute("key", key);
                Result<List<RocketMQMessage>> result = rocketMQManageService.queryByMessageKey(key);
                ResultValidate.isTrue(result);
                response.putSuccess("成功", result.getData());
            }else{
                response.putFail("参数错误", null);
            }
        } catch (Exception e) {
            LOGGER.error("",e);
            response.putFail("查询失败.", e);
        }
        model.addAllAttributes(response.toJSON());
        return toSearch();
    }
}
