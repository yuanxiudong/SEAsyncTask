package com.seagle.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

/**
 * Async task.
 * Created by seagle on 2018/4/3.
 *
 * @author yuanxiudong66@sina.com
 */

public class SEAsyncTask<V> {
    private volatile static ExecutorService sDefaultExecutor;
    private final CancelableTask<V> mTask;
    private volatile TaskCallback<V> mCallback;
    private volatile FutureTask<V> mFutureTask;


    public SEAsyncTask(CancelableTask<V> task) {
        mTask = task;
    }

    public SEAsyncTask() {
        mTask = null;
    }

    public final V get() throws ExecutionException, InterruptedException {
        if (mFutureTask != null) {
            return mFutureTask.get();
        }
        return null;
    }

    public final void cancel() {
        if (mFutureTask != null) {
            mFutureTask.cancel(true);
        }
        mFutureTask = null;
    }

    public final SEAsyncTask submit(TaskCallback<V> callback) {
        synchronized (this) {
            if (mFutureTask == null) {
                CancelableTask<V> task = mTask;
                if (task == null) {
                    task = new InternalTask();
                }
                mCallback = callback;
                mFutureTask = new TaskSession(task);
            } else {
                throw new IllegalStateException("Task has been submitted");
            }
        }
        getDefaultExecutor().submit(mFutureTask);
        return this;
    }

    public final SEAsyncTask submit(ExecutorService executor, TaskCallback<V> callback) {
        synchronized (this) {
            if (mFutureTask == null) {
                CancelableTask<V> task = mTask;
                if (task == null) {
                    task = new InternalTask();
                }
                mCallback = callback;
                mFutureTask = new TaskSession(task);
            } else {
                throw new IllegalStateException("Task has been submitted");
            }
        }
        if (executor == null) {
            getDefaultExecutor().submit(mFutureTask);
        } else {
            executor.submit(mFutureTask);
        }
        return this;
    }

    private void notifyComplete(V result) {
        if (mCallback != null) {
            mCallback.onComplete(result);
        }
    }

    private void notifyFailed(Throwable throwable) {
        if (mCallback != null) {
            mCallback.onFailure(throwable);
        }
    }

    protected final void notifyProgressUpdate() {
        if (mCallback != null && mFutureTask != null && !mFutureTask.isCancelled() && mCallback instanceof ProgressTaskCallback) {
            ((ProgressTaskCallback) mCallback).onProgressUpdate();
        }
    }

    protected V call() {
        throw new UnsupportedOperationException("Task not implemented!");
    }

    protected void onCancel() {

    }

    private static synchronized ExecutorService getDefaultExecutor() {
        if (sDefaultExecutor == null) {
            int coreSize = Runtime.getRuntime().availableProcessors();
            sDefaultExecutor = Executors.newFixedThreadPool(coreSize * 2 + 1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "Task_Thread");
                    thread.setDaemon(true);
                    return thread;
                }
            });
        }
        return sDefaultExecutor;
    }

    /**
     * Internal task
     */
    private class InternalTask implements CancelableTask<V> {

        @Override
        public V call() throws Exception {
            return SEAsyncTask.this.call();
        }

        @Override
        public void cancel() {
            SEAsyncTask.this.onCancel();
        }
    }

    /**
     * Http request session.
     * Cancelable request.
     */
    private class TaskSession extends FutureTask<V> {

        private final CancelableTask<V> mTask;

        private TaskSession(CancelableTask<V> task) {
            super(task);
            mTask = task;
        }

        public void done() {
            if (!isCancelled() && mCallback != null) {
                try {
                    V result = get();
                    if (!isCancelled() && mCallback != null) {
                        notifyComplete(result);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!isCancelled() && mCallback != null) {
                        notifyFailed(ex);
                    }
                }
            }
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            mTask.cancel();
            return super.cancel(true);
        }
    }
}
