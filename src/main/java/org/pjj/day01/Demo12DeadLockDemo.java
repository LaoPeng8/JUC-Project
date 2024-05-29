package org.pjj.day01;

/**
 * 死锁测试
 * @author PengJiaJun
 * @Date 2024/05/29 09:59
 */
public class Demo12DeadLockDemo {
    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();

        new Thread(() -> {
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + " ==> 自己持有A锁, 希望获取B锁");
                try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }// 等待2秒, 保证B线程先获取到B锁
                synchronized (lockB) {
                    System.out.println(Thread.currentThread().getName() + " ==> 已经获取B锁");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + " ==> 自己持有B锁, 希望获取A锁");
                try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }// 等待2秒, 保证A线程先获取到A锁
                synchronized (lockA) {
                    System.out.println(Thread.currentThread().getName() + " ==> 已经获取A锁");
                }
            }

        }, "B").start();
    }
}
