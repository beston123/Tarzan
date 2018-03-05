package com.tongbanjie.tarzan.common;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 〈轮形定时器〉<p>
 * 〈粒度：一秒对应一个slot〉
 *
 * @author zixiao
 * @date 18/2/5
 */
public abstract class WheelTimer<T> extends Timer {

    /**
     * 总槽数
     */
    private final int slotNum;

    /**
     * 槽队列
     */
    private final WheelSlot<T>[] slots;

    /**
     * 当前槽index
     */
    private int currentIndex = -1;

    public WheelTimer() {
        this(3600);
    }

    /**
     * 构造方法
     * @param slotNum 槽数
     */
    public WheelTimer(int slotNum) {
        super("WheelTimer-");
        this.slotNum = slotNum;
        this.slots = new WheelSlot[this.slotNum];

        for(int i=0; i< this.slotNum; i++){
            slots[i] = new WheelSlot<T>();
        }
    }

    public void start(){
        this.schedule(new TimerTask() {
            @Override
            public void run() {
                WheelSlot slot = nextSlot();
                Iterator<WheelTask<T>> iterator = slot.getTaskSet().iterator();
                while (iterator.hasNext()){
                    WheelTask<T> task = iterator.next();
                    if(task.getCycleNum() == 0){
                        //等待圈数=0时，开始执行，执行完后删除
                        doTask(task);
                        iterator.remove();
                    }else {
                        //等待圈数>0时，每被扫描到一次，等待圈数-1
                        task.reduceCycleNum();
                    }
                }
            }
        }, 1000L, 1000L);
    }

    /**
     * 执行任务
     * @param task
     */
    abstract protected void doTask(WheelTask<T> task);

    public WheelSlot nextSlot(){
        this.currentIndex = nextSlotNum();
        return slots[currentIndex];
    }

    private int nextSlotNum(){
        return currentIndex+1== slotNum ? 0 : currentIndex+1;
    }

    public WheelSlot getSlot(int slotIndex){
        if(slotIndex < 0 || slotIndex >= slotNum){
            throw new IllegalArgumentException("Slot index must be between 0 and "+(slotNum -1));
        }
        return slots[slotIndex];
    }

    /**
     * 添加延时任务
     * @param task     延时任务
     * @param delaySec 延时秒数
     */
    public void addTask(T task, int delaySec){
        if(delaySec <= 0){
            throw new IllegalArgumentException("Delay seconds must be larger then 0.");
        }
        Integer cycleNum = (delaySec-1)/ slotNum;
        int slotOffset = (delaySec-1)% slotNum;
        int slotIndex = currentIndex + 1 + slotOffset;
        if(slotIndex >= slotNum){
            slotIndex = slotIndex - slotNum;
        }
        WheelSlot slot = getSlot(slotIndex);
        slot.addTask(task, delaySec, cycleNum.shortValue());
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

}


