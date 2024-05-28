package org.pjj.day01;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 演示非公平锁
 *
 * 模拟3个售票员卖完50张票 (3个线程, 消费50张票)
 * 可以看到如果是非公平锁, 则可能一个线程就卖完50张票
 *       如果是公平锁, 则会是3个线程相互执行卖票方法
 *
 * 公平锁: 多个线程按照申请锁的顺序来获取锁, 这里类似排队买票, 先来的人先买, 后来的人在队尾排着
 * 非公平锁: 多个线程获取锁的顺序并不是按照申请锁的顺序, 有可能后申请的线程比先申请的线程优先获取锁, 在高并发环境下, 有可能造成优先级翻转或者饥饿的状态(某个线程一直得不到锁)
 *
 * 非公平锁可以节省线程切换时间(线程切换的开销还是很大的)
 *
 * @author PengJiaJun
 * @Date 2024/05/28 16:54
 */
public class Demo10SaleTicketDemo {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for(int i=0; i < 55; i++) {
                ticket.sale();
            }
        }, "a").start();

        new Thread(() -> {
            for(int i=0; i < 55; i++) {
                ticket.sale();
            }
        }, "b").start();

        new Thread(() -> {
            for(int i=0; i < 55; i++) {
                ticket.sale();
            }
        }, "c").start();
    }
}

class Ticket {
    private int number = 50;
//    ReentrantLock lock = new ReentrantLock();
    ReentrantLock lock = new ReentrantLock(true);// true表示公平锁

    public void sale(){
        lock.lock();
        try{
            if(number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第: " + (number--) + "张票, 还剩下" + (number));
            }
        } finally {
            lock.unlock();
        }
    }
}
