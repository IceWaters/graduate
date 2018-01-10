package nest.mdc.routing;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import nest.mdc.field.Field;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.timeclassifier.timeclassifier;

public class Routing {
	public static NodePool nodePool;
	public static int[] visited;// 访问数组，1：已访问；0：未访问
	public static int[] preCapacity;// 记录上一次节点流量上限
	public static int[] curCapacity;// 记录当前平均时间最少的节点流量上限
	private int[] curNodeSequence;
	private double curTime = 10000;// 记录当前的最少平均时间
	public final static int areaLength = 50;// 区域调整时的区域长度
	public final static int radius = 100;
	private timeclassifier timeclassifier1;
	private MaxFlowRouter maxFlowRouter;

	public Routing(NodePool nodePool) {
		Routing.nodePool = nodePool;
		visited = new int[nodePool.getNodeNum()];
		preCapacity = new int[nodePool.getNodeNum()];
		curCapacity = new int[nodePool.getNodeNum()];
		curNodeSequence = new int[nodePool.getNodeNum() - 1];
		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
	}

	/**
	 * 每次全局调整节点流量上限下降一半，然后计算平均时间，查看效果
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlow() throws FileNotFoundException {
		int maxChildrenNum = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// 找出集合中除根节点外的最大子节点数
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity2;
		capacity2 = maxFlowRouter.getNodeCapacity(maxChildrenNum);
		int capacity = (int) Math.sqrt(capacity2[3]);

		while (maxFlowRouter.run()) {
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			capacity--;
			System.out.println(capacity);
			// System.out.println(nodePool.getNodeWithID(0));
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
		}
	}

	/**
	 * 通过调整每个节点的最大能耗，改变图的路由情况，计算得出最后的平均效果
	 * 调整策略为一次取出实际流量最大的节点，根据相应的判断条件，判断是否将该节点的流量上限降半
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfPriority() throws FileNotFoundException {
		PriorityBlockingQueue<Integer> priorQueue = new PriorityBlockingQueue<Integer>(10, new Comparable());
		int maxChildrenNum = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// 找出集合中除根节点外的最大子节点数
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// 初始化访问数组
		for (int i = 0; i < nodePool.getNodeNum(); i++) {
			visited[i] = 0;
		}
		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity;
		capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);

		while (maxFlowRouter.run()) {
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			// 初始化优先队列
			for (int i = 1; i < nodePool.getNodeNum(); i++)
				priorQueue.add(i);

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			// 记录上一次的节点流量上限
			for (int i = 0; i < nodePool.getNodeNum(); i++)
				preCapacity[i] = capacity[i];
			while (!priorQueue.isEmpty()) {
				nodeID = priorQueue.poll();
				// System.out.println(nodePool.getNodeWithID(nodeID).getChildrenNum());
				if (visited[nodeID] == 0 && capacity[nodeID] > 1
						&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
					// System.out.println(nodeID + " " +
					// nodePool.getNodeWithID(nodeID).getChildrenNum());
					capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
				}
				// System.out.println();
				// System.out.println(nodePool.getNodeWithID(nodeID).getChildrenNum());
			}
			// 初始化访问数组
			for (int i = 0; i < nodePool.getNodeNum(); i++) {
				visited[i] = 0;
			}

		}

		// 全局调整流量上限失败后，进行局部调整流量上限
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			capacity[i] = preCapacity[i];
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		maxFlowRouter.setNodeCapacity(capacity);
		System.out.println("ni");
		int nodeID = 1;
		while (maxFlowRouter.run()) {
			System.out.println("bu hao");
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			// 初始化优先队列
			for (int i = 1; i < nodePool.getNodeNum(); i++)
				priorQueue.add(i);

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			nodeID = priorQueue.poll();
			while (visited[nodeID] == 1 || capacity[nodeID] <= 1 || maxFlowRouter.flag == false) {
				if (priorQueue.isEmpty()) {
					// 初始化访问数组
					for (int i = 0; i < nodePool.getNodeNum(); i++) {
						visited[i] = 0;
					}
					nodeID = 1;
					break;
				}
				nodeID = priorQueue.poll();
			}
			capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
		}
	}

	/**
	 * 根据区域设置流量上限
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfArea() throws FileNotFoundException {
		int maxChildrenNum = 0;
		int count = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// 找出集合中除根节点外的最大子节点数
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}
		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity;
		capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);

		while (maxFlowRouter.run()) {
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			// 设置流量上限，区域之间间隔
			for (int i = count % 2; i < Field.iMaxX / areaLength; i += 2) {
				for (int j = count % 2; j < Field.iMaxX / areaLength; j += 2) {
					// System.out.println(i + " " + j);
					capacity = maxFlowRouter.setNodeCapacityByArea(i, j, capacity);
				}
			}
			// 记录上一次的节点流量上限
			for (int i = 0; i < nodePool.getNodeNum(); i++)
				preCapacity[i] = capacity[i];

			count++;
		}

		// 全局调整流量上限失败后，进行局部调整流量上限
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			capacity[i] = preCapacity[i];
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		maxFlowRouter.setNodeCapacity(capacity);
		System.out.println("ni");
		int i = 0;
		int j = 0;
		while (maxFlowRouter.run()) {
			System.out.println("bu hao");
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			// 设置流量上限
			if (j < Field.iMaxY / areaLength) {
				maxFlowRouter.setNodeCapacityByArea(i, j, capacity);
				j += 2;
			} else {
				i += 2;
				if (i < Field.iMaxX / areaLength) {
					j = 0;
				} else {
					i = 0;
					j = 0;
				}
			}
		}
	}

	/**
	 * 每次首先根据优先队列的顺序依次取出节点，调整流量上限，然后随机打乱该序列，对上次记录的流量上限数组，多次调整，记录平均时间最少的调整数组
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfSA() throws FileNotFoundException {
		PriorityBlockingQueue<Integer> priorQueue = new PriorityBlockingQueue<Integer>(10, new Comparable());
		boolean flag = true;
		double time = 0;
		int maxChildrenNum = 0;
		int[] nodeSequence = new int[nodePool.getNodeNum() - 1];
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// 找出集合中除根节点外的最大子节点数
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);// 初始设置每个节点相同的流量上限
		for (int i = 0; i < curCapacity.length; i++) {
			// 初始化
			curCapacity[i] = capacity[i];
			preCapacity[i] = capacity[i];
		}

		while (true) {
			System.out.println("\n\n");
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
			maxFlowRouter.run();
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			int index = 0;

			if (flag) {
				flag = false;
				for (int i = 1; i < nodePool.getNodeNum(); i++) {
					// 初始化优先队列
					priorQueue.add(i);
					// 初始化访问数组
					visited[i] = 0;
				}
				while (!priorQueue.isEmpty()) {
					// 第一次按照实际流量的大小的顺序依次取出，调整流量上限
					nodeID = priorQueue.poll();
					nodeSequence[index++] = nodeID;
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				maxFlowRouter.run();
				countTotalFlow();// 计算总能耗
				time = countAverageTime();// 计算平均时间
				if (curTime > time) {
					// 当平均时间比记录的最少时间少，接受该调整方案
					curTime = time;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
					curNodeSequence = nodeSequence;
				}
			} else {
				changeNodeSeqence();
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// 初始化访问数组
					visited[j] = 0;
				}
				for (int j = 0; j < curNodeSequence.length; j++) {
					nodeID = curNodeSequence[j];
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				maxFlowRouter.run();
				countTotalFlow();// 计算总能耗
				time = countAverageTime();// 计算平均时间
				if (curTime > time) {
					// 当平均时间比记录的最少时间少，接受该调整方案
					curTime = time;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
				}

			}

			for (int i = 0; i < 3; i++) {
				maxFlowRouter = new MaxFlowRouter(nodePool, 0);
				for (int j = 0; j < preCapacity.length; j++) {
					capacity[j] = preCapacity[j];
				}

				changeNodeSeqence();// 改变节点顺序
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// 初始化访问数组
					visited[j] = 0;
				}
				for (int j = 0; j < curNodeSequence.length; j++) {
					nodeID = curNodeSequence[j];
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				maxFlowRouter.run();
				countTotalFlow();// 计算总能耗
				time = countAverageTime();// 计算平均时间
				if (curTime > time) {
					// 当平均时间比记录的最少时间少，接受该调整方案
					curTime = time;
					flag = true;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
				}
			}

			for (int j = 0; j < curCapacity.length; j++) {
				capacity[j] = curCapacity[j];
				preCapacity[j] = curCapacity[j];
			}
		}
	}

	/**
	 * 每次首先根据优先队列的顺序依次取出节点，调整流量上限 然后随机打乱该序列，对上次记录的流量上限数组，多次调整，记录平均时间最少的调整数组
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfSA2() throws FileNotFoundException {
		PriorityBlockingQueue<Integer> priorQueue = new PriorityBlockingQueue<Integer>(10, new Comparable());
		boolean flag = true;
		double time = 0;
		int maxChildrenNum = 0;
		int[] nodeSequence = new int[nodePool.getNodeNum() - 1];
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// 找出集合中除根节点外的最大子节点数
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);// 初始设置每个节点相同的流量上限

		while (maxFlowRouter.run()) {
			countTotalFlow();// 计算总能耗
			time = countAverageTime();// 计算多次平均充电时间
			if (curTime > time) {
				// 当平均时间比记录的最少时间少，接受该调整方案
				curTime = time;
				for (int j = 0; j < nodePool.getNodeNum(); j++) {
					curCapacity[j] = capacity[j];
				}
			} else if (time - curTime > 30)
				break;

			for (int i = 1; i < nodePool.getNodeNum(); i++) {
				// 初始化优先队列
				priorQueue.add(i);
				// 初始化访问数组
				visited[i] = 0;
			}

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			// 记录上一次的节点流量上限
			for (int i = 0; i < nodePool.getNodeNum(); i++)
				preCapacity[i] = capacity[i];
			while (!priorQueue.isEmpty()) {
				nodeID = priorQueue.poll();
				// System.out.println(nodePool.getNodeWithID(nodeID).getChildrenNum());
				if (visited[nodeID] == 0 && capacity[nodeID] > 1
						&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
					// System.out.println(nodeID + " " +
					// nodePool.getNodeWithID(nodeID).getChildrenNum());
					capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
				}
				// System.out.println();
				// System.out.println(nodePool.getNodeWithID(nodeID).getChildrenNum());
			}

		}

		// 下一次调整流量上限，是以最好的结果为基础的
		for (int j = 0; j < nodePool.getNodeNum(); j++) {
			capacity[j] = curCapacity[j];
			preCapacity[j] = curCapacity[j];
		}

		while (true) {
			System.out.println("\n\n");
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
			maxFlowRouter.run();
			countTotalFlow();// 计算总能耗
			countAverageTime();// 计算多次平均充电时间

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			int index = 0;

			if (flag) {
				flag = false;
				for (int i = 1; i < nodePool.getNodeNum(); i++) {
					// 初始化优先队列
					priorQueue.add(i);
					// 初始化访问数组
					visited[i] = 0;
				}
				while (!priorQueue.isEmpty()) {
					// 第一次按照实际流量的大小的顺序依次取出，调整流量上限
					nodeID = priorQueue.poll();
					nodeSequence[index++] = nodeID;
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				if (maxFlowRouter.run()) {
					countTotalFlow();// 计算总能耗
					time = countAverageTime();// 计算平均时间
					if (curTime > time) {
						// 当平均时间比记录的最少时间少，接受该调整方案
						curTime = time;
						for (int j = 0; j < nodePool.getNodeNum(); j++)
							curCapacity[j] = capacity[j];
						curNodeSequence = nodeSequence;
					}
				}
			} else {
				changeNodeSeqence();
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// 初始化访问数组
					visited[j] = 0;
				}
				for (int j = 0; j < curNodeSequence.length; j++) {
					nodeID = curNodeSequence[j];
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				if (maxFlowRouter.run()) {
					countTotalFlow();// 计算总能耗
					time = countAverageTime();// 计算平均时间
					if (curTime > time) {
						// 当平均时间比记录的最少时间少，接受该调整方案
						curTime = time;
						for (int j = 0; j < nodePool.getNodeNum(); j++)
							curCapacity[j] = capacity[j];
					}
				}

			}

			for (int i = 0; i < 3; i++) {
				maxFlowRouter = new MaxFlowRouter(nodePool, 0);
				for (int j = 0; j < preCapacity.length; j++) {
					capacity[j] = preCapacity[j];
				}

				changeNodeSeqence();// 改变节点顺序
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// 初始化访问数组
					visited[j] = 0;
				}
				for (int j = 0; j < curNodeSequence.length; j++) {
					nodeID = curNodeSequence[j];
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				if (!maxFlowRouter.run())
					continue;
				countTotalFlow();// 计算总能耗
				time = countAverageTime();// 计算平均时间
				if (curTime > time) {
					// 当平均时间比记录的最少时间少，接受该调整方案
					curTime = time;
					flag = true;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
				}
			}

			// 下次调整流量上限，在最好的情况上进行调整
			for (int j = 0; j < curCapacity.length; j++) {
				capacity[j] = curCapacity[j];
				preCapacity[j] = curCapacity[j];
			}
		}
	}

	/**
	 * 改变调整节点的顺序
	 */
	private void changeNodeSeqence() {
		int begin = (int) (Math.random() * curNodeSequence.length);
		int end = (int) (Math.random() * curNodeSequence.length);
		int temp;

		if (begin > end) {
			// 如果begin比end大，则交换两个值
			temp = begin;
			begin = end;
			end = temp;
		}

		for (int i = 0; i < ((begin + end) / 2 - begin); i++) {
			temp = curNodeSequence[begin + i];
			curNodeSequence[begin + i] = curNodeSequence[end - i];
			curNodeSequence[end - i] = temp;
		}
	}

