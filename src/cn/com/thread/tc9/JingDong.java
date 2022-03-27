package cn.com.thread.tc9;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lilibo
 * @create 2022-01-07 7:01 PM
 */
public class JingDong implements Shop<Commodity>{

    private final ReentrantLock myLock = new ReentrantLock();

    private final Condition putCondition = myLock.newCondition();

    private final Condition takeCondition = myLock.newCondition();

    private int size;

    private int putIndex;

    private int takeIndex;

    private final int capacity;

    private Object[] array;

    public JingDong(int capacity) {
        this.capacity = capacity;
        array = new Object[capacity];
    }

    @Override
    public void put(Commodity commodity) throws InterruptedException {
        try{
            myLock.lock();
            while (size >= capacity) {
                putCondition.await();
            }
            array[putIndex] = commodity;
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

    @Override
    public Commodity take() throws InterruptedException {
        try {
            myLock.lock();
            while (size <= 0) {
                takeCondition.await();
            }
            Commodity returnValue = (Commodity) array[takeIndex];
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

    @Override
    public int size() {
        return size;
    }

}
