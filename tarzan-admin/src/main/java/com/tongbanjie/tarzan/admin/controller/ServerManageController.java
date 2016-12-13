package com.tongbanjie.tarzan.admin.controller;

import com.tongbanjie.tarzan.admin.common.Controllers;
import com.tongbanjie.tarzan.admin.common.Response;
import com.tongbanjie.tarzan.admin.service.ServerManageService;
import com.tongbanjie.tarzan.common.Result;
import com.tongbanjie.tarzan.common.util.ResultValidate;
import com.tongbanjie.tarzan.registry.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 〈服务端管理Controller〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/17
 */

@RequestMapping(Controllers.SERVER)
@Controller
public class ServerManageController extends BaseController{

    @Autowired
    private ServerManageService serverManageService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model){
        Response response = Response.newInstance();
        try {
            Result<List<ServerAddress>> result = serverManageService.getAllServers();
            ResultValidate.isTrue(result);
            response.putSuccess("成功", result.getData());
        } catch (Exception e) {
            LOGGER.error("",e);
            response.putFail("查询失败.", e);
        }
        model.addAllAttributes(response.toJSON());
        return "server/serverList";
    }

    @RequestMapping(value = "/ids", method = RequestMethod.GET)
    public String listId(Model model){
        Response response = Response.newInstance();
        try {
            Result<List<ServerAddress>> result = serverManageService.getServerIds();
            ResultValidate.isTrue(result);
            response.putSuccess("成功", result.getData());
        } catch (Exception e) {
            LOGGER.error("",e);
            response.putFail("查询失败.", e);
        }
        model.addAllAttributes(response.toJSON());
        return "server/serverIds";
    }

    /**
     * 删除ServerId
     * 谨慎操作
     * @param id
     * @return
     */
    @RequestMapping(value = "/id/{id}", method = RequestMethod.DELETE)
    public @ResponseBody Object deleteId(@PathVariable Integer id){
        Response response = Response.newInstance();
        try {
            Result<Void> result = serverManageService.deleteServerId(id);
            ResultValidate.isTrue(result);
            response.putSuccess("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败",e);
            response.putFail("删除失败，"+e.getMessage());
        }
        return response.toJSON();
    }


}
