package com.tongbanjie.tarzan.common;

import com.tongbanjie.tarzan.common.util.DateUtils;

import java.util.Date;

/**
 * 〈WheelTask〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/5
 */
public class WheelTask<T> {

    /**
     * 任务
     */
    private T task;

    /**
     * 预期执行时间
     */
    private Date expectExecTime;

    /**
     * 等待圈数
     * 0:立即执行, >0:每扫描一次 值-1
     */
    private short cycleNum;

    public WheelTask(T task, int delaySec, short cycleNum) {
        this.task = task;
        this.expectExecTime = org.apache.commons.lang3.time.DateUtils.addSeconds(new Date(), delaySec);
        this.cycleNum = cycleNum;
    }

    public T getTask() {
        return task;
    }

    public short getCycleNum() {
        return cycleNum;
    }

    public void reduceCycleNum(){
        this.cycleNum--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WheelTask)) return false;

        WheelTask<?> wheelTask = (WheelTask<?>) o;

        if (task != null ? !task.equals(wheelTask.task) : wheelTask.task != null) return false;
        return !(expectExecTime != null ? !expectExecTime.equals(wheelTask.expectExecTime) : wheelTask.expectExecTime != null);

    }

    @Override
    public int hashCode() {
        int result = task != null ? task.hashCode() : 0;
        result = 31 * result + (expectExecTime != null ? expectExecTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WheelTask{" +
                "task=" + task +
                ", expectExecTime=" + DateUtils.format(expectExecTime, "yyyyMMdd HH:mm:ss") +
                ", cycleNum=" + cycleNum +
                '}';
    }
}
