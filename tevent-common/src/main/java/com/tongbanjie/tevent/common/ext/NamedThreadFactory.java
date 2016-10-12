package com.tongbanjie.tevent.common.ext;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicLong threadIndex = new AtomicLong(0);
    private final String threadNamePrefix;


    public NamedThreadFactory(final String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }


    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());

    }
}
