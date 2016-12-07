package com.tongbanjie.tevent.common.exception;

/**
 * 〈超时异常〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class TimeoutException extends TEventException {

    private static final long serialVersionUID = 262632580841180603L;

    private long execMills;

    public TimeoutException(long execMills){
        this("Exec costs more than "+execMills+"ms.");
        this.execMills = execMills;
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public long getExecMills() {
        return execMills;
    }
}
