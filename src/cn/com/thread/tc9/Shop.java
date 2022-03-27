package cn.com.thread.tc9;

public interface Shop<E> {
	
	void put(E e) throws Exception;
	
	E take() throws Exception;
	
	int size() throws Exception;

}
