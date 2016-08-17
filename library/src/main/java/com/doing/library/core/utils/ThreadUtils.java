package com.doing.library.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Doing on 2016/7/25 0025.
 *
 * 以下各个ExecutorService的参数是根据 java.util.concurrent.Executors 源码来设置的。
 */

public final class ThreadUtils {
    private static final String TAG = ThreadUtils.class.getSimpleName();

    //线程池为无限大,复用线程，灵活回收空闲线程
    // name：线程名字
    public static ThreadPoolExecutor newCachedThreadPool(final String name) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new CounterThreadFactory(name),
                new LogDiscardPolicy());
    }

    //定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
    //name：线程名字， nThread：线程数
    public static ThreadPoolExecutor newFixedThreadPool(final String name, int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name),
                new LogDiscardPolicy());
    }

    //创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行
    //name：线程名字
    public static ThreadPoolExecutor newSingleThreadExecutor(final String name) {
        return newFixedThreadPool(name, 1);
    }

    //创建一个定长线程池，支持定时及周期性任务执行。
    /*
    scheduledThreadPool.schedule(new Runnable() {
        @Override
        public void run() {
            System.out.println("delay 3 seconds");
        }
        }, 3, TimeUnit.SECONDS);  //表示延迟3秒执行。
     */
    public static ScheduledExecutorService newScheduledExecutorService(int nThreads){
        return  Executors.newScheduledThreadPool(nThreads);
    }

    public static class LogDiscardPolicy implements RejectedExecutionHandler {

        public LogDiscardPolicy() {
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            LogUtils.v(TAG, "rejectedExecution() " + r + " is discard.");
        }
    }

    public static class CounterThreadFactory implements ThreadFactory {
        private int count;
        private String name;

        public CounterThreadFactory(String name) {
            this.name = (name == null ? "Android" : name);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + "-thread #" + count++);
            return thread;
        }
    }
}