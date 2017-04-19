package com.cocolab.common.aiwebcollection.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QDThreadPool {
    private static Object lockobj = new Object();

    public static final int PRIORITY_HIGH = 0;//
    public static final int PRIORITY_MEDIUM = 1;//
    public static final int PRIORITY_LOW = 2;//

    public static final int PRIORITY_CHAPTERDOWN = 3;//
    public static final int PRIORITY_WRITELOG = 4;//

    private static ExecutorService mHighPool;
    private static ExecutorService mMediumPool;
    private static ExecutorService mLowPool;

    private static ExecutorService mChapterPool;
    private static ExecutorService mLogWritePool;

    private static ExecutorService getMediumInstance() {
        synchronized (lockobj) {
            if (mMediumPool == null || mMediumPool.isShutdown()) {
                mMediumPool = Executors.newFixedThreadPool(3);
            }
            return mMediumPool;
        }
    }

    private static ExecutorService getHighInstance() {
        synchronized (lockobj) {
            if (mHighPool == null || mHighPool.isShutdown()) {
                mHighPool = Executors.newCachedThreadPool();
            }
            return mHighPool;
        }
    }

    private static ExecutorService getLowInstance() {
        synchronized (lockobj) {
            if (mLowPool == null || mLowPool.isShutdown()) {
                mLowPool = Executors.newSingleThreadExecutor();
            }
            return mLowPool;
        }
    }

    private static ExecutorService getDownLoadInstance() {
        synchronized (lockobj) {
            if (mChapterPool == null || mChapterPool.isShutdown()) {
                mChapterPool = Executors.newFixedThreadPool(3);
            }
            return mChapterPool;
        }
    }

    private static ExecutorService getLogWriteInstance() {
        synchronized (lockobj) {
            if (mLogWritePool == null || mLogWritePool.isShutdown()) {
                mLogWritePool = Executors.newFixedThreadPool(3);
            }
            return mLogWritePool;
        }
    }

    public static ExecutorService getInstance(int priority) {
        if (priority == PRIORITY_HIGH) {
            return getHighInstance();
        } else if (priority == PRIORITY_MEDIUM) {
            return getMediumInstance();
        } else if (priority == PRIORITY_CHAPTERDOWN) {
            return getDownLoadInstance();
        } else if (priority == PRIORITY_WRITELOG) {
            return getLogWriteInstance();
        } else {
            return getLowInstance();
        }
    }

//	public static void submit(Runnable runnable, int level) {
//		switch (level) {
//			case PRIORITY_HIGH :
//				getHighInstance().submit(runnable);
//				break;
//			case PRIORITY_MEDIUM :
//				getMediumInstance().submit(runnable);
//				break;
//			case PRIORITY_LOW :
//				getLowInstance().submit(runnable);
//				break;
//		}
//	}

    public static void shutdown() {
        if (mHighPool != null && !mHighPool.isShutdown()) {
            mHighPool.shutdown();
        }
        if (mMediumPool != null && !mMediumPool.isShutdown()) {
            mMediumPool.shutdown();
        }
        if (mLowPool != null && !mLowPool.isShutdown()) {
            mLowPool.shutdown();
        }
    }
}
