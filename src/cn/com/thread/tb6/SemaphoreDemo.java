package cn.com.thread.tb6;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 使用Semaphore控制并发度
 * @author lilibo
 * @create 2022-01-06 9:25 PM
 */
public class SemaphoreDemo {

    public static final String format1 = "numOfTestThread = %s, ConnectionCount = %s, semaphore permits = %s, semaphore.availablePermits = %s";

    @Test
    public void test1() {
        int numOfTestThread = 7;
        int permits = 10;
        ConnectionManager connectionManager = new ConnectionManager(permits);
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(() -> {
                while (true) {
                    try(MiConnection connection = connectionManager.getConnection()) {
                        // 模拟增删改查操作
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "getConnection-" + count).start();
        });

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(String.format(format1, numOfTestThread, ConnectionManager.getConnectionCount(), permits, connectionManager.getAvailablePermits()));
            }
        }, "getConnectionCount").start();

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }

}

class ConnectionManager {

    private static AtomicInteger count = new AtomicInteger(0);

    private final int permits;

    private final Semaphore semaphore;

    public ConnectionManager(int permits) {
        this.permits = permits;
        semaphore = new Semaphore(permits);
    }

    public MiConnection getConnection() throws InterruptedException {
        return new MiConnection(semaphore);
    }

    public static int getConnectionCount() {
        return MiConnection.getConnectionCount();
    }

    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }
}

class MiConnection implements Closeable {

    private final String id;

    private final Semaphore semaphore;

    private static final AtomicInteger connectionCount = new AtomicInteger();

    public MiConnection(Semaphore semaphore) throws InterruptedException {
        id = UUID.randomUUID().toString();
        this.semaphore = semaphore;
        semaphore.acquire();
        connectionCount.incrementAndGet();
        System.out.println("创建Connection, id = " + id);
    }

    @Override
    public void close() throws IOException {
        semaphore.release();
        connectionCount.decrementAndGet();
        System.out.println("关闭Connection, id = " + id);
    }

    public static int getConnectionCount() {
        return connectionCount.get();
    }

    @Override
    public String toString() {
        return "MiConnection{" +
                "id='" + id + '\'' +
                '}';
    }
}