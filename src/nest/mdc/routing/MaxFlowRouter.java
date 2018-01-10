package nest.mdc.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

public class MaxFlowRouter {
	private final int maxCost = 10000; // ��ʾ��Ӧ�ı߲���ͨ��
	private int maxFlow; // �������Ҳ�����������������ԭ����Ľڵ���������
	private NodePool nodePool; // ԭ�ڵ��
	private int newNodeNum; // ת��֮��������˽ṹ�ĵ�ĸ���
	private int souNode; // ��ͼ�е�Դ�ڵ�
	private int desNode; // ��ͼ�еĻ�۽ڵ�
	private int[][] costMatrix; // ��ͼ����ͨ�Ծ���
	private Map<Integer, Integer> oldToNewMap; // ԭͼ��nodeID����ͼ�нڵ�ID��ӳ��
	private Map<Integer, Set<Integer>> neigMap; // ��ͼ�еĽڵ��ھ�ӳ��
	private Set<Integer> availableSet; // ·�ɹ����е�ʣ���ͨ�нڵ�
	private Map<Integer, Integer> newToOldMap; // ��ͼ�ڵ�ID��ԭͼnodeID��ӳ��
	public int[] nodeCapacity; // ��ͼ�и����ڵ������
	public int[] flow; // ��ͼ�и����ڵ��ʵ������
	public int[] oldFlow;
	private int[][] flowAlloc; // ��ͼ�и����ڵ���������������flowAlloc[i][j]��ʾ�ڵ�i����ڵ�j������
	public boolean flag;

