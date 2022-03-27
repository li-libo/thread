package cn.com.thread.t8;

/**
 * @author lilibo
 * @create 2022-01-02 10:49 AM
 */
public class Person {

    volatile int age;

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                '}';
    }
}
