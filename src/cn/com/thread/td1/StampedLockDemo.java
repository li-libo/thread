package cn.com.thread.td1;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * 前面介绍的ReadWriteLock可以解决多线程同时读，但只有一个线程能写的问题。
 * <p>
 * 如果我们深入分析ReadWriteLock，会发现它有个潜在的问题：如果有线程正在读，写线程需要等待读线程释放锁后才能获取写锁，
 * 即读的过程中不允许写，这是一种悲观的读锁。
 * <p>
 * 要进一步提升并发执行效率，Java 8引入了新的读写锁：StampedLock。
 * <p>
 * StampedLock和ReadWriteLock相比，改进之处在于：
 * 读的过程中也允许获取写锁后写入！这样一来，我们读的数据就可能不一致，所以，需要一点额外的代码来判断读的过程中是否有写入，这种读锁是一种乐观锁。
 * 乐观锁的意思就是乐观地估计读的过程中大概率不会有写入，因此被称为乐观锁。反过来，悲观锁则是读的过程中拒绝有写入，也就是写入必须等待。显然乐观
 * 锁的并发效率更高，但一旦有小概率的写入导致读取的数据不一致，需要能检测出来，再读一遍就行。
 * <p>
 * 和ReadWriteLock相比，写入的加锁是完全一样的，不同的是读取。注意到首先我们通过tryOptimisticRead()获取一个乐观读锁，并返回版本号。接着进行读取，
 * 读取完成后，我们通过validate()去验证版本号，如果在读取过程中没有写入，版本号不变，验证成功，我们就可以放心地继续后续操作。如果在读取过程中有写入，
 * 版本号会发生变化，验证将失败。在失败的时候，我们再通过获取悲观读锁再次读取。由于写入的概率不高，程序在绝大部分情况下可以通过乐观读锁获取数据，极少数
 * 情况下使用悲观读锁获取数据。
 * <p>
 * 可见，StampedLock把读锁细分为乐观读和悲观读，能进一步提升并发效率。但这也是有代价的：
 * 一是代码更加复杂，二是StampedLock是不可重入锁，不能在一个线程中反复获取同一个锁。
 * <p>
 * StampedLock还提供了更复杂的将悲观读锁升级为写锁的功能，它主要使用在if-then-update的场景：即先读，如果读的数据满足条件，就返回，如果读的数据不满足条件，
 * 再尝试写。
 * <p>
 * 小结:
 * StampedLock提供了乐观读锁，可取代ReadWriteLock以进一步提升并发性能；
 * StampedLock是不可重入锁。
 *
 * @author lilibo
 */
public class StampedLockDemo {

    private static Random random = new Random();

    private static StampedLock stampedLock = new StampedLock();

    private static int count;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            //启动 5个线程写计数,95 个线程读计数,
            if (i % 20 == 0) {
                new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(5));
                        System.out.println("threadName = " + Thread.currentThread().getName() + "新增1, count = " + add());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "add-" + i).start();
            } else {
                new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(5));
                        System.out.println("threadName = " + Thread.currentThread().getName() + "读取, count = " + get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, "get-" + i).start();
            }
        }
    }

    private static int get() {
        long stamp = stampedLock.tryOptimisticRead();
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                return count;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return count;
    }

    private static int add() {
        long stamp = stampedLock.writeLock();
        try {
            count++;
            return count;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

}
