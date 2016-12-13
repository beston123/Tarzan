package com.tongbanjie.tarzan.common.util;


import com.tongbanjie.tarzan.common.exception.TimeoutException;

/**
 * 〈Timeout工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/2
 */
public class Timeout {

    /**
     * 开始计时点
     */
    private long start;

    /**
     * 超时毫秒数
     */
    private long timeOutMills;

    public Timeout(long timeOutMills){
        this.timeOutMills = timeOutMills;
        this.start = System.currentTimeMillis();
    }

    /**
     * 重置开始计时点
     */
    public void reset(){
        this.start = System.currentTimeMillis();
    }

    /**
     * 检查是否超时
     * @return
     */
    public boolean isTimeout(){
        return (System.currentTimeMillis() - start) >= timeOutMills;
    }

    /**
     * 检查是否超时
     * 超时则抛异常
     * @return
     * @throws TimeoutException
     */
    public boolean validate() throws TimeoutException {
        if(isTimeout()){
            throw new TimeoutException(timeOutMills);
        }
        return true;
    }

    /**
     * 耗时
     * @return
     */
    public long cost(){
        return (System.currentTimeMillis() - start);
    }

    /**
     * 剩余毫秒数
     * @return
     */
    public long remain(){
        long cost = cost();
        return (timeOutMills - cost) > 0 ? (timeOutMills - cost) : 0;
    }

}
