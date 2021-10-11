package org.github.boot.threads.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2021/10/11 22:33
 * veegn
 *
 * @author veegn.me@gmail.com
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger threadSeq = new AtomicInteger();

    private final String threadNamePrefix;

    private final ThreadGroup threadGroup;

    private static final String THREAD_NAME_FORMAT = "%s-%d";

    public NamedThreadFactory(String prefix) {
        this.threadNamePrefix = prefix;
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            threadGroup = Thread.currentThread().getThreadGroup();
        } else {
            threadGroup = securityManager.getThreadGroup();
        }

    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = String.format(THREAD_NAME_FORMAT, threadNamePrefix, threadSeq.incrementAndGet());
        Thread thread = new Thread(threadGroup, r, threadName);
        thread.setDaemon(false);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