	/**
	 * 计算总能耗
	 */
	private void countTotalFlow() {
		int power = 0;
		int[] flow = maxFlowRouter.getFlow();
		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;
			node.setChildrenNum(flow[node.getNodeID()]);
			power = power + node.getChildrenNum();
		}
		System.out.println("总能耗 ： " + power);
	}

	/**
	 * 计算多次的平均充电时间
	 * 
	 * @throws FileNotFoundException
	 */
	private double countAverageTime() throws FileNotFoundException {
		Node node = nodePool.getNodeWithID(0);// 计算充电周期时，除去中心基站
		nodePool.eraseNode(node);
		// System.out.println(nodePool.getNodeWithID(0));
		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
		timeclassifier1.initialOriginalCluster();
		Field.clusterSize = timeclassifier1.getOriginalCluster().size();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Field.drawChargingPeriod(5);// 在图上画出充电时间

		// 计算平均时间
		double averageTime = 0;
		int times = 3;
		for (int i = 0; i < times; i++) {
			System.out.print(i + " ");
			averageTime += timeclassifier1.runAlgXXX();
		}
		System.out.println("\n多次平均时间为 ：" + averageTime / times);
		nodePool.addCenterNode(node);// 计算每个节点的能耗时，需要将中心基站，即根节点加入节点池内

		return averageTime / times;
	}
}

/**
 * 给出优先队列的比较规则，实际流量越大的节点，优先级越高
 * 
 * @author tfl
 * @version 1.0
 */
class Comparable implements Comparator<Object> {
	public int compare(Object arg0, Object arg1) {
		if (Routing.nodePool.getNodeWithID((int) arg0).getChildrenNum() < Routing.nodePool.getNodeWithID((int) arg1)
				.getChildrenNum())
			return 1;
		else if (Routing.nodePool.getNodeWithID((int) arg0).getChildrenNum() == Routing.nodePool
				.getNodeWithID((int) arg1).getChildrenNum()) {
			return 0;
		}
		return -1;
	}
}
