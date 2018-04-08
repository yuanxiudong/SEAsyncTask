package com.seagle.task;

/**
 * Progress task callback.
 * Use for task with progress update.
 * Created by seagle on 2018/4/3.
 *
 * @author yuanxiudong66@sina.com
 */

public interface SEProgressTaskCallback<V> extends SETaskCallback<V> {
    void onProgressUpdate(Object object);
}
