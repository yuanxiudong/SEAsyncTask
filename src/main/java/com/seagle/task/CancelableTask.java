package com.seagle.task;


import java.util.concurrent.Callable;

/**
 * Callable with cancel method.
 * Created by seagle on 2018/4/3.
 *
 * @author yuanxiudong66@sina.com
 */

public interface CancelableTask<V> extends Callable<V> {
    void cancel();
}
