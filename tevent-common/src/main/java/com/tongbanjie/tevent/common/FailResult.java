package com.tongbanjie.tevent.common;

/**
 * 〈失败结果〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/18
 */
public enum FailResult {

    SYSTEM("SYSTEM_ERROR", "系统异常"),
    PARAMETER("PARAM_ERROR", "参数异常"),
    BUSINESS("BIZ_ERROR", "业务异常"),
    STORE("STORE_ERROR", "存储异常"),
    RPC("RPC_ERROR", "RPC异常"),
    TIMEOUT("TIMEOUT_RPC", "超时异常");


    private String code;

    private String name = null;

    FailResult(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static FailResult getByCode(String code) {
        for (FailResult tmp : values()) {
            if (tmp.code.equals(code)) {
                return tmp;
            }
        }
        return null;
    }

}
