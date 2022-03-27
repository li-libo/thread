package cn.com.thread.td5;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author lilibo
 * @create 2022-01-08 7:52 PM
 */
public class TryWithResourceTest {

    @Test
    public void test1() {
        try(Closeable closeable = () -> {
            System.out.println("回调关闭");
        }) {
            System.out.println("增删改查");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("finally");
        }
    }

}

