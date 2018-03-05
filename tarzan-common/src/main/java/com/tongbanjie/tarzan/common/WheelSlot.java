package com.tongbanjie.tarzan.common;

import com.tongbanjie.tarzan.common.util.ConcurrentHashSet;

import java.util.Set;

/**
 * 〈WheelSlot〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/5
 */
public class WheelSlot<T> {

    /**
     * 任务集合
     */
    private final Set<WheelTask<T>> taskSet;

    public WheelSlot() {
        this.taskSet = new ConcurrentHashSet<WheelTask<T>>();
    }

    public Set<WheelTask<T>> getTaskSet() {
        return this.taskSet;
    }

    public void addTask(T task, int delaySec, short cycleNum){
        this.taskSet.add(new WheelTask<T>(task, delaySec, cycleNum));
    }
}

