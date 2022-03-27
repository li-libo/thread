package cn.com.thread.tc9;

/**
 * @author lilibo
 * @create 2022-01-07 5:39 PM
 */
public class Tmall implements Shop<Commodity>{

    private final int capacity;

    private int size;

    private final Commodity[] array;

    private int putIndex;

    private int takeIndex;

    public Tmall(int capacity) {
        this.capacity = capacity;
        array = new Commodity[capacity];
    }

    @Override
    public synchronized void put(Commodity commodity) throws InterruptedException {
        while (size >= capacity) {
            this.wait();
        }
        array[putIndex] = commodity;
        putIndex++;
        size++;
        if(putIndex == capacity) {
            putIndex = 0;
        }
        this.notifyAll();
    }

    @Override
    public synchronized Commodity take() throws InterruptedException {
        while (size <= 0) {
            this.wait();
        }
        Commodity returnValue = array[takeIndex];
        array[takeIndex] = null;
        takeIndex++;
        size--;
        if(takeIndex == capacity) {
            takeIndex = 0;
        }
        this.notifyAll();
        return returnValue;
    }

    @Override
    public int size() {
        return size;
    }
}
