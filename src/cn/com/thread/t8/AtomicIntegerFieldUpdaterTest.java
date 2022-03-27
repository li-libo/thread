package cn.com.thread.t8;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.Stream;

/**
 * 原子更新字段测试
 * @author lilibo
 * @create 2022-01-02 10:51 AM
 */
public class AtomicIntegerFieldUpdaterTest {

    public static final int numOfTestThread = 5;

    public static final int numOfLoop = 5;

    @Test
    public void test1() {
        // 属性需要为volatile, 且属性可以访问
        AtomicIntegerFieldUpdater<Person> ageAtomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");
        Person person = new Person();
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            Stream.iterate(0, count1 -> count1 + 1).limit(numOfLoop).forEach(count1 -> {
                ageAtomicIntegerFieldUpdater.incrementAndGet(person);
            });
        });
        System.out.println(person);
    }

}
