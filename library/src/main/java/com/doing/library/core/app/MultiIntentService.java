package com.doing.library.core.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Doing on 2016/7/25 0025.
 */

/**
 * 类似于IntentService，但是多个异步任务可以并行执行：
 * 每次startService（Intent）即添加一个线程任务到线程池，子线程会调用onHandleIntent(final Intent intent, final String tag)处理传递进来的intent
 * Service每隔300秒自动检查，如果活跃任务目为0则自动结束；自动结束时间可设置，是否启用自动结束功能可设置
 * User: mcxiaoke
 * Date: 14-4-22 14-05-22
 * Time: 14:04
 */

public abstract class MultiIntentService extends Service {

    private static final String BASE_TAG = MultiIntentService.class.getSimpleName(); //Log tag

    // 默认空闲5分钟后自动stopSelf()
    public static final long AUTO_CLOSE_DEFAULT_TIME = 300 * 1000L; //300秒
    private long mAutoCloseTime;
    private boolean mAutoCloseEnable;

    private ExecutorService mExecutor;  //线程池
    private volatile Map<String, Future<?>> mFutures;  //用于可取消任务
    private Handler mHandler;  //创建MultiIntentService的线程的handler

    private volatile AtomicInteger mRetainCount;  //剩余线程数（原子计数）
    private final Object mLock = new Object(); //synchronized锁代码块使用

    //执行自动stopSelf()
    private final Runnable mAutoCloseRunnable = new Runnable() {
        @Override
        public void run() {
            autoClose(); //当发现剩余线程数mRetainCount为0时，stopSelf()关闭Service
        }
    };

    //不同Intent设置不同Tag
    private static final String SEPARATOR = "::";
    private static volatile long sSequence = 0L;  //Intent序号数（区分不同Intent）

    public MultiIntentService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(BASE_TAG, "onCreate()");

        mRetainCount = new AtomicInteger(0);
        mFutures = new ConcurrentHashMap<String, Future<?>>();
        mAutoCloseEnable = true;
        mAutoCloseTime = AUTO_CLOSE_DEFAULT_TIME;
        ensureHandler();  //创建Handler
        ensureExecutor(); //创建线程池
        checkAutoClose();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        //每次startService都把Intent放到线程池执行任务
        //可以在onHandleIntent(intent, tag)中获取Intent（根据不同Intent或Tag处理）
        if (intent != null) {
            dispatchIntent(intent);  //把Intent放到线程池执行任务
        }
        return START_NOT_STICKY;  //Service被kill后不重新创建
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(BASE_TAG, "onDestroy() mRetainCount=" + mRetainCount.get());
        Log.v(BASE_TAG, "onDestroy() mFutures.size()=" + mFutures.size());
        cancelAutoClose();
        destroyHandler();
        destroyExecutor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void ensureHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    private void destroyHandler() {
        synchronized (mLock) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }


    private ExecutorService ensureExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = Executors.newCachedThreadPool();
        }
        return mExecutor;
    }

    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
    }


    private void checkAutoClose() {
        if (mAutoCloseEnable) {
            scheduleAutoClose();
        } else {
            cancelAutoClose();
        }
    }

    //执行在mAutoCloseTime后，检查剩余线程数mRetainCount为0时，stopSelf()关闭Service
    private void scheduleAutoClose() {
        if (mAutoCloseTime > 0) {
            Log.v(BASE_TAG, "scheduleAutoClose()");
            if (mHandler != null) {
                mHandler.postDelayed(mAutoCloseRunnable, mAutoCloseTime);
            }
        }
    }

    private void cancelAutoClose() {
        Log.v(BASE_TAG, "cancelAutoClose()");
        if (mHandler != null) {
            //Remove any pending posts of Runnable r that are in the message queue.
            mHandler.removeCallbacks(mAutoCloseRunnable);
        }
    }

    protected boolean isIdle() {
        return mRetainCount.get() <= 0;
    }

    private void autoClose() {
        Log.v(BASE_TAG, "autoClose() mRetainCount=" + mRetainCount.get());
        Log.v(BASE_TAG, "autoClose() mFutures.size()=" + mFutures.size());
        if (isIdle()) {
            stopSelf();
        }
    }


    private void dispatchIntent(final Intent intent) {
        final String tag = buildTag(intent);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.v(BASE_TAG, "dispatchIntent thread=" + Thread.currentThread());
                Log.v(BASE_TAG, "dispatchIntent start tag=" + tag);
                onHandleIntent(intent, tag);
                Log.v(BASE_TAG, "dispatchIntent end tag=" + tag);
                release(tag);
            }
        };
        Future<?> future = submit(runnable);
        retain(tag, future);
    }

    //为每个Intent创建不同Tag
    private String buildTag(final Intent intent) {
        final long hashCode = System.identityHashCode(intent);
        final long sequence = incSequence();
        final long timestamp = SystemClock.elapsedRealtime();
        StringBuilder builder = new StringBuilder();
        builder.append(hashCode).append(SEPARATOR);
        builder.append(timestamp).append(SEPARATOR);
        builder.append(sequence);
        return builder.toString();
    }

    static long incSequence() {
        return ++sSequence;
    }

    //提交给线程池执行
    private Future<?> submit(Runnable runnable) {
        ensureExecutor();
        return mExecutor.submit(runnable);
    }

    //添加线程任务的统计
    protected void retain(final String tag, final Future<?> future) {
        Log.v(BASE_TAG, "retain() tag=" + tag);
        mRetainCount.incrementAndGet();
        mFutures.put(tag, future);
    }

    //删除线程任务的统计
    protected void release(final String tag) {
        Log.v(BASE_TAG, "release() tag=" + tag);
        mRetainCount.decrementAndGet();
        synchronized (mLock) {
            mFutures.remove(tag);
        }
        checkAutoClose();   //检查是否需要自动stopSelf()
    }

    /***************************************子类调用********************************************************/

    protected void setAutoCloseEnable(boolean enable) {
        mAutoCloseEnable = enable;
        checkAutoClose();
    }

    protected void setAutoCloseTime(long milliseconds) {
        mAutoCloseTime = milliseconds;
        checkAutoClose();
    }

    protected final void cancel(final String tag) {
        Future<?> future;
        synchronized (mLock) {
            future = mFutures.get(tag);
        }
        if (future != null) {
            future.cancel(true);
            release(tag);
        }
    }


    /**
     * 此方法在非UI线程执行
     *
     * @param intent Intent
     * @param tag    TAG，可以用于取消任务：cancel（tag）
     */
    //这里线程池里每个线程都调用同一个onHandleIntent，所以实现onHandleIntent时要根据intent、tag 加以区分具体操作
    protected abstract void onHandleIntent(final Intent intent, final String tag);

}
