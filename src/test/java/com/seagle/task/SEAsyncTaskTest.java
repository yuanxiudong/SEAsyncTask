package com.seagle.task;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
                System.out.println("----------------");
            }

            @Override
            public Boolean call() throws Exception {
                TimeUnit.SECONDS.sleep(5);
                return true;
            }
        });
        Boolean result = asyncTask.submit(null).get();
        Assert.assertTrue(result);
    }

    @Test
    public void testTask1() throws ExecutionException, InterruptedException {
        SEAsyncTask<Boolean> asyncTask = new SEAsyncTask<Boolean>(){
            protected Boolean call() throws InterruptedException {
                TimeUnit.SECONDS.sleep(5);
                return true;
            }
        }.submit(null);
        Boolean result = asyncTask.get();
        Assert.assertTrue(result);
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