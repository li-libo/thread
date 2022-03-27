package cn.com.thread.tb5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 利用CountDownLatch计算文件总和
 * 
 * CountDownLatch和CyclicBarrier的功能看起来很相似，不易区分，有一种谜之的神秘。本文将通过通俗的例子并结合代码讲解两者的使用方法和区别。
 * 
 * CountDownLatch和CyclicBarrier都是java.util.concurrent包下面的多线程工具类。
 * 
 * 从字面上理解，CountDown表示减法计数，Latch表示门闩的意思，计数为0的时候就可以打开门闩了。Cyclic
 * Barrier表示循环的障碍物。两个类都含有这一个意思：对应的线程都完成工作之后再进行下一步动作，也就是大家都准备好之后再进行下一步。然而两者最大的区别是，
 * 进行下一步动作的动作实施者是不一样的。这里的“动作实施者”有两种，一种是主线程（即执行main函数），另一种是执行任务的其他线程，后面叫这种线程为“其他线程”，
 * 区分于主线程。对于CountDownLatch，当计数为0的时候，下一步的动作实施者是main函数；对于CyclicBarrier，下一步动作实施者是“其他线程”。
 * 
 * 备注:
 * 这里我们使用主线程（即main函数）来创建CountDown和CyclicBarrier对象。所以上文将线程分为“主线程”和“其它线程”两类，主要是便于大家理解。
 * 需要提醒读者的是，不单单是主线程可以创建该对象，其它当前正在运行的线程也可以创建CountDown和CyclicBarrier对象，此时该current thread扮演了“主线程”的角色。
 * 因此，更加准确的方式是将线程分为“当前线程”和“其它线程”，前者表示创建CountDown和CyclicBarrier对象的线程。希望这个备注没有给
 * 大家带来更大的困惑。
 * 
 * @author lilibo
 *
 */
public class Demo2 {

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		List<String> lineList = readFile("/Users/lilibo/eclipse-workspace/thread/src/cn/com/thread/tb4/nums.txt");
		int threadNum = lineList.size();
		int[] resultArray = new int[threadNum];
		CountDownLatch countDownLatch = new CountDownLatch(threadNum);
		// map-分开计算
		for (int i = 0; i < threadNum; i++) {
			final int j = i;
			new Thread(() -> {
				String line = lineList.get(j);
				String[] strArray = line.split(",");
				int sum = 0;
				for (String str : strArray) {
					sum += Integer.parseInt(str);
				}
				System.out.println("threadName = " + Thread.currentThread().getName() + " 处理第" + j + "行"
						+ Arrays.toString(strArray) + "相加结果 = " + sum);
				resultArray[j] = sum;
				countDownLatch.countDown();
			}).start();
		}
		countDownLatch.await();
		// reduce-规约集合
		int sum = 0;
		for (int result : resultArray) {
			sum += result;
		}
		System.out.println("文件计算总和 = " + sum);
	}

	private static List<String> readFile(String fileName) throws FileNotFoundException, IOException {
		List<String> lineList = new ArrayList<>();
		String line = null;
		try (BufferedReader bufferReader = new BufferedReader(new FileReader(fileName))) {
			while ((line = bufferReader.readLine()) != null) {
				lineList.add(line);
			}
		}
		return lineList;
	}

}
