package com.tongbanjie.tevent.store.model;

/**
 * 〈汇总状态〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/1
 */
public interface AggregateStatus {

    //0 初始 1成功 -1失败
    byte INITIAL = 0;
    byte SUCCESS = 1;
    byte FAILED = -1;

}
