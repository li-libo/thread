package cn.com.thread.tb3;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * 强引用-软引用-弱引用
 * 强引用: 普通的引用,强引用指向的对象不会被回收
 * 软引用: 仅有软引用指向的对象,只有发生gc且内存不足时,才会被回收
 * 弱引用: 仅有弱引用指向的对象,只要发生gc就会被回收
 * @author lilibo
 */
public class ReferenceTestDemo {
	
	public static void main(String[] args) {
		Object a = new Object();
		Object b = new Object();
		Object c = new Object();
		
		Object strongReference = a;
		SoftReference<Object> softReference = new SoftReference<>(b);
		WeakReference<Object> weakReference = new WeakReference<>(c);
		a = null;
		b = null;
		c = null;
		String format = "strongRef = %s, softRef = %s, weakRef = %s";
		System.out.println("gc回收前: " + String.format(format, strongReference, softReference.get(), weakReference.get()));
		System.gc();
		System.out.println("gc回收后: " + String.format(format, strongReference, softReference.get(), weakReference.get()));
	}

}
