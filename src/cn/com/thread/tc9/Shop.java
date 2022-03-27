package cn.com.thread.tc9;

/**
 * @author lilibo
 * @create 2022-01-07 5:38 PM
 */
public interface Shop<E> {

    void put(E e) throws InterruptedException;

    E take() throws InterruptedException;

    int size();
}
