package org.pjj.day02;

/**
 *
 * 在JMM中, 如果一个操作执行的结果需要对另一个操作可见性, 或者代码重排序, 那么这两个操作之间必须存在happens-before(先行发生)原则.
 * 逻辑上的先后关系
 *
 * x = 5; 线程A执行
 * y = x; 线程B执行
 *
 * 问题: y是否等于5呢?
 * 如果 线程A的操作(x=5) happens-before(先行发生)线程B的操作(y=x), 那么可以确定线程B执行后y=5一定成立;
 * 如果 它们不存在 happens-before原则, 那么y=5不一定成立.
 * 这就是 happens-before原则 --> 包含可见性和有序性的约束
 *
 * 我们没有时时、处处、次次, 添加volatile和synchronized来完成程序, 这是因为Java语言中JMM原则下, 有一个"先行发生"(happens-before)的原则限制和规则
 *
 * happens-before总原则
 * 如果一个操作happens-before(先行于)另一个操作, 那么第一个操作的执行结果将对第二个操作可见, 而且第一个操作的执行顺序排在第二个操作之前.
 * 两个操作之间存在happens-before关系, 并不意味着一定要按照happens-before原则制定的顺序来执行. 如果重排序之后的执行结果与按照happens-before关系来执行的结果一致, 那么这种重排序并不非法
 * (第二点的意思就是说, 就算有happens-before, 代码也不一定完全按照happens-before原则的顺序来执行, 可能会发生指令重排, 但是重排序后结果肯定是一致的)
 *
 * happens-before之8条
 * 1.次序规则
 *      一个线程内, 按照代码顺序, 写在前面的操作先行发生于写在后面的操作
 * 2.锁定规则
 *      一个unlock操作先行发生于后面对同一个锁的lock操作; (一定是先解锁后, 才能上锁)
 * 3.volatile变量规则
 *      对一个volatile变量的写操作先行发生于后面对这个变量的读操作, 前面的写对后面的读是可见的; ("后面"指的是时间上的先后)
 * 4.传递规则
 *      如果操作A先行发生于操作B, 而操作B又先行发生于操作C, 则可以得出操作A先行发生于操作C
 * 5.线程启动规则 (Thread Start Rule)
 *      Thread对象的start()方法先行发生于此线程的每一个动作
 * 6.线程中断规则 (Thread Interruption Rule)
 *      对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生; (也就是说, 我方法都没调, 中断线程的检测代码怎么可能检测的到? 所以是interrupt()先行发生于代码检测中断事件)
 * 7.线程中止规则 (Thread Termination Rule)
 *      线程中的所有操作都先行发生于对此线程的终止检测, 我们可以通过isAlive()等手段检测线程是否已经中止执行
 * 8.对象终结规则 (Finalizer Rule)
 *      一个对象的初始化完成(构造函数执行结束), 先行发生于它的finalize()方法的开始
 *
 * @author PengJiaJun
 * @Date 2024/06/13 09:42
 */
public class Demo05HappensBefore {
    public static void main(String[] args) {

    }
}
