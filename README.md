# SETask

一个灰常灰常简单的任务执行类。类似Android的AsyncTask类。
为啥要使用这样的类哩：
1. 支持同步的任务。
2. 支持异步的任务。
3. 多个任务并发执行。
4. 任务可以取消。


### 如何使用

1. new 一个SETask.有两种办法构建SETask
    - 传入一个SECancelableTask.
    - 继承SETask并实现call和onCancel这两个方法。

2. 调用submit方法提交这个task。提交的时候可选参数
    - SETaskCallback  异步执行回调
    - ExecutorService 任务执行线程，传null,使用默认线程池。

3. 取消任务调用cancel方法

4. 同步任务callback传null,调用SETask#get()获取任务执行结果。

5. 异步任务通过callback返回结果。

6. 子类可以调用的方法：
    - notifyProgressUpdate 对于带进度的任务，可以调用这个方法在通知回调进度更新。
