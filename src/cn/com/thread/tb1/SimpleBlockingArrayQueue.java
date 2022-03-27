package cn.com.thread.tb1;

import org.omg.CORBA.TIMEOUT;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-05 5:15 PM
 */
public class SimpleBlockingArrayQueue<E> {

    private final ReentrantLock myLock = new ReentrantLock();

    private final Condition putCondition = myLock.newCondition();

    private final Condition takeCondition = myLock.newCondition();

    private final int capacity;

    private final Object[] array;

    private int size;

    private int putIndex;

    private int takeIndex;

    public static final String format1 = "%s, the currentThread name = %s";

    public SimpleBlockingArrayQueue(int capacity) {
        this.capacity = capacity;
        array = new Object[capacity];
    }

    public void put(E e) {
        try{
            myLock.lock();
            while (size >= capacity) {
                try {
                    // System.out.println(String.format(format1, "队列已满", Thread.currentThread().getName()));
                    putCondition.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            array[putIndex] = e;
            putIndex++;
            size++;
            if(putIndex == capacity) {
                putIndex = 0;
            }
            takeCondition.signalAll();
        }finally {
            myLock.unlock();
        }
    }

    public <E> E take() {
        try{
            myLock.lock();
            while (size <= 0) {
                try {
                    System.out.println(String.format(format1, "队列已空", Thread.currentThread().getName()));
                    takeCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            E returnValue = (E) array[takeIndex];
            array[takeIndex] = null;
            takeIndex++;
            size--;
            if(takeIndex == capacity) {
                takeIndex = 0;
            }
            putCondition.signalAll();
            return returnValue;
        }finally {
            myLock.unlock();
        }
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        SimpleBlockingArrayQueue<Object> blockingArrayQueue = new SimpleBlockingArrayQueue<>(10);
        int numOfPutThread = 3;
        Stream.iterate(0, count -> count + 1).limit(numOfPutThread).forEach(count -> {
            new Thread(()->{
                while (true) {
                    blockingArrayQueue.put(UUID.randomUUID().toString());
                }
            }, "put-"+ count).start();
        });

        Set<String> resultSet = Collections.synchronizedSet(new HashSet<>());
        int numOfTakeThread = 3;
        Stream.iterate(0, count -> count + 1).limit(numOfTakeThread).forEach(count -> {
            new Thread(() -> {
                while (true) {
                    String takeResult = blockingArrayQueue.take();
                    if(!resultSet.add(takeResult)) {
                        throw new RuntimeException("出现重复数据! data = " + takeResult);
                    }
                }
            }, "take-" + count).start();
        });

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    System.out.println("queue size = " + blockingArrayQueue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "size").start();
    }
}
