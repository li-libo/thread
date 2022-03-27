package cn.com.thread.td2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyDataSourceDemo {

	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

	private static final String USER = "root";

	private static final String PASSWORD = "24di6785";

	private static final String URL = "jdbc:mysql://localhost/abpm?autoReconnect=true&nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&tcpRcvBuf=1024000";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Map<String, String> properties = new HashMap<>();
		properties.put(MyDataSource.JDBCURL, URL);
		properties.put(MyDataSource.USER, USER);
		properties.put(MyDataSource.PASSWORD, PASSWORD);
		properties.put(MyDataSource.DRIVERCLASS, DRIVER_CLASS);
		Random random = new Random();
		MyDataSource myDataSource = new MyDataSource(10, properties);
		for(int i = 0; i< 8; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					try {
						Connection connection = myDataSource.getConnection();
						System.out.println("connection = " + connection);
						TimeUnit.SECONDS.sleep(random.nextInt(3));
						myDataSource.releaseConnection(connection);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}				
			}, "test-" + j).start();
		}
		
		new Thread(()->{
			while(true) {
				try {
					TimeUnit.SECONDS.sleep(1);
					System.out.println("size = " + myDataSource.size());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "size").start();
	}

}

class MyDataSource{
	
	private final LinkedList<Connection> pool = new LinkedList<>();
	
	@SuppressWarnings("unused")
	private final Map<String, String> properties;
	
	private final int capacity;
	
	public static final String JDBCURL = "jdbcUrl";
	
	public static final String USER = "user";
	
	public static final String PASSWORD = "password";
	
	public static final String DRIVERCLASS = "driverClass";
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final Condition emptyCondition = lock.newCondition();
	
	public MyDataSource(int capacity, Map<String, String> properties) throws SQLException, ClassNotFoundException {
		this.capacity = capacity;
		this.properties = properties;
		Class.forName(properties.get(DRIVERCLASS));
		for(int i = 0; i< capacity; i++) {
			Connection connection = DriverManager.getConnection(properties.get(JDBCURL), properties.get(USER), properties.get(PASSWORD));
			pool.addLast(connection);
		}
	}
	
	public Connection getConnection() throws InterruptedException {
		try {
			lock.lock();
			while(pool.isEmpty()) {
				emptyCondition.await();
			}
			Connection connection = pool.removeFirst();
			return connection;
		}finally {
			lock.unlock();
		}
	}
	
	public void releaseConnection(Connection connection) {
		try {
			lock.lock();
			pool.addLast(connection);
			emptyCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
	public int size() {
		return pool.size();
	}

	@Override
	public String toString() {
		return "MyDataSource [capacity=" + capacity + ", size = " + pool.size() + "]";
	}
	
}