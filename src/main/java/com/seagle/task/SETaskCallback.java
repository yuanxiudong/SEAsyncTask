package com.seagle.task;

/**
 * Async task callback
 * Created by seable on 2018/4/3.
 *
 * @author yuanxiudong66@sina.com
 */

public interface SETaskCallback<V> {

    /**
     * Task complete.
     *
     * @param result task result
     */
    void onComplete(V result);

    /**
     * Task failed.
     *
     * @param throwable task exception
     */
    void onFailure(Throwable throwable);
}
