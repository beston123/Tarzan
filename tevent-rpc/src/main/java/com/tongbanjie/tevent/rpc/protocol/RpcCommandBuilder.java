package com.tongbanjie.tevent.rpc.protocol;

import com.tongbanjie.tevent.rpc.protocol.header.CustomHeader;

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
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdCode(cmdCode);
        cmd.setCmdType(RpcCommand.REQUEST_COMMAND);
        cmd.setCustomHeader(customHeader);
        return cmd;
    }

    /************************************ Response Command ************************************/

    public static RpcCommand buildResponse(int cmdCode, String remark, CustomHeader customHeader) {
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdCode(cmdCode);
        cmd.setRemark(remark);
        cmd.setCmdType(RpcCommand.RESPONSE_COMMAND);
        cmd.setCustomHeader(customHeader);
        return cmd;
    }

    public static RpcCommand buildResponse(int cmdCode, String remark){
        return buildResponse(cmdCode, remark, null);
    }


    public static RpcCommand buildResponse(CustomHeader customHeader){
        RpcCommand cmd = new RpcCommand();
        cmd.setCmdType(RpcCommand.RESPONSE_COMMAND);
        cmd.setCustomHeader(customHeader);
        return cmd;
    }

    public static RpcCommand buildResponse() {
        return buildResponse(null);
    }

    public static RpcCommand buildSuccess() {
        return buildResponse(ResponseCode.SUCCESS, null);
    }

    public static RpcCommand buildFail(String remark){
        return buildResponse(ResponseCode.SYSTEM_ERROR, remark);
    }


}
