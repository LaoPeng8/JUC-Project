package org.pjj.day02;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 通过一个 volatile 变量实现, 中断一个线程 (虽然不知道 加不加 volatile 作用貌似一样, volatile可以防止指令重排 和 可以使变量对所有的线程立即可见)
 * 通过 原子类 AtomicBoolean 实现, 中断一个线程 (原子类保证了该变量的值, 在多线程的条件下, 也是线程安全的)
 * 通过线程中断API 实现, 中断一个线程
 *
 * @author PengJiaJun
 * @Date 2024/05/29 13:48
 */
public class Demo02Interrupt {

    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
//        m1_volatile();
//        m2_AtomicBoolean();
        m3_interrupt();
    }

    private static void m3_interrupt() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("t1 --- interrupt()被调用, 程序停止");
                    break;
                }

                System.out.println("t1 --- 业务中...");
            }
        }, "t1");
        t1.start();

        Thread.sleep(200);

        new Thread(t1::interrupt, "t2").start();
    }

    private static void m2_AtomicBoolean() throws InterruptedException {
        new Thread(() -> {
            while(true) {
                if(atomicBoolean.get()) {
                    System.out.println("t1 --- atomicBoolean被修改为true, 程序停止");
                    break;
                }

                System.out.println("t1 --- 业务中...");
            }
        }, "t1").start();

        Thread.sleep(200);

        new Thread(() -> atomicBoolean.set(true), "t2").start();
    }

    private static void m1_volatile() throws InterruptedException {
        new Thread(() -> {
            while(true) {
                if(isStop) {
                    System.out.println("t1 --- isStop被修改为true, 程序停止");
                    break;
                }

                System.out.println("t1 --- 业务中...");
            }
        }, "t1").start();

        Thread.sleep(200);

        new Thread(() -> isStop = true, "t2").start();
    }
}
