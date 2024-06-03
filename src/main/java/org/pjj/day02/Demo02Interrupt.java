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
//        m3_interrupt();
        m4_interrupt();
    }

    /**
     * 如果一个线程在阻塞中被其他线程调用了中断方法, 那么该线程会抛出异常 java.lang.InterruptedException: sleep interrupted 并将中断状态清除 (重新设置为false)
     *
     * 当t2线程将 t1线程的中断状态修改为true时, 正常来说t1会检测到中断状态的修改并做出处理, 但是当t1线程处于阻塞时发现该线程中断状态为true, 那么会抛出异常并且将中断状态清除,
     * 导致下一次循环时判断中断状态依旧为false, 则不会停止循环
     * 如果在catch中再次修改中断状态为true, 由于catch中也没有阻塞了, 所以下一次循环时候检测到中断状态为true就直接停止循环了,结束线程了
     *
     * @throws InterruptedException
     */
    private static void m4_interrupt() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while(true) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("t1 --- interrupt()被调用, 程序停止");
                    break;
                }

                System.out.println("t1 --- 业务中...");

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }, "t1");
        t1.start();

        Thread.sleep(300);

        new Thread(t1::interrupt, "t2").start();
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
