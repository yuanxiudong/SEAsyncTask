package com.seagle.task;

import org.junit.Test;

/**
 * Test unit.
 * Created by seagle on 2018/4/3.
 */
public class SEAsyncTaskTest {
    @Test
    public void get() throws Exception {
        SEAsyncTask<Boolean> asyncTask = new SEAsyncTask<>(new CancelableTask<Boolean>() {
            @Override
            public void cancel() {

            }

            @Override
            public Boolean call() throws Exception {
                return null;
            }
        });
        asyncTask.submit(new TaskCallback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Test
    public void cancel() throws Exception {
    }

    @Test
    public void execute() throws Exception {
    }

    @Test
    public void execute1() throws Exception {
    }
}