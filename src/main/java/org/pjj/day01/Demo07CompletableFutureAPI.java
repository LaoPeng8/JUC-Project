package org.pjj.day01;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * CompletableFuture常见API
 *
 * completableFuture.get() 与 completableFuture.join() 的区别:
 * 相同: 都会一直阻塞等待获取结果
 * 不同: get()抛出的异常是继承至Exception, 需要我们try catch来处理, join()抛出的异常是RuntimeException 不会强制开发者抛出
 *
 * 顺序执行:
 *  thenRun(Runnable runnable), 任务A执行完执行B, 并且B不需要A的结果
 *  thenAccept(Consumer action), 任务A执行完执行B, B需要A的结果, 但是B没有返回值
 *  thenApply(Function fn), 任务A执行完执行B, B需要A的结果, 同时B也有返回值
 *
 * 关于CompletableFuture使用线程池的问题 (test06())
 * 1. 没有传入自定义线程池, 则使用默认线程池ForkJoinPool
 * 2. 传入了自定义线程池
 *  2.1 如果执行第一个任务时传入了一个自定义线程池, 则调用thenRun()方法执行第二个任务时, 则第二个任务和第一个任务是共用一个线程池(自定义线程池)
 *  2.2 如果执行第一个任务时传入了一个自定义线程池, 则调用thenRunAsync()方法执行第二个任务时, 第一个任务使用自定义线程池, 第二个任务使用的是ForkJoinPool线程池
 * 3. 可能是因为处理太快, 系统优化切换原则, 直接使用main线程处理 (类似于数据量少了, mysql走索引查询, 依旧是全表扫描)
 * 4. 其他如: thenAccept和thenAcceptAsync, thenApply和thenApplyAsync等, 它们之间的区别也是同理 (那么我觉得它们之间的区别就是, 普通版并没有提供传入线程池的方法, 而Async版, 重载了一个可以传入线程池的方法)
 *
 * @author PengJiaJun
 * @Date 2024/05/22 17:20
 */
public class Demo07CompletableFutureAPI {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        test01();
//        test02();
//        test03();
//        test04();
//        test05();
//        test06();
//        test07();
        test08();
    }

    public static void test01() {
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

    /**
     * .thenApply类似于串行化, 只有当上一个任务完成了, 才会执行下一个任务, 如果中途报错了, 那么就在报错处直接中断了, 停止执行了
     * @throws InterruptedException
     */
    public static void test02() throws InterruptedException {
        CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("业务111...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        }).thenApply(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer s) {
                System.out.println("业务222...");
//                int i = 10/0;
                return s + 2;
            }
        }).thenApply(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                System.out.println("业务333...");
                return integer + 3;
            }
        }).whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer integer, Throwable throwable) {
                if(throwable == null) {
                    System.out.println("业务结果为: " + integer);
                }
            }
        }).exceptionally(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return null;
            }
        });

        System.out.println(Thread.currentThread().getName() + " -- 主线程先去做其他事情");
        // 此处需要等待是因为, 主线程结束了, 那么CompletableFuture使用的默认线程池会立刻关闭
        Thread.sleep(6000);
    }

    /**
     * handle 有异常也可以往下一步走, 根据带的异常参数可以进行下一步的处理
     * @throws InterruptedException
     */
    public static void test03() throws InterruptedException {
        CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                System.out.println("业务111...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        }).handle(new BiFunction<Integer, Throwable, Integer>() {
            @Override
            public Integer apply(Integer integer, Throwable throwable) {
                System.out.println("业务222...");
                int i = 10/0;
                return integer + 2;
            }
        }).handle((f, e) -> {
            System.out.println("业务333...");
            return f + 3;
        }).whenComplete((i, t) -> {
            if(t == null) {
                System.out.println("业务结果为: " + i);
            }
        }).exceptionally(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return null;
            }
        });

        System.out.println(Thread.currentThread().getName() + " -- 主线程先去做其他事情");
        // 此处需要等待是因为, 主线程结束了, 那么CompletableFuture使用的默认线程池会立刻关闭
        Thread.sleep(6000);
    }

    /**
     * thenAccept需要的是一个Consumer接口, 即一个输入值和没有返回值, 故称消费式接口
     */
    public static void test04() {
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(f -> {
            return f + 2;
        }).thenApply(f -> {
            return f + 3;
        }).thenAccept(res -> System.out.println(res));
    }

    /**
     * thenRun不接收返回值也不返回结果
     */
    public static void test05() {
        Void join = CompletableFuture.supplyAsync(() -> {
            System.out.println("业务111中...");
            return 1;
        }).thenRun(() -> {
            System.out.println("业务222中...");
        }).join();

        System.out.println(join);
    }

    /**
     * 线程池的测试
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void test06() throws ExecutionException, InterruptedException {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(20);
                System.out.println("1号任务\t" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "success";
        }).thenRunAsync(() -> {
            try {
                Thread.sleep(20);
                System.out.println("2号任务\t" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            try {
                Thread.sleep(20);
                System.out.println("3号任务\t" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            try {
                Thread.sleep(20);
                System.out.println("4号任务\t" + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(completableFuture.get());

        Thread.sleep(3000);
        fixedThreadPool.shutdown();
    }

    /**
     * 对计算速度进行选用
     */
    public static void test07() {
        CompletableFuture<String> playA = CompletableFuture.supplyAsync(() -> {
            System.out.println("A come in");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "playA";
        });

        CompletableFuture<String> playB = CompletableFuture.supplyAsync(() -> {
            System.out.println("B come in");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "playB";
        });

        /**
         * 对计算速度进行选用
         * 比较两个completableFuture的执行速度, 并且返回较快执行的completableFuture的结果, 并在 lambda表达式中处理后返回
         */
        CompletableFuture<String> result = playA.applyToEither(playB, (completableFuture) -> {
            return completableFuture + " is winner";
        });
        System.out.println(Thread.currentThread().getName()+"\t"+"---: " + result.join());
    }

    /**
     * 对任务结果的合并
     */
    public static void test08() {
        CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " --- 启动");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 10;
        });

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " --- 启动");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 210;
        });

        CompletableFuture<Integer> result = completableFuture1.thenCombine(completableFuture2, (future1res, future2res) -> {
            System.out.println("--- 开始两个结果合并 ---");
            return future1res + future2res;
        });

        Integer join = result.join();
        System.out.println(join);
    }

}
