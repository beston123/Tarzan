package com.tongbanjie.tevent.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈一句话功能简述〉<p>
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
