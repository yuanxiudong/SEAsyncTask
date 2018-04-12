package com.seagle.task;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test unit.
 * Created by seagle on 2018/4/3.
 */
public class SETaskTest {
    @Test
    public void doSyncCancelableTask() throws Exception {
        SETask<Boolean> asyncTask = new SETask<>(new SECancelableTask<Boolean>() {
            @Override
            public void cancel() {
                System.out.println("Task has been canceled!");
            }

            @Override
            public Boolean call() throws Exception {
                //here do some concurrent and time-consuming user task
                TimeUnit.SECONDS.sleep(5);
                return true;
            }
        });
        Boolean result = asyncTask.submit(null).get();
        Assert.assertTrue(result);
    }

    @Test
    public void doSyncExtendTask() throws ExecutionException, InterruptedException {
        SETask<Boolean> asyncTask = new SETask<Boolean>() {
            protected Boolean call() throws InterruptedException {
                //here do some concurrent and time-consuming user task
                TimeUnit.SECONDS.sleep(5);
                return true;
            }
        }.submit(null);
        Boolean result = asyncTask.get();
        Assert.assertTrue(result);
    }

    @Test
    public void dosAsyncTask() throws InterruptedException {
        SETask<Boolean> asyncTask = new SETask<>(new SECancelableTask<Boolean>() {
            @Override
            public void cancel() {
                System.out.println("Task has been canceled!");
            }

            @Override
            public Boolean call() throws Exception {
                //Do some concurrent and time-consuming user task here
                System.out.println("Task begin running!");
                TimeUnit.SECONDS.sleep(3);
                return true;
            }
        });
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean taskResult = new AtomicBoolean();
        asyncTask.submit(new SETaskCallback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                taskResult.set(result);
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable throwable) {
                taskResult.set(false);
                latch.countDown();
            }
        });
        latch.await();
        Assert.assertTrue(taskResult.get());
    }

    @Test
    public void doCancelTask() throws Exception {
        SETask<Boolean> asyncTask = new SETask<>(new SECancelableTask<Boolean>() {
            @Override
            public void cancel() {
                System.out.println("Task has been canceled!");
            }

            @Override
            public Boolean call() throws Exception {
                //Do some concurrent and time-consuming user task here
                System.out.println("Task begin running!");
                TimeUnit.SECONDS.sleep(15);
                return true;
            }
        });
        asyncTask.submit(null);
        TimeUnit.SECONDS.sleep(2);
        asyncTask.cancel();
        try {
            asyncTask.get();
            Assert.assertTrue(false);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof CancellationException);
        }
    }

    @Test
    public void doExecuteTaskOnUserThread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        SETask<Boolean> asyncTask = new SETask<>(new SECancelableTask<Boolean>() {
            @Override
            public void cancel() {
                System.out.println("Task has been canceled!");
            }

            @Override
            public Boolean call() throws Exception {
                //here do some concurrent and time-consuming user task
                TimeUnit.SECONDS.sleep(2);
                return true;
            }
        });
        Boolean result = asyncTask.submit(executor,null).get();
        Assert.assertTrue(result);
    }

}