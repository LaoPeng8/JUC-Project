package org.pjj.day02;

/**
 * 线程中断
 *
 * 一个线程不应该由其他线程来强制中断或停止, 而是应该由线程自己自行停止, 自己决定自己的命运
 * 所以 Thread.stop(), Thread.suspend().. 等方法已经弃用了.
 *
 * 在Java中没有办法立即停止一条线程, 然而停止线程却显得尤为重要, 如取消一个耗时操作.
 * 因此, Java提供了一种用于停止线程的协商机制, 中断机制 (中断标识协商机制)
 *
 * 中断只是一种协作协商机制, Java没有给中断增加任何语法, 中断的过程完全由程序员自己实现.
 * 若要中断一个线程, 则需要手动的调用该线程的interrupt()方法, 该方法也仅仅将线程对象的中断标识设置为true
 * 接着需要自己写代码通过isInterrupted()方法 或 interrupted() 方法 来不断的检测当前线程的标识位, 如果为true, 则标识有其他线程请求该条下暗藏中断,
 * 此时究竟该做什么需要自己实现 (如通过将 while(flag) 中的 flag 置为false, 那么该线程就结束了)
 *
 * 每个线程对象中都有一个中断标识位, 表示该线程是否被中断, 通过线程对象的interrupt()方法将中断标识位设为true, 可在别的线程中调用, 也可在自己的线程中调用.
 *
 * 如果一个线程在阻塞中被其他线程调用了中断方法, 那么该线程会抛出异常 java.lang.InterruptedException: sleep interrupted, 可在捕获到该异常时, 实现该将线程停止
 *
 *
 * public void interrupt()      仅仅是设置线程的中断状态为true, 发起一个协商而不会立刻停止线程
 * public static boolean interrupted()      返回当前线程的中断状态, 并将当前中断状态清零重新设为false
 * public boolean isInterrupted()       判断当前线程是否被中断 (通过检查中断标志位) (并不会将中断状态重新设为false)
 *
 * @author PengJiaJun
 * @Date 2024/05/29 10:42
 */
public class Demo01Interrupt {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("start");
                Thread.sleep(5000);
                System.out.println("end");
            } catch (InterruptedException e) {
                System.out.println("在阻塞过程中, 被其他线程中断了");
            }
        }, "t1");

        t1.start();

        Thread.sleep(2000);

        t1.interrupt();//中断 t1 线程
    }
}
