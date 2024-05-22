package org.pjj.day01;

import java.text.Format;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author PengJiaJun
 * @Date 2024/05/22 15:50
 */
public class Demo06CompletableFutureMall {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
//        List<String> prices = getPrice();// 耗时: 6094
//        List<String> prices = getPriceByCompletableFuture();// 耗时: 6091
        List<String> prices = getPriceByCompletableFuture2();// 1095
        for (String item : prices) {
            System.out.println(item);
        }
        long end = System.currentTimeMillis();
        System.out.println("单线程耗时: " + (end - start));
    }

    private static List<NetMall> netMallList = Arrays.asList(
            new NetMall("京东"),
            new NetMall("当当"),
            new NetMall("淘宝"),
            new NetMall("拼多多"),
            new NetMall("拼少少"),
            new NetMall("拼夕夕")
    );

    /**
     * 单线程操作 耗时: 6094
     * @return
     */
    public static List<String> getPrice() {
        List<String> result = netMallList
                .stream()
                .map((netMall -> netMall.getMallName() + " -> mysql -> " + netMall.getBookPrice()))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * CompletableFuture 耗时: 6091
     *
     * 是有问题的, 这种写法虽然 List中有几条数据, 就开了几个线程(CompletableFuture)去处理,
     * 但是主线程却在等待每个线程执行完了, join()返回值了, 才开下一个线程去处理, 这和一个线程有什么区别, 全部搁那里被join()阻塞了
     * @return
     */
    public static List<String> getPriceByCompletableFuture() {
        List<String> result = netMallList
                .stream()
                .map((netMall) -> {
                    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
                        return netMall.getMallName() + " -> mysql -> " + netMall.getBookPrice();
                    });
                    return completableFuture.join();
                }).collect(Collectors.toList());
        return result;
    }

    /**
     * CompletableFuture 耗时: 1095
     *
     * 这种方式才是正确的, 主线程先不需要线程返回值, 而是先把任务全部给线程分发下去, 之后在获取值,
     * 假设这些线程干的事都一样(效率一样), 那么join()就算阻塞等待, 也只用阻塞一次, 第一个线程返回了, 其他线程也都该返回了
     *
     * @return
     */
    public static List<String> getPriceByCompletableFuture2() {
        List<CompletableFuture<String>> CompletableFutureList = netMallList
                .stream()
                .map((netMall) -> {
                    // 当 lambda表达式 方法体只有一行时 {} 可以省略, return 也可以省略
                    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() ->
                        netMall.getMallName() + " -> mysql -> " + netMall.getBookPrice()
                    );
                    return completableFuture;
                }).collect(Collectors.toList());

        List<String> res = CompletableFutureList
                .stream()
                .map((item) -> item.join())
                .collect(Collectors.toList());

        return res;
    }
}


class NetMall {
    private String mallName;
    private String bookName;
    private double bookPrice;

    public NetMall() {
    }

    public NetMall(String mallName) {
        this.mallName = mallName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public double getBookPrice() {
        double temp = new Random().nextDouble() * 100;
        String format = String.format("%.2f", temp);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Double(format);
    }

    public String getMallName() {
        return mallName;
    }

    public void setMallName(String mallName) {
        this.mallName = mallName;
    }
}
