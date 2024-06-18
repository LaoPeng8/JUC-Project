package org.pjj.day02;

/**
 *
 * 被volatile修饰的变量有2大特点: 可见性, 有序性
 *
 * volatile内存语义:
 *  当写一个volatile变量时, JMM会把该线程对应的本地内存中的共享变量值立即刷新回主内存中
 *  当读一个volatile变量时, JMM会把该线程对应的本地内存设置为无效, 重新回到主内存中读取最新的共享变量
 *  所以volatile的写内存语义是直接刷新到主内存中, 读的内存语义是直接从主内存中读取
 *
 * volatile为什么可以保证可见性,有序性?
 *  内存屏障 Memory Barrier
 *
 * 内存屏障(也称内存栅栏, 屏障指令等, 是一类同步屏障指令, 是CPU或编译器在对内存随机访问的操作中的一个同步点, 使得此点之前的所有读写操作都执行后才可以开始执行此点之后的操作), 避免代码重排序.
 * 内存屏障其实就是一种JVM指令, Java内存模型的重排规则会要求Java编译器在生成JVM指令时插入特定的内存屏障指令, 通过这些内存屏障指令volatile实现了Java内存模型中的可见性和有序性(禁重排),但volatile无法保证原子性
 *
 * 内存屏障之前的所有写操作都要回到主内存
 * 内存屏障之后的所有读操作都能获得内存屏障之前的所有写操作的最新结果(实现了可见性)
 * 写屏障(Store Memory Barrier): 告诉处理器在写入屏障之前将所有存储在缓存(store bufferes)中的数据同步到主内存.也就是说看到Store屏障指令, 就必须把该指令之前所有写入指令执行完毕才能继续往下执行
 * 读屏障(Load Memory Barrier): 处理器在读取屏障之后的读操作, 都在读操屏障之后执行. 也就是说在Load屏障指令之后就能够保证后面的读取数据指令一定能够读取到最新的数据
 * 因此重排序时, 不允许把内存屏障之后的指令重排序到内存屏障之前. 一句话: 对一个volatile变量的写, 先行发生于任意后续对这个volatile变量的读, 也叫写后读.
 *
 *
 *
 * @author PengJiaJun
 * @Date 2024/06/13 09:43
 */
public class Demo06Volatile {

    static boolean flag = true;

    /**
     * 情况1:
     *      当 flag 没有被 volatile 修饰时
     *      在主线程3秒后将flag置为false; 但是在t1线程中的while(flag), 就仿佛没有感知到flag被置为false; 还在不停的循环, 根本不会打印 end
     *      问题可能:
     *          1. 主线程修改了flag之后没有将其刷新到主内存, 所以t1线程感知不到
     *          2. 主线程将flag刷新到了主内存, 但是t1线程一致读取的是自己工作内存中flag的值, 没有去主内存中更新获取flag最新的值.
     *
     * 情况2:
     *      当 flag 被 volatile 修饰时
     *      程序正常执行, 说明 volatile 起作用了保证了线程共享变量的可见性.
     *      原因:
     *          1. volatile保证了可见性,有序性, 被volatile修饰的变量有以下特点
     *          2. 线程中读取的时候, 每次读取都会去主内存中读取共享变量最新的值, 然后赋值到工作内存
     *          3. 线程中修改了工作内存中变量的副本, 修改之后会立即刷新到主内存
     *
     * 情况3:
     *      当 flag 没有被 volatile 修饰时
     *      while(flag){} 中 打印了 flag 的值时
     *      程序正常执行, 在主线程3秒后将flag置为false时, t1线程中的 end 成功打印, 并且t1线程结束
     *      问题可能:
     *          1. 打印处读取的flag 和 while处读取的flag 方式不一样??? 打印出的flag是重新去主内存获取的.
     *          2. t1线程内连续两次读取flag就会重新去主内存获取???
     *          3. while(flag)的读取flag不算真正的读取??? 所以当打印出的flag相当于真正使用了flag这个变量, 当线程中使用了flag变量时候, 其他线程对flag赋值时才会将其刷新到主内存???
     *
     * @throws InterruptedException
     */
    public static void test01() throws InterruptedException {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " --- start");
            while(flag) {
                System.out.println(Thread.currentThread().getName() + " --- flag = " + flag);
            }
            System.out.println(Thread.currentThread().getName() + " --- end");
        }, "t1").start();

        Thread.sleep(3000);
        flag = false;
        System.out.println(Thread.currentThread().getName() + " --- flag = " + flag);
    }

    /**
     * volatile 不能保证原子性
     */
    public static void test02() throws InterruptedException {

        MyNumber myNumber = new MyNumber();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myNumber.addPlus();
                }
            }, "线程-" + i).start();
        }

        Thread.sleep(3000);

        System.out.println(myNumber.number);
    }

    public static void main(String[] args) throws InterruptedException {
//        test01();
        test02();
    }
}

class MyNumber {

    volatile int number = 0;

    public void addPlus() {
        number++;
    }

//    int number = 0;
//
//    public synchronized void addPlus() {
//        number++;
//    }

}
