package org.pjj.day01;

import java.util.concurrent.*;

/**
 * 不系, 哥们
 * 这例子举的, 看不懂, 是想说明线程池可以重复利用线程, 不用一个一个new线程来消耗资源吗
 *
 * @author PengJiaJun
 * @Date 2024/05/21 22:25
 */
public class Demo03FutureThreadPool {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        demo01();
        demo02();
    }

    // 如果直接使用 FutureTask来处理, 则需要使用 3 个线程, 如果3个还好, 更多就不好了
    // 可以使用线程池, 核心线程数 3, 再有加入则会进入队列阻塞, 线程可重复利用
    // 耗时 708 这耗时多多少少还是有点, 哈哈
    public static void demo02() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<String> futureWork01 = executorService.submit(() -> {
            work01();
            return "work01 - over";
        });

        Future<String> futureWork02 = executorService.submit(() -> {
            work02();
            return "work02 - over";
        });

        Future<String> futureWork03 = executorService.submit(() -> {
            work03();
            return "work03 - over";
        });

        // get()方法阻塞的等待执行结果
        System.out.println(futureWork01.get());
        System.out.println(futureWork02.get());
        System.out.println(futureWork03.get());

        executorService.shutdown();

        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + " == 耗时: " + (end - start));
    }

    // 3个任务, 目前只有一个线程main来处理, 耗时 1804
    public static void demo01() {
        long start = System.currentTimeMillis();
        work01();
        work02();
        work03();
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName() + " == 耗时: " + (end - start));
    }

    public static void work01() {
        try {
            System.out.println("work01");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void work02() {
        try {
            System.out.println("work02");
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void work03() {
        try {
            System.out.println("work03");
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
