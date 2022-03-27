package cn.com.thread.tc9;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lilibo
 * @create 2022-01-07 7:37 PM
 */
public class PingDuoDuo implements Shop<Commodity>{

    private final BlockingQueue<Commodity> blockingQueue;

    public PingDuoDuo(int capacity) {
        this.capacity = capacity;
        this.blockingQueue = new LinkedBlockingQueue<>(capacity);
    }

    private final int capacity;

    @Override
    public void put(Commodity commodity) throws InterruptedException {
        blockingQueue.put(commodity);
    }

    @Override
    public Commodity take() throws InterruptedException {
        return blockingQueue.take();
    }

    @Override
    public int size() {
        return blockingQueue.size();
    }
}
