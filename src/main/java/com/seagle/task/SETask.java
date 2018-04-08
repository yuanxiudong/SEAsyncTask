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
public class SETask<V> {
    private volatile static ExecutorService sDefaultExecutor;
    private final SECancelableTask<V> mTask;
    private volatile SETaskCallback<V> mCallback;
    private volatile FutureTask<V> mFutureTask;


    public SETask(SECancelableTask<V> task) {
        mTask = task;
    }

    public SETask() {
        mTask = null;
    }

    /**
     * Get task result.
     *
     * @return result
     * @throws ExecutionException   call method exception
     * @throws InterruptedException thread interrupted
     */
    public final V get() throws ExecutionException, InterruptedException {
        if (mFutureTask != null) {
            return mFutureTask.get();
        }
        return null;
    }

    /**
     * Cancel task.
     */
    public final void cancel() {
        if (mFutureTask != null) {
            mFutureTask.cancel(true);
        }
    }

    /**
     * Submit and execute task.
     *
     * @param callback callback
     * @return SETask
     */
    public final SETask<V> submit(SETaskCallback<V> callback) {
        synchronized (this) {
            if (mFutureTask == null) {
                SECancelableTask<V> task = mTask;
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

    /**
     * Submit and execute task.
     *
     * @param callback callback
     * @param executor thread pool
     * @return SETask
     */
    public final SETask<V> submit(ExecutorService executor, SETaskCallback<V> callback) {
        synchronized (this) {
            if (mFutureTask == null) {
                SECancelableTask<V> task = mTask;
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

    /**
     * Notify progress update.
     * For sub class call.
     */
    protected final void notifyProgressUpdate(Object object) {
        if (mCallback != null && mFutureTask != null && !mFutureTask.isCancelled() && mCallback instanceof SEProgressTaskCallback) {
            ((SEProgressTaskCallback) mCallback).onProgressUpdate(object);
        }
    }

    /**
     * Task run.
     * For sub class implementation.
     */
    protected V call() throws Exception {
        throw new UnsupportedOperationException("Task not implemented!");
    }

    /**
     * Task canceled.
     * Sub class can release resources.
     */
    protected void onCancel() {

    }

    /**
     * Return default executor.
     *
     * @return ExecutorService
     */
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
    private class InternalTask implements SECancelableTask<V> {

        @Override
        public V call() throws Exception {
            return SETask.this.call();
        }

        @Override
        public void cancel() {
            SETask.this.onCancel();
        }
    }

    /**
     * Http request session.
     * Cancelable request.
     */
    private class TaskSession extends FutureTask<V> {

        private final SECancelableTask<V> mTask;

        private TaskSession(SECancelableTask<V> task) {
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
