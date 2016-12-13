package com.tongbanjie.tarzan.rpc.protocol;

import com.tongbanjie.tarzan.rpc.protocol.header.CustomHeader;

/**
 * RpcCommand构造器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class RpcCommandBuilder {

    /************************************ Request Command ************************************/

    public static RpcCommand buildRequest(int cmdCode, CustomHeader customHeader) {
        return buildRequest(cmdCode, customHeader, null/* remark */);
    }

    public static RpcCommand buildRequest(int cmdCode, CustomHeader customHeader, String remark) {
        return buildRequest(cmdCode, customHeader, null/* serializeType */, null/* body */, remark);
    }

    public static RpcCommand buildRequest(int cmdCode, CustomHeader customHeader, Object body) {
        return buildRequest(cmdCode, customHeader, null/* serializeType */, body);
    }

    public static RpcCommand buildRequest(int cmdCode, CustomHeader customHeader, SerializeType serializeType, Object body) {
        return buildRequest(cmdCode, customHeader, serializeType, body, null/* remark */);
    }

    public static RpcCommand buildRequest(int cmdCode, CustomHeader customHeader, SerializeType serializeType, Object body, String remark) {
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdCode(cmdCode);
        cmd.setCmdType(RpcCommand.REQUEST_COMMAND);
        cmd.setCustomHeader(customHeader);
        if(serializeType != null){
            cmd.setSerializeType(serializeType);
        }
        if(body != null){
            cmd.setBody(body);
        }
        cmd.setRemark(remark);
        return cmd;
    }

    /************************************ Response Command ************************************/

    public static RpcCommand buildResponse() {
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdType(RpcCommand.RESPONSE_COMMAND);
        return cmd;
    }

    public static RpcCommand buildResponse(int cmdCode, String remark, CustomHeader customHeader) {
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdCode(cmdCode);
        cmd.setRemark(remark);
        cmd.setCmdType(RpcCommand.RESPONSE_COMMAND);
        cmd.setCustomHeader(customHeader);
        return cmd;
    }

    public static RpcCommand buildResponse(int cmdCode, String remark){
        return buildResponse(cmdCode, remark, null/* customHeader */);
    }

    public static RpcCommand buildSuccess(Object body) {
        RpcCommand cmd = buildResponse();
        cmd.setCmdCode(ResponseCode.SUCCESS);
        if(body != null){
            cmd.setBody(body);
        }
        return cmd;
    }

    public static RpcCommand buildSuccess() {
        return buildSuccess(null/* customHeader */);
    }

    public static RpcCommand buildSuccess(CustomHeader customHeader) {
        return buildResponse(ResponseCode.SUCCESS, "SUCCESS", customHeader);
    }

    public static RpcCommand buildFail(String remark){
        return buildResponse(ResponseCode.SYSTEM_ERROR, remark);
    }


}
