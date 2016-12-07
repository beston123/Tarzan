package com.tongbanjie.tevent.admin.common;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * 前台响应对象<p/>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2016/11/17
 */
public class Response implements Serializable {

    private static final long serialVersionUID = -8082455763324687927L;

    public static final int SUCCESS = 1;

    public static final int FAILURE = -1;

    public static final String ERROR_MSG = "errorMsg";

    private static final String DATA = "data";

    private static final String STATUS ="status";

    private static final String MESSAGE ="message";

    private static final String EXCEPTION ="exception";

    private JSONObject result;

    private Response(){
        result = new JSONObject();
    }

    public static Response newInstance(){
        return new Response();
    }

    public Response buildFail(String message){
        Response response = newInstance();
        response.putFail(message);
        return response;
    }

    public Response buildFail(String message, Exception exception){
        Response response = newInstance();
        response.putFail(message, exception);
        return response;
    }

    public Response buildSuccess(String message){
        Response response = newInstance();
        response.putSuccess(message);
        return response;
    }

    public Response buildSuccess(String message, Object data){
        Response response = newInstance();
        response.putSuccess(message, data);
        return response;
    }

    public void put(String key, Object value){
        result.put(key, value);
    }

    public void putFail(String message){
        result.put(STATUS, FAILURE);
        result.put(MESSAGE, message);
    }

    public void putFail(String message, Exception exception){
        putFail(message);
        if(exception != null){
            result.put(EXCEPTION, exception.getMessage());
        }
    }

    public void putSuccess(String message){
        result.put(STATUS, SUCCESS);
        result.put(MESSAGE, message);
    }

    public void putSuccess(String message, Object data){
        putSuccess(message);
        result.put(DATA, data);
    }

    public String toJSONString(){
        return result.toJSONString();
    }

    public JSONObject toJSON(){
        return result;
    }

}

