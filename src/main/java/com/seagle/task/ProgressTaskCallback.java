package com.seagle.task;

/**
 * Progress task callback.
 * Created by seagle on 2018/4/3.
 *
 * @author yuanxiudong66@sina.com
 */

public interface ProgressTaskCallback<V> extends TaskCallback<V> {
    void onProgressUpdate(Object object);
}
