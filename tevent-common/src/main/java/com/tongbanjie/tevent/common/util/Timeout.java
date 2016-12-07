package com.tongbanjie.tevent.common.util;


import com.tongbanjie.tevent.common.exception.TimeoutException;

/**
 * 〈TimeOut工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/2
 */
public class Timeout {

    private long start;

    private long timeOutMills;

    public Timeout(long timeOutMills){
        this.timeOutMills = timeOutMills;
        this.start();
    }

    public void start(){
        this.start = System.currentTimeMillis();
    }

    public boolean isTimeout(){
        return (System.currentTimeMillis() - start) >= timeOutMills;
    }

    public boolean validate() throws TimeoutException {
        if(isTimeout()){
            throw new TimeoutException(timeOutMills);
        }
        return true;
    }

    public long cost(){
        return (System.currentTimeMillis() - start);
    }

}
