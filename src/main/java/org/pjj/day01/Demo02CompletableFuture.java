package org.pjj.day01;

import java.util.concurrent.*;

/**
 * Future接口定义了操作异步任务执行的一些方法, 如获取异步任务的执行结果、取消任务的执行、判断任务是否被取消、判断任务执行是否完毕等.
 * 例如: 主线程需要执行一个很耗时的计算任务, 我们就可以通过Future把这个任务放到异步线程中执行. 主线程继续处理其他任务或者先行结束, 再通过Future获取计算结果.
 *
 * FutureTask是Future的实现类, 可通过该类实现异步任务执行
 * 也可通过线程池, 来返回Future
 *
 * @author PengJiaJun
 * @Date 2024/05/21 16:39
 */
public class Demo02CompletableFuture {
    public static void main(String[] args) {
        try {
            FutureTask<String> futureTask = new FutureTask<>(new MyThread());
            futureTask.run();

            boolean done = futureTask.isDone();
            System.out.println("异步任务是否完成: " + done);

            String s = futureTask.get();
            System.out.println(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("---main---");

//        ExecutorService es = Executors.newCachedThreadPool();
//        Future<String> future = es.submit(new MyThread());
//        future.isDone();
//        future.get();
    }
}

class MyThread implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("---Callable Start---");
        Thread.sleep(3000);
        System.out.println("---Callable End---");
        return "hello Callable";
    }
}