	/**
	 * ���캯�������ڳ�ʼ��
	 * 
	 * @param nodePool
	 *            ԭͼ�Ľڵ��
	 * @param rootNodeID
	 *            ԭͼ�ĸ��ڵ�
	 */
	public MaxFlowRouter(NodePool nodePool, int rootNodeID) {
		// TODO Auto-generated constructor stub
		this.nodePool = nodePool;
		maxFlow = nodePool.nodePoolWithID.size();
		newNodeNum = nodePool.nodePoolWithID.size() * 2 + 1;
		souNode = newNodeNum - 1;
		desNode = rootNodeID;
		costMatrix = new int[newNodeNum][newNodeNum];
		oldToNewMap = new HashMap<>();
		neigMap = new HashMap<>();
		availableSet = new HashSet<>();
		newToOldMap = new HashMap<>();
		nodeCapacity = new int[newNodeNum];
		oldFlow = new int[nodePool.nodePoolWithID.size()];
		flow = new int[newNodeNum];
		flowAlloc = new int[newNodeNum][newNodeNum];
		flag = false;
		initGraph();
		// for (int i : oldToNewMap.keySet()) {
		// if (i % 50 != 0) {
		// continue;
		// }
		// for (int j : oldToNewMap.keySet()) {
		// if (costMatrix[oldToNewMap.get(i)][oldToNewMap.get(j)] < maxCost) {
		// Field.display.drawLine(nodePool.getNodeWithID(i),
		// nodePool.getNodeWithID(j), 1, Color.red);
		// }
		// }
		// }
		//
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public boolean run() {
		for (int i = 0; i < newNodeNum; i++) {
			availableSet.add(i);
		}
		int count = maxFlow;
		Dijkstra dijkstra = new Dijkstra(newNodeNum, neigMap);
		while (count > 0) {
			if (!dijkstra.findShortestPathAtoB(souNode, desNode, availableSet, costMatrix)) {
				return false;
			}
			int hop = -1;
			int[] path = dijkstra.getPathToDes(desNode);
			for (int i = 1; i < path.length; i++) {
				if (path[i] < nodePool.getNodeNum()) {
					hop++;
				}
			}
			nodePool.getNodeWithID(newToOldMap.get(path[2])).setHopNum(hop);
			// System.out.println(path[2] + " : " + hop);

			// for (int i = 0; i < path.length - 1; i++) {
			// if (path[i] < nodePool.getNodeNum()) {
			// // int j = 1;
			// // while (path[i + j] >= nodePool.getNodeNum())
			// // j++;
			// Field.display.drawLine(nodePool.getNodeWithID(newToOldMap.get(path[i])),
			// nodePool.getNodeWithID(newToOldMap.get(path[i + 1])), 1,
			// Color.red);
			// }
			// }
			//
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// for (int i = 0; i < path.length - 1; i++) {
			// if (path[i] < nodePool.getNodeNum()) {
			// // int j = 1;
			// // while (path[i + j] >= nodePool.getNodeNum())
			// // j++;
			// Field.display.drawLine(nodePool.getNodeWithID(newToOldMap.get(path[i])),
			// nodePool.getNodeWithID(newToOldMap.get(path[i + 1])), 1,
			// Color.black);
			// }
			// }

			int tmp = desNode, next;
			while (tmp != souNode) {
				flow[tmp]++;
				if (flow[tmp] >= nodeCapacity[tmp]) {
					availableSet.remove(tmp);
				}
				next = tmp;
				tmp = dijkstra.prevArray[tmp];
				flowAlloc[tmp][next]++;
			}
			count--;
		}
		return true;
	}

	/**
	 * �õ�ԭʼ���ŵ��ܺ�
	 * 
	 * @return oldFlow �� ��Ӧԭʼ��ŵ��ܺ�
	 */
	public int[] getFlow() {

		for (int i = 0; i < nodePool.getNodeSet().size(); i++) {
			oldFlow[newToOldMap.get(i)] = flow[i];
		}

		return oldFlow;
	}

	/**
	 * ��ʼ����ͼ����ԭͼ�õ���ͼ
	 */
	private void initGraph() {
		int i = 0;
		for (int nodeID : nodePool.nodePoolWithID.keySet()) {
			// Ϊÿ��ԭͼ�еĽڵ㽨����Ӧ����ͼ�ڵ�
			oldToNewMap.put(nodeID, i);
			newToOldMap.put(i++, nodeID);
		}
		desNode = oldToNewMap.get(desNode); // ��ԭͼ�ĸ��ڵ��Ӧ����ͼ�Ļ�۽ڵ�

		for (int oldNodeID : nodePool.nodePoolWithID.keySet()) {
			// ����ԭͼ�����˽ṹ������ͼ���ڽӹ�ϵ
			int newNodeID = oldToNewMap.get(oldNodeID);
			if (!neigMap.containsKey(newNodeID)) {
				neigMap.put(newNodeID, new HashSet<Integer>());
			}
			Set<Node> neigSet = nodePool.getNodeWithID(oldNodeID).neighbors.get(1).getNeig();
			for (Node currNeig : neigSet) {
				neigMap.get(newNodeID).add(oldToNewMap.get(nodePool.getNodeID(currNeig)));
			}
		}

		neigMap.put(souNode, new HashSet<Integer>()); // ������ͼ��Դ�ڵ�
		for (int j = i; j < newNodeNum; j++) {
			// Ϊÿһ��ԭͼ�еĽڵ㽨��һ���µİ���ڵ㣬������Ͷ�Ӧ��ԭͼ�ڵ���������
			if (!neigMap.containsKey(j)) {
				neigMap.put(j, new HashSet<Integer>());
			}
			// ͬʱ��ÿ������ڵ㶼����ͼ�е�Դ�ڵ���������
			neigMap.get(j).add(j - i);
			neigMap.get(souNode).add(j);
		}

		for (int j = 0; j < costMatrix.length; j++) {
			// ��ʼ���ڽӾ���
			for (int j2 = 0; j2 < costMatrix.length; j2++) {
				costMatrix[j][j2] = maxCost;
			}
		}

		for (int j = 0; j < newNodeNum; j++) {
			// �����ڽ�ӳ�������ڽӾ���
			for (int neigID : neigMap.get(j)) {
				if (newToOldMap.containsKey(j) && newToOldMap.containsKey(neigID)) {
					// ����·��Ȩ��
					costMatrix[j][neigID] = /*
											 * (int) nodePool.getNodeWithID(
											 * newToOldMap.get(j))
											 * .getDistance(nodePool.
											 * getNodeWithID(newToOldMap.get(
											 * neigID))) +
											 */1;
				} else {
					costMatrix[j][neigID] = 1;
				}
			}
		}
	}

	/**
	 * ��ȡ�����ڵ���������� ,Դ�ڵ��Ŀ�Ľڵ��������������ԭ����Ĵδ��ӽڵ���������
	 * 
	 * @param maxNodeNum
	 *            - ����Dijkstra�󣬵õ��ĳ����ڵ�֮�������ӽڵ���
	 */
	public int[] getNodeCapacity(int maxNodeNum) {
		int index = 0;
		for (int i = 0; i < 20; i++) {
			if (Math.pow(2, i) <= maxNodeNum && maxNodeNum < Math.pow(2, i + 1)) {
				index = i;
				break;
			}
		}
		// System.out.println(nodeCapacity.length);
		int capacity = (int) Math.pow(2, index);
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacity;

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;

		return nodeCapacity;
	}

	/**
	 * ���ø����ڵ����������
	 * 
	 * @param index
	 *            - 2��index�η�Ϊÿ���ڵ����������
	 */
	public void setNodeCapacity(int index) {

		int capacity = (int) Math.pow(2, index);
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			nodeCapacity[i] = capacity;

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeNum(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * ���ø����ڵ����������
	 * 
	 * @param capacity
	 *            - ÿ���ڵ����������
	 */
	public void setNodeCapacity(int[] capacity) {

		for (int i = 0; i < nodePool.getNodeNum(); i++)
			nodeCapacity[i] = capacity[i];

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeNum(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * �����ھӵ�����ƽ���������ø����ڵ����������
	 * 
	 * @param index
	 *            - 2��index�η�Ϊÿ���ڵ����������
	 */
	public void setNodeCapacityByNeigFlow(int hop) {

		for (int i = 0; i < nodePool.getNodeSet().size(); i++) {
			Node node = nodePool.getNodeWithID(i);
			int average = 0;
			int neighborNum = 0;
			int load = 0;
			for (int j = 1; j <= hop; j++) {
				@SuppressWarnings("unchecked")
				Set<Node> neighbors = (Set<Node>) node.neighbors.get(j).getNeig();
				for (Node node2 : neighbors) {
					neighborNum++;
					load += node2.getChildrenNum() + 1;
				}
			}
			if (neighborNum != 0)
				average = (int) (load / neighborNum);
			int index = 0;
			for (int k = 0; k < 20; k++) {
				if (Math.pow(2, k) <= average && average < Math.pow(2, k + 1)) {
					index = k;
					break;
				}
			}

			nodeCapacity[oldToNewMap.get(i)] = (int) Math.pow(2, index);
		}

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * �ҳ��ھ��е����ڵ�������ͬ�Ľڵ㣬�жϸ����ڵ�����������ǲ������ģ�����ǣ������޳���2�����򣬲���
	 * 
	 * @param nodeID
	 *            - �����ڵ��ID
	 * @param capacitys
	 *            - �洢��һ���������޵�����
	 * @return
	 */
	public int[] setNodeCapacityByNeighbor(int nodeID, int[] capacitys) {
		Node node = nodePool.getNodeWithID(nodeID);
		nodeID = oldToNewMap.get(nodeID);
		flag = false;
		nodeCapacity = new int[newNodeNum];

		int hop = node.getHopNum();
		int flowSum = 0; // ��¼���������ʵ������֮��
		int capacitySum = 0; // ��¼���������ڵ���������֮��
		Routing.visited[nodeID] = 1;
		Set<Node> nodes = new HashSet<>();// �洢�ھ���������ͬ�Ľڵ�
		Set<Node> neighbors = (Set<Node>) node.neighbors.get(1).getNeig();
		// System.out.println("hop : " + hop);
		flowSum += node.getChildrenNum();
		for (Node node2 : neighbors) {
			if (Routing.visited[node2.getNodeID()] == 0 && node2.getNodeID() != 0) {

				if (node2.getHopNum() == hop) {
					nodes.add(node2);
					flowSum += node2.getChildrenNum();
					capacitySum += capacitys[oldToNewMap.get(node2.getNodeID())];
				}
			}
		}

		// ����ÿ���ڵ�Ľڵ�����
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		nodeCapacity[nodeID] = capacitys[nodeID] / 2;
		// �ж��Ƿ�ı�����Ľڵ�����
		for (Node node2 : nodes) {
			if (node2.getNodeID() == 0)
				continue;
			// if (node2.getChildrenNum() > node.getChildrenNum()) {
			if (capacitys[oldToNewMap.get(node2.getNodeID())] > capacitys[nodeID]
					&& node2.getChildrenNum() > node.getChildrenNum()) {
				nodeCapacity[nodeID] = capacitys[nodeID];
				break;
			}
		}

		if (nodeCapacity[nodeID] != capacitys[nodeID]) {
			if ((nodeCapacity[nodeID] + capacitySum) < flowSum && nodes.size() > 0) {
				nodeCapacity[nodeID] = capacitys[nodeID];
			} else {
				flag = true;
				for (Node node2 : neighbors)
					Routing.visited[node2.getNodeID()] = 1;
			}
		}

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
		// System.out.println(nodeID + " : " + nodeCapacity[nodeID] + " " +
		// capacitys[nodeID]);
		// System.out.print(" " + nodeCapacity[nodeID] + "\n");
		return nodeCapacity;
	}

	/**
	 * ����ͬһ�������ڵĽڵ����������ͬʱ���ͣ���������Ϊ����
	 * 
	 * @param nodeID
	 *            - �����ڵ��ID
	 * @param capacitys
	 *            - �洢��һ���������޵�����
	 * @return
	 */
	public int[] setNodeCapacityByArea(int x, int y, int[] capacitys) {
		flag = false;
		nodeCapacity = new int[newNodeNum];
		Set<Node> nodes = new HashSet<>();// �洢������Ľڵ�

		// ����ÿ���ڵ�Ľڵ�����
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		// �ҳ�������ĵ�
		for (Node node : nodePool.getNodeSet()) {
			if (node.getXCoordinate() >= Routing.areaLength * x && node.getXCoordinate() < (x + 1) * Routing.areaLength
					&& node.getYCoordinate() >= Routing.areaLength * y
					&& node.getYCoordinate() < (y + 1) * Routing.areaLength)
				nodes.add(node);
		}

		// �ж��Ƿ�ı�����Ľڵ�����
		for (Node node : nodes) {
			if (node.getNodeID() == 0)
				continue;
			// System.out.print(node.getNodeID() + " ");
			// if(node2.getChildrenNum() > node.getChildrenNum()){
			nodeCapacity[oldToNewMap.get(node.getNodeID())] = capacitys[oldToNewMap.get(node.getNodeID())] / 2;
		}
		// System.out.println();

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
		// System.out.println(nodeID + " : " + nodeCapacity[nodeID] + " " +
		// capacitys[nodeID]);
		return nodeCapacity;
	}

	/**
	 * ����ͬһ�������ڵĽڵ����������ͬʱ���ͣ���������Ϊ����
	 * 
	 * @param nodeID
	 *            - �����ڵ��ID
	 * @param capacitys
	 *            - �洢��һ���������޵�����
	 * @return nodeCapacity - ���������������
	 */
	public int[] setNodeCapacityByCircleArea(int k, int[] capacitys) {
		flag = false;
		nodeCapacity = new int[newNodeNum];
		Set<Node> nodes = new HashSet<>();// �洢������Ľڵ�
		double x = nodePool.getNodeWithID(0).getXCoordinate();
		double y = nodePool.getNodeWithID(0).getYCoordinate();

		// ����ÿ���ڵ�Ľڵ�����
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		// �ҳ�������ĵ�
		for (Node node : nodePool.getNodeSet()) {
			if (Math.sqrt(Math.abs(node.getXCoordinate() - x) + Math.abs(node.getYCoordinate() - y)) <= k
					* Routing.radius
					&& Math.sqrt(Math.abs(node.getXCoordinate() - x) + Math.abs(node.getYCoordinate() - y)) > (k - 1)
							* Routing.radius)
				nodes.add(node);
		}

		// �ж��Ƿ�ı�����Ľڵ�����
		for (Node node : nodes) {
			if (node.getNodeID() == 0)
				continue;
			// if(node2.getChildrenNum() > node.getChildrenNum()){
			nodeCapacity[node.getNodeID()] = capacitys[node.getNodeID()] / 2;
		}

		// ����ڵ������Ϊ1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
		// System.out.println(nodeID + " : " + nodeCapacity[nodeID] + " " +
		// capacitys[nodeID]);
		return nodeCapacity;
	}

	/**
	 * ��ȡָ���ڵ㵽���ڵ�����·������
	 * 
	 * @param newNodeID
	 *            ָ���Ľڵ�
	 * @return ����ָ���ڵ㵽���ڵ�����·������
	 */
	private int getHopCountToDesNode(int newNodeID) {
		if (newNodeID == desNode) {
			return 0;
		}
		return nodePool.getNodeWithID(newToOldMap.get(newNodeID)).getTable()
				.getRestHopCount(nodePool.getNodeWithID(newToOldMap.get(desNode)));
	}
}
