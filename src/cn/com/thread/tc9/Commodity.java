package cn.com.thread.tc9;

import java.util.UUID;

/**
 * @author lilibo
 * @create 2022-01-07 5:40 PM
 */
public class Commodity {

    private String id;

    public Commodity() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "id='" + id + '\'' +
                '}';
    }
}
