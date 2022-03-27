package cn.com.thread.tb9;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lilibo
 * @create 2022-01-07 10:37 AM
 */
public class CakeFuture {

    private final String orderId;

    private final ReentrantLock myLock = new ReentrantLock();

    private final Condition setCondition = myLock.newCondition();

    private final Condition getCondition = myLock.newCondition();

    private Cake target;

    public CakeFuture() {
        orderId = UUID.randomUUID().toString();
    }

    public void set(Cake cake) throws InterruptedException {
        try{
            myLock.lock();
            if(target != null) {
                setCondition.await();
            }
            target = cake;
            getCondition.signalAll();
        }finally {
            myLock.unlock();
        }
    }

    public Cake get() throws InterruptedException {
        try{
            myLock.lock();
            if(target == null) {
                getCondition.await();
            }
            Cake returnValue = target;
            target = null;
            setCondition.signalAll();
            return returnValue;
        }finally {
            myLock.unlock();
        }
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "CakeFuture{" +
                "orderId='" + orderId + '\'' +
                ", target=" + target +
                '}';
    }
}
