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
	public static int[] visited;// �������飬1���ѷ��ʣ�0��δ����
	public static int[] preCapacity;// ��¼��һ�νڵ���������
	public static int[] curCapacity;// ��¼��ǰƽ��ʱ�����ٵĽڵ���������
	private int[] curNodeSequence;
	private double curTime = 10000;// ��¼��ǰ������ƽ��ʱ��
	public final static int areaLength = 50;// �������ʱ�����򳤶�
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
	 * ÿ��ȫ�ֵ����ڵ����������½�һ�룬Ȼ�����ƽ��ʱ�䣬�鿴Ч��
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlow() throws FileNotFoundException {
		int maxChildrenNum = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// �ҳ������г����ڵ��������ӽڵ���
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity2;
		capacity2 = maxFlowRouter.getNodeCapacity(maxChildrenNum);
		int capacity = (int) Math.sqrt(capacity2[3]);

		while (maxFlowRouter.run()) {
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			capacity--;
			System.out.println(capacity);
			// System.out.println(nodePool.getNodeWithID(0));
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
		}
	}

	/**
	 * ͨ������ÿ���ڵ������ܺģ��ı�ͼ��·�����������ó�����ƽ��Ч��
	 * ��������Ϊһ��ȡ��ʵ���������Ľڵ㣬������Ӧ���ж��������ж��Ƿ񽫸ýڵ���������޽���
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfPriority() throws FileNotFoundException {
		PriorityBlockingQueue<Integer> priorQueue = new PriorityBlockingQueue<Integer>(10, new Comparable());
		int maxChildrenNum = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// �ҳ������г����ڵ��������ӽڵ���
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// ��ʼ����������
		for (int i = 0; i < nodePool.getNodeNum(); i++) {
			visited[i] = 0;
		}
		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity;
		capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);

		while (maxFlowRouter.run()) {
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			// ��ʼ�����ȶ���
			for (int i = 1; i < nodePool.getNodeNum(); i++)
				priorQueue.add(i);

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			// ��¼��һ�εĽڵ���������
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
			// ��ʼ����������
			for (int i = 0; i < nodePool.getNodeNum(); i++) {
				visited[i] = 0;
			}

		}

		// ȫ�ֵ�����������ʧ�ܺ󣬽��оֲ�������������
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			capacity[i] = preCapacity[i];
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		maxFlowRouter.setNodeCapacity(capacity);
		System.out.println("ni");
		int nodeID = 1;
		while (maxFlowRouter.run()) {
			System.out.println("bu hao");
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			// ��ʼ�����ȶ���
			for (int i = 1; i < nodePool.getNodeNum(); i++)
				priorQueue.add(i);

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			nodeID = priorQueue.poll();
			while (visited[nodeID] == 1 || capacity[nodeID] <= 1 || maxFlowRouter.flag == false) {
				if (priorQueue.isEmpty()) {
					// ��ʼ����������
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
	 * ��������������������
	 * 
	 * @throws FileNotFoundException
	 */
	public void testFlowOfArea() throws FileNotFoundException {
		int maxChildrenNum = 0;
		int count = 0;
		System.out.println(nodePool.getNodeNum());
		for (int i = 1; i < nodePool.getNodeNum(); i++) {
			// �ҳ������г����ڵ��������ӽڵ���
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}
		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity;
		capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);

		while (maxFlowRouter.run()) {
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			// �����������ޣ�����֮����
			for (int i = count % 2; i < Field.iMaxX / areaLength; i += 2) {
				for (int j = count % 2; j < Field.iMaxX / areaLength; j += 2) {
					// System.out.println(i + " " + j);
					capacity = maxFlowRouter.setNodeCapacityByArea(i, j, capacity);
				}
			}
			// ��¼��һ�εĽڵ���������
			for (int i = 0; i < nodePool.getNodeNum(); i++)
				preCapacity[i] = capacity[i];

			count++;
		}

		// ȫ�ֵ�����������ʧ�ܺ󣬽��оֲ�������������
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			capacity[i] = preCapacity[i];
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		maxFlowRouter.setNodeCapacity(capacity);
		System.out.println("ni");
		int i = 0;
		int j = 0;
		while (maxFlowRouter.run()) {
			System.out.println("bu hao");
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			// ������������
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
	 * ÿ�����ȸ������ȶ��е�˳������ȡ���ڵ㣬�����������ޣ�Ȼ��������Ҹ����У����ϴμ�¼�������������飬��ε�������¼ƽ��ʱ�����ٵĵ�������
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
			// �ҳ������г����ڵ��������ӽڵ���
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);// ��ʼ����ÿ���ڵ���ͬ����������
		for (int i = 0; i < curCapacity.length; i++) {
			// ��ʼ��
			curCapacity[i] = capacity[i];
			preCapacity[i] = capacity[i];
		}

		while (true) {
			System.out.println("\n\n");
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
			maxFlowRouter.run();
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			int index = 0;

			if (flag) {
				flag = false;
				for (int i = 1; i < nodePool.getNodeNum(); i++) {
					// ��ʼ�����ȶ���
					priorQueue.add(i);
					// ��ʼ����������
					visited[i] = 0;
				}
				while (!priorQueue.isEmpty()) {
					// ��һ�ΰ���ʵ�������Ĵ�С��˳������ȡ����������������
					nodeID = priorQueue.poll();
					nodeSequence[index++] = nodeID;
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				maxFlowRouter.run();
				countTotalFlow();// �������ܺ�
				time = countAverageTime();// ����ƽ��ʱ��
				if (curTime > time) {
					// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
					curTime = time;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
					curNodeSequence = nodeSequence;
				}
			} else {
				changeNodeSeqence();
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// ��ʼ����������
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
				countTotalFlow();// �������ܺ�
				time = countAverageTime();// ����ƽ��ʱ��
				if (curTime > time) {
					// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
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

				changeNodeSeqence();// �ı�ڵ�˳��
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// ��ʼ����������
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
				countTotalFlow();// �������ܺ�
				time = countAverageTime();// ����ƽ��ʱ��
				if (curTime > time) {
					// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
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
	 * ÿ�����ȸ������ȶ��е�˳������ȡ���ڵ㣬������������ Ȼ��������Ҹ����У����ϴμ�¼�������������飬��ε�������¼ƽ��ʱ�����ٵĵ�������
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
			// �ҳ������г����ڵ��������ӽڵ���
			if (nodePool.getNodeWithID(i).getChildrenNum() > maxChildrenNum)
				maxChildrenNum = nodePool.getNodeWithID(i).getChildrenNum();
		}

		// System.out.println(nodePool.getNodeNum());
		maxFlowRouter = new MaxFlowRouter(nodePool, 0);
		int[] capacity = maxFlowRouter.getNodeCapacity(maxChildrenNum);// ��ʼ����ÿ���ڵ���ͬ����������

		while (maxFlowRouter.run()) {
			countTotalFlow();// �������ܺ�
			time = countAverageTime();// ������ƽ�����ʱ��
			if (curTime > time) {
				// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
				curTime = time;
				for (int j = 0; j < nodePool.getNodeNum(); j++) {
					curCapacity[j] = capacity[j];
				}
			} else if (time - curTime > 30)
				break;

			for (int i = 1; i < nodePool.getNodeNum(); i++) {
				// ��ʼ�����ȶ���
				priorQueue.add(i);
				// ��ʼ����������
				visited[i] = 0;
			}

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			// ��¼��һ�εĽڵ���������
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

		// ��һ�ε����������ޣ�������õĽ��Ϊ������
		for (int j = 0; j < nodePool.getNodeNum(); j++) {
			capacity[j] = curCapacity[j];
			preCapacity[j] = curCapacity[j];
		}

		while (true) {
			System.out.println("\n\n");
			maxFlowRouter = new MaxFlowRouter(nodePool, 0);
			maxFlowRouter.setNodeCapacity(capacity);
			maxFlowRouter.run();
			countTotalFlow();// �������ܺ�
			countAverageTime();// ������ƽ�����ʱ��

			maxFlowRouter = new MaxFlowRouter(nodePool, 0);

			int nodeID = 1;
			int index = 0;

			if (flag) {
				flag = false;
				for (int i = 1; i < nodePool.getNodeNum(); i++) {
					// ��ʼ�����ȶ���
					priorQueue.add(i);
					// ��ʼ����������
					visited[i] = 0;
				}
				while (!priorQueue.isEmpty()) {
					// ��һ�ΰ���ʵ�������Ĵ�С��˳������ȡ����������������
					nodeID = priorQueue.poll();
					nodeSequence[index++] = nodeID;
					if (visited[nodeID] == 0 && capacity[nodeID] > 1
							&& nodePool.getNodeWithID(nodeID).getChildrenNum() > 1) {
						capacity = maxFlowRouter.setNodeCapacityByNeighbor(nodeID, capacity);
					}
				}

				if (maxFlowRouter.run()) {
					countTotalFlow();// �������ܺ�
					time = countAverageTime();// ����ƽ��ʱ��
					if (curTime > time) {
						// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
						curTime = time;
						for (int j = 0; j < nodePool.getNodeNum(); j++)
							curCapacity[j] = capacity[j];
						curNodeSequence = nodeSequence;
					}
				}
			} else {
				changeNodeSeqence();
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// ��ʼ����������
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
					countTotalFlow();// �������ܺ�
					time = countAverageTime();// ����ƽ��ʱ��
					if (curTime > time) {
						// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
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

				changeNodeSeqence();// �ı�ڵ�˳��
				for (int j = 1; j < nodePool.getNodeNum(); j++) {
					// ��ʼ����������
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
				countTotalFlow();// �������ܺ�
				time = countAverageTime();// ����ƽ��ʱ��
				if (curTime > time) {
					// ��ƽ��ʱ��ȼ�¼������ʱ���٣����ܸõ�������
					curTime = time;
					flag = true;
					for (int j = 0; j < nodePool.getNodeNum(); j++)
						curCapacity[j] = capacity[j];
				}
			}

			// �´ε����������ޣ�����õ�����Ͻ��е���
			for (int j = 0; j < curCapacity.length; j++) {
				capacity[j] = curCapacity[j];
				preCapacity[j] = curCapacity[j];
			}
		}
	}

	/**
	 * �ı�����ڵ��˳��
	 */
	private void changeNodeSeqence() {
		int begin = (int) (Math.random() * curNodeSequence.length);
		int end = (int) (Math.random() * curNodeSequence.length);
		int temp;

		if (begin > end) {
			// ���begin��end���򽻻�����ֵ
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
	 * �������ܺ�
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
		System.out.println("���ܺ� �� " + power);
	}

	/**
	 * �����ε�ƽ�����ʱ��
	 * 
	 * @throws FileNotFoundException
	 */
	private double countAverageTime() throws FileNotFoundException {
		Node node = nodePool.getNodeWithID(0);// ����������ʱ����ȥ���Ļ�վ
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
		Field.drawChargingPeriod(5);// ��ͼ�ϻ������ʱ��

		// ����ƽ��ʱ��
		double averageTime = 0;
		int times = 3;
		for (int i = 0; i < times; i++) {
			System.out.print(i + " ");
			averageTime += timeclassifier1.runAlgXXX();
		}
		System.out.println("\n���ƽ��ʱ��Ϊ ��" + averageTime / times);
		nodePool.addCenterNode(node);// ����ÿ���ڵ���ܺ�ʱ����Ҫ�����Ļ�վ�������ڵ����ڵ����

		return averageTime / times;
	}
}

/**
 * �������ȶ��еıȽϹ���ʵ������Խ��Ľڵ㣬���ȼ�Խ��
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
