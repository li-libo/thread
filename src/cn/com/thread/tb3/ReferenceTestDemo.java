package cn.com.thread.tb3;

import org.junit.Test;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 /**
 * 强引用-软引用-弱引用
 * 强引用: 普通的引用,强引用指向的对象不会被回收
 * 软引用: 仅有软引用指向的对象,只有发生gc且内存不足时,才会被回收
 * 弱引用: 仅有弱引用指向的对象,只要发生gc就会被回收

 * @author lilibo
 * @create 2022-01-05 8:08 PM
 */
public class ReferenceTestDemo {

    public static final String format1 = "strongReference = %s, softReference = %s, weakReference = %s";

    @Test
    public void test1() {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        Object strongReference = a;
        SoftReference<Object> softReference = new SoftReference<>(b);
        WeakReference<Object> weakReference = new WeakReference<>(c);
        a = null;
        b = null;
        c = null;
        System.out.println(String.format(format1, strongReference, softReference.get(), weakReference.get()));
        System.gc();
        System.out.println(String.format(format1, strongReference, softReference.get(), weakReference.get()));
    }

}
