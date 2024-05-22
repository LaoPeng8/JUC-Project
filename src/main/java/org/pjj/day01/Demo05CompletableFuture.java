package org.pjj.day01;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 不推荐new
 * 而是有四大静态方法
 * runAsync(Runnable runnable) 无返回值
 * runAsync(Runnable runnable, Executor executor) 无返回值
 * supplyAsync(Supplier<U> supplier) 有返回值
 * supplyAsync(Supplier<U> supplier, Executor executor) 有返回值
 * 没有指定Executor的方法, 使用默认的ForkJoinPool.commonPool()作为它的线程池执行异步代码
 *
 *
 * @author PengJiaJun
 * @Date 2024/05/22 14:29
 */
public class Demo05CompletableFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test02();
    }

    public static void test02() throws InterruptedException {
        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    System.out.println("当前线程: " + Thread.currentThread().getName() + "正在业务中...");
                    Thread.sleep(5000);
                    System.out.println("当前线程: " + Thread.currentThread().getName() + "正在业务中...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int res = new Random().nextInt(10);
                if(res >= 5) {
                    throw new RuntimeException("结果异常");
                }
                return "{code: 2000, message: 'ok', data: " + res + "}";
            }
        }).whenComplete(new BiConsumer<String, Throwable>() {// 执行完之后会回调该方法
            @Override
            public void accept(String s, Throwable throwable) {
                if(throwable == null) {
                    System.out.println("计算完成, 获取到上一步的计算的值: " + s);
                }
            }
        }).exceptionally(new Function<Throwable, String>() {
            @Override
            public String apply(Throwable throwable) {
                throwable.printStackTrace();
                System.out.println("捕获异常: " + throwable.getCause() + "\t" + throwable.getMessage());
                return null;
            }
        });

        System.out.println(Thread.currentThread().getName() + "干自己的事情");

        // 此处需要等待是因为, 主线程结束了, 那么CompletableFuture使用的默认线程池会立刻关闭
        Thread.sleep(6000);
    }

    /**
     * 可以看到 指定了线程池和没有指定线程池, 打印出的线程名称都不一样, 没有指定线程池默认是 ForkJoinPool
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void test01() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "---业务...");
            }
        });
        System.out.println(completableFuture.get());

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "---业务...");

            }
        }, fixedThreadPool);
        System.out.println(completableFuture1.get());
        fixedThreadPool.shutdown();
    }
}
