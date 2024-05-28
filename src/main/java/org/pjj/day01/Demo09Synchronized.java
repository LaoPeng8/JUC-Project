package org.pjj.day01;

/**
 * 通过 javap -c Demo09Synchronized.class 后
 * 可以发现
 *      synchronized代码块, 是由一个进入, 两个退出命令组成的
 *      monitorenter 获取锁, monitorexit 释放锁 (出现了两次, 一次为正常释放锁, 一次为如果同步块中出现了异常, 则又第二次的 monitorexit释放锁)
 *      一般情况下都是, 一个monitorenter配两个monitorexit (极端情况, 自己在同步代码块中抛出异常, 则只有一个monitorexit)
 *
 *      synchronized方法, 通过 javap -v Demo09Synchronized.class 后,
 *      发现, 有该关键字修饰的方法, 在字节码上被添加上了 ACC_SYNCHRONIZED 标识, 表示这是一个同步方法, JVM在执行时就会自动加锁 monitorenter与monitorexit
 *
 *      synchronized修饰的静态方法
 *      发现不止有 ACC_SYNCHRONIZED, 还会被加上 ACC_STATIC
 *
 *      所以是由 ACC_STATIC和ACC_SYNCHRONIZED 来区分某方法是否为静态同步方法
 *
 * @author PengJiaJun
 * @Date 2024/05/27 14:22
 */
public class Demo09Synchronized {

    Object lock = new Object();

//    public void m1() {
//        System.out.println("--- m1 ---");
//        synchronized (lock) {
//            System.out.println("--- m1: hello synchronized code block ---");
//            // throw new RuntimeException("exception");
//        }
//    }

    public synchronized void m2() {
        System.out.println("--- m2 ---");
        System.out.println("--- m2: hello synchronized code block ---");
    }

    public static synchronized void m3() {
        System.out.println("--- m3 ---");
        System.out.println("--- m3: hello synchronized code block ---");
    }

    public static void main(String[] args) {

    }
}
