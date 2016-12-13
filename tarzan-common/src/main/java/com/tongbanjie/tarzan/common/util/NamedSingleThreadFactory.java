package com.tongbanjie.tarzan.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可命名的单线程工厂 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class NamedSingleThreadFactory implements ThreadFactory {

    private final String threadName;

    public NamedSingleThreadFactory(final String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadName);
    }
}
