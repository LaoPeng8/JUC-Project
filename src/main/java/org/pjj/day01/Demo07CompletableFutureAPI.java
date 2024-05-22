package org.pjj.day01;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * CompletableFuture常见API
 *
 * completableFuture.get() 与 completableFuture.join() 的区别:
 * 相同: 都会一直阻塞等待获取结果
 * 不同: get()抛出的异常是继承至Exception, 需要我们try catch来处理, join()抛出的异常是RuntimeException 不会强制开发者抛出
 *
 * @author PengJiaJun
 * @Date 2024/05/22 17:20
 */
public class Demo07CompletableFutureAPI {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "success";
            }
        });

        // 一直阻塞等待获取结果
//        System.out.println(completableFuture.get());

        // 只等待500毫秒, 等不到就不等了, 直接抛出异常 java.util.concurrent.TimeoutException
//        System.out.println(completableFuture.get(500, TimeUnit.MILLISECONDS));

        // 一直阻塞等待获取结果
//        System.out.println(completableFuture.join());

        // 如果此时结果已经返回了, 则返回实际结果, 否则返回咋们输入的参数
//        Thread.sleep(1000);
//        System.out.println(completableFuture.getNow("please continue wait..."));

        // 用于手动完成一个异步任务, 并设置其结果, 如果设置时异步任务已经完成, 则设置不会成功并返回false, 如果设置时候异步任务并没有完成, 则设置成功, 并且异步任务完成(值为此处设置的值), 并且返回true
//        Thread.sleep(1000);
//        boolean completeFlag = completableFuture.complete("complete return value");
//        System.out.println(completeFlag + " -- " + completableFuture.join());
    }
}
