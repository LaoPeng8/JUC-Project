package org.pjj.day01;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 实际上, 我看博客上说, 为什么有了Future还需要CompletableFuture, 或者说 区别
 * 因为
 *  1. Future 想要获取结果, 需要调用get()方法, 但是该方法会阻塞线程, 不返回结果就一直堵在这,
 *      但是我又不知道它啥时候执行完, 难道我还一直等着吗? 那我还怎么干自己的事情, 那还开个集贸线程啊, 自己执行算了
 *  2. 通过轮询 isDone()方法, 来获取任务执行完成没有, 执行完成了则获取结果进行处理
 *      耗费无谓的CPU资源, 也并不能及时获取CPU资源
 *  3. 结论: Future对结果的获取并不是很友好, 只能通过阻塞和轮询
 *
 *  4. 所以我们需要一种, 能够在执行完了之后, 主动回调我们的方法, 而不是我们一直去轮询消耗CPU
 *  5. 将多个异步任务的计算结果组合起来, 后一个异步任务依赖前一个异步任务的结果
 *
 *
 * @author PengJiaJun
 * @Date 2024/05/21 22:53
 */
public class Demo04Future {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
//        test01();
        test02();
    }

    // 阻塞等待 get()
    public static void test01() throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                int i = 0;
                while(i++ < 10) {
                    System.out.println("业务进行中...");
                    Thread.sleep(1000);
                }
                return "over";
            }
        });

        new Thread(futureTask).start();// 执行线程体

        System.out.println("main..." + new Date().getSeconds());// 48秒
        String s = futureTask.get();
//        String s = futureTask.get(5000, TimeUnit.MILLISECONDS); // 5000毫秒如果没有返回会抛出超时异常TimeoutException
        System.out.println("main 获取到了异步任务值: " + s + " " + new Date().getSeconds());// 58秒
    }

    // 轮询 isDone()
    public static void test02() throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                int i = 0;
                while(i++ < 10) {
                    System.out.println("业务进行中...");
                    Thread.sleep(1000);
                }
                return "over";
            }
        });

        new Thread(futureTask).start();// 执行线程体

        // 可以新开一个线程来轮询监测 futureTask.isDone() 是否执行完了, 执行完了才去 get();
        new Thread(() -> {
            boolean flag = true;
            while(flag) {
                boolean done = futureTask.isDone();// 是否执行完了
                if(done) {
                    try {
                        String res = futureTask.get();
                        System.out.println(Thread.currentThread().getName() + " ==> res = " + res);
                        // 插入数据库等其他操作...
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    flag = false;
                }

                // 如果没有执行完, 则等200毫秒后继续判断
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 这边就直接返回了
        System.out.println("main...end");
    }
}
