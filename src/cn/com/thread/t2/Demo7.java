package cn.com.thread.t2;

import java.util.Arrays;
import java.util.List;

/**
 * lambda表达式实现多线程
 * @author lilibo
 * @create 2021-12-31 7:21 PM
 */
public class Demo7 {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        int sum = list.parallelStream().mapToInt(value -> {
            System.out.println("the currentThread name is " + Thread.currentThread().getName());
            value = value * 2;
            return value;
        }).sum();
        System.out.println("sum = " + sum);
    }
}
