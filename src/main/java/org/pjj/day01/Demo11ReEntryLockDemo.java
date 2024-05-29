package org.pjj.day01;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁: 指可重复可递归调用的锁, 在外层使用锁之后, 在内层仍然可以使用, 不会发生死锁
 *
 * 可重入锁分为 隐式锁 和 显式锁
 *      隐式锁是指synchronized关键字使用的锁, 默认是可重入锁
 *      显式锁即Lock, 也有ReentrantLock这样的可重入锁
 *
 * @author PengJiaJun
 * @Date 2024/05/29 09:20
 */
public class Demo11ReEntryLockDemo {
    public static void main(String[] args) {
//        new Demo11ReEntryLockDemo().m1();

        ReentrantLock lock = new ReentrantLock();
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " ----- 外层调用");

                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + " ----- 内层调用");
                } finally {
                    lock.unlock();
                }

            } finally {
                // 加锁后一定要释放锁, 否则下次获取该锁时,就获取不到了
                lock.unlock();
            }

        }).start();

    }

    // 可重入锁, synchronized方法测试
    public synchronized void m1() {
        System.out.println(Thread.currentThread().getName() + " ----- m1 start");
        m2();
        System.out.println(Thread.currentThread().getName() + " ----- m1 end");
    }

    public synchronized void m2() {
        System.out.println(Thread.currentThread().getName() + " ----- m2 start");
        m3();
        System.out.println(Thread.currentThread().getName() + " ----- m2 end");
    }

    public synchronized void m3() {
        System.out.println(Thread.currentThread().getName() + " ----- m3 start");
        System.out.println(Thread.currentThread().getName() + " ----- m3 end");
    }

    // 可重入锁, synchronized代码块测试
    private static void reEntryM1() {
        Object lock = new Object();

        new Thread(() -> {
            synchronized (lock) {
                System.out.println(Thread.currentThread().getName() + " ----- 外层调用");

                synchronized (lock) {
                    System.out.println(Thread.currentThread().getName() + " ----- 中层调用");

                    synchronized (lock) {
                        System.out.println(Thread.currentThread().getName() + " ----- 内层调用");
                    }
                }
            }
        }, "t1").start();
    }
}
