package org.pjj.day01;

/**
 * 8锁案例:
 * 1. 标准访问有AB两个线程, 请问先打印邮件还是短信?
 *      邮件 (因为邮件后sleep了200毫秒, 才执行的短信线程, 此时邮件线程早就被CPU调度了)
 * 2. sendEmail()方法中加入暂停3秒钟, 请问先打印邮件还是短信?
 *      邮件 (因为1中邮件先被调度, 且synchronized定义在方法上, 锁的是调用这个方法的对象,
 *      由于短信邮件都是phone对象调用, 所以sendEmail()方法不执行完, 短信方法获取不到锁)
 * 3. 添加一个普通的hello方法, 请问先打印邮件还是hello?
 *      hello (因为 1中邮件先被调度, 但延迟3秒才打印, 200毫秒后B线程被调度, hello方法并没有加synchronized, 所以不需要获取锁直接执行hello方法)
 * 4. 有两部手机, 请问先打印邮件还是短信?
 *      短信 (因为1中邮件先被调度获取到锁, 但是是phone的锁(synchronized定义在方法上, 锁的是调用这个方法的对象),
 *      所以 当线程A被阻塞3秒时, 虽然没有释放锁, 但是线程B需要的锁是phone2的锁, 所以短信先执行)
 * 5. 将邮件与短信方法改为静态同步方法, 请问先打印邮件还是短信?
 *      邮件 (因为1中邮件先被调度获取到锁, 当synchronized修饰的是静态方法, 那么锁的则是整个类, 这个类同时只能有一个加锁的静态方法执行)
 * 6. 老师讲的 5,6是一样的, 无非就是5是一部手机, 6是两部手机, 是通过两个不同的对象调用的, 但是实际上编译后 对象.静态方法 还是会被编译为 类.静态方法
 * 7. 有一个静态同步方法, 有一个普通同步方法, 1部手机, 请问先打印邮件还是短信?
 *      短信 (synchronized修饰静态方法, 锁的是整个类, 类锁. synchronized修饰实例方法, 是对象锁 两者不一样, 可以分别获取到,
 *      所以当静态的邮件方法获取到类锁后(阻塞3秒), 实例方法短信, 依旧能获取到对象锁并执行)
 * 8. 老师讲的 7,8是一样的, 无非就是7是一部手机, 8是两部手机, 是通过两个不同的对象调用的, 但是实际上编译后 对象.静态方法 还是会被编译为 类.静态方法
 *
 *
 * @author PengJiaJun
 * @Date 2024/05/27 09:03
 */
public class Demo08Synchronized {
    public static void main(String[] args) throws InterruptedException {
        IPhone phone = new IPhone();
        IPhone phone2 = new IPhone();

        new Thread(() -> {
//            phone.sendEmail();
//            IPhone.sendEmail();
            phone.sendEmail();
        }, "A").start();

        Thread.sleep(200);

        new Thread(() -> {
//            phone.sendSMS();
//            phone.hello();
//            phone2.sendSMS();
//            IPhone.sendSMS();
            phone.sendSMS();
        }, "B").start();


    }
}

class IPhone {

    public static synchronized void sendEmail() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----sendEmail-----");
    }

    public synchronized void sendSMS(){
        System.out.println("-----sendSMS-----");
    }

    public void hello() {
        System.out.println("-----hello-----");
    }
}
