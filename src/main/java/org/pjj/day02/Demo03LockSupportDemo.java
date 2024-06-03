package org.pjj.day02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * synchronized wait notify
 * lock await signal unlock
 * 可以发现这两组线程等待与唤醒的方法都需要获取锁了之后才能正常执行, 一个是需要在synchronized同步块中执行, 一个是需要lock.lock()后才能执行,
 * 否则均会抛出 java.lang.IllegalMonitorStateException 异常
 *
 * 上述两个对象Object和Condition使用的限制条件
 * 1. 线程必须要先获得并持有锁, 必须在锁块(synchronized 或 lock)中
 * 2. 必须要先等待后唤醒, 线程才能够被唤醒
 *
 * LockSupport.park();
 * LockSupport.unpark(线程);
 * 1. 不需要一定要在锁块中
 * 2. 可以先unpark() 后 park() 也一样会被唤醒
 *
 *
 * @author PengJiaJun
 * @Date 2024/06/03 15:00
 */
public class Demo03LockSupportDemo {
    public static void main(String[] args) throws InterruptedException {
//        m1_wait_notify();
//        m2_await_signal();
        m3_park_unpark();
    }

    public static void m3_park_unpark() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " --- 业务中...");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + " --- 被唤醒了");
        }, "t1");
        t1.start();

        Thread.sleep(1000);

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " ---业务中... ");
            LockSupport.unpark(t1);
            System.out.println(Thread.currentThread().getName() + " --- 发出通知");
        }, "t2").start();
    }

    public static void m2_await_signal() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " --- 业务中...");
                condition.await();
                System.out.println(Thread.currentThread().getName() + " --- 被唤醒了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        }, "t1").start();

        Thread.sleep(1000);

        new Thread(() -> {
            lock.lock();
            condition.signal();
            System.out.println(Thread.currentThread().getName() + " --- 发出通知");
            lock.unlock();
        }, "t2").start();

    }

    public static void m1_wait_notify() throws InterruptedException {
        Object objectLock = new Object();

        new Thread(() -> {
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + " --- 业务中...");
                try {
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(Thread.currentThread().getName() + " --- 被唤醒了");
            }
        }, "t1").start();

        Thread.sleep(1000);

        new Thread(() -> {
            synchronized (objectLock) {
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + " --- 发出通知");
            }
        }, "t2").start();
    }
}
