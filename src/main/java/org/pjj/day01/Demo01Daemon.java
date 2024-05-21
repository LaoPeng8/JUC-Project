package org.pjj.day01;

/**
 * @author PengJiaJun
 * @Date 2024/05/21 15:29
 */
public class Demo01Daemon {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t开始运行, " + (Thread.currentThread().isDaemon() ? "守护线程" : "用户线程"));
            while (true) {

            }
        }, "t1");

        t1.setDaemon(true);// 守护线程, 当没有用户线程时, JVM会直接停止运行
        t1.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + "\t ----end Main Thread");
    }
}
