package nest.mdc.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

public class MaxFlowRouter {
	private final int maxCost = 10000; // 表示相应的边不可通行
	private int maxFlow; // 最大流，也就是最大数据量，由原网络的节点数量决定
	private NodePool nodePool; // 原节点池
	private int newNodeNum; // 转换之后的新拓扑结构的点的个数
	private int souNode; // 新图中的源节点
	private int desNode; // 新图中的汇聚节点
	private int[][] costMatrix; // 新图的连通性矩阵
	private Map<Integer, Integer> oldToNewMap; // 原图的nodeID与新图中节点ID的映射
	private Map<Integer, Set<Integer>> neigMap; // 新图中的节点邻居映射
	private Set<Integer> availableSet; // 路由过程中的剩余可通行节点
	private Map<Integer, Integer> newToOldMap; // 新图节点ID到原图nodeID的映射
	public int[] nodeCapacity; // 新图中各个节点的容量
	public int[] flow; // 新图中各个节点的实际流量
	public int[] oldFlow;
	private int[][] flowAlloc; // 新图中各个节点的流量分配情况，flowAlloc[i][j]表示节点i流向节点j的流量
	public boolean flag;

	/**
	 * 构造函数，用于初始化
	 * 
	 * @param nodePool
	 *            原图的节点池
	 * @param rootNodeID
	 *            原图的根节点
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
	 * 得到原始点编号的能耗
	 * 
	 * @return oldFlow ： 对应原始编号的能耗
	 */
	public int[] getFlow() {

		for (int i = 0; i < nodePool.getNodeSet().size(); i++) {
			oldFlow[newToOldMap.get(i)] = flow[i];
		}

		return oldFlow;
	}

	/**
	 * 初始化新图，又原图得到新图
	 */
	private void initGraph() {
		int i = 0;
		for (int nodeID : nodePool.nodePoolWithID.keySet()) {
			// 为每个原图中的节点建立对应的新图节点
			oldToNewMap.put(nodeID, i);
			newToOldMap.put(i++, nodeID);
		}
		desNode = oldToNewMap.get(desNode); // 将原图的根节点对应到新图的汇聚节点

		for (int oldNodeID : nodePool.nodePoolWithID.keySet()) {
			// 根据原图的拓扑结构构件新图的邻接关系
			int newNodeID = oldToNewMap.get(oldNodeID);
			if (!neigMap.containsKey(newNodeID)) {
				neigMap.put(newNodeID, new HashSet<Integer>());
			}
			Set<Node> neigSet = nodePool.getNodeWithID(oldNodeID).neighbors.get(1).getNeig();
			for (Node currNeig : neigSet) {
				neigMap.get(newNodeID).add(oldToNewMap.get(nodePool.getNodeID(currNeig)));
			}
		}

		neigMap.put(souNode, new HashSet<Integer>()); // 设置新图的源节点
		for (int j = i; j < newNodeNum; j++) {
			// 为每一个原图中的节点建立一个新的伴随节点，并将其和对应的原图节点连接起来
			if (!neigMap.containsKey(j)) {
				neigMap.put(j, new HashSet<Integer>());
			}
			// 同时将每个伴随节点都和新图中的源节点连接起来
			neigMap.get(j).add(j - i);
			neigMap.get(souNode).add(j);
		}

		for (int j = 0; j < costMatrix.length; j++) {
			// 初始化邻接矩阵
			for (int j2 = 0; j2 < costMatrix.length; j2++) {
				costMatrix[j][j2] = maxCost;
			}
		}

		for (int j = 0; j < newNodeNum; j++) {
			// 根据邻接映射设置邻接矩阵
			for (int neigID : neigMap.get(j)) {
				if (newToOldMap.containsKey(j) && newToOldMap.containsKey(neigID)) {
					// 设置路径权重
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
	 * 获取各个节点的流量上限 ,源节点和目的节点的流量上限是由原网络的次大子节点数量决定
	 * 
	 * @param maxNodeNum
	 *            - 运行Dijkstra后，得到的除根节点之外的最大子节点数
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

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;

		return nodeCapacity;
	}

	/**
	 * 设置各个节点的流量上限
	 * 
	 * @param index
	 *            - 2的index次方为每个节点的流量上限
	 */
	public void setNodeCapacity(int index) {

		int capacity = (int) Math.pow(2, index);
		for (int i = 0; i < nodePool.getNodeNum(); i++)
			nodeCapacity[i] = capacity;

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeNum(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * 设置各个节点的流量上限
	 * 
	 * @param capacity
	 *            - 每个节点的流量上限
	 */
	public void setNodeCapacity(int[] capacity) {

		for (int i = 0; i < nodePool.getNodeNum(); i++)
			nodeCapacity[i] = capacity[i];

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeNum(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * 根据邻居的流量平均数，设置各个节点的流量上限
	 * 
	 * @param index
	 *            - 2的index次方为每个节点的流量上限
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

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
	}

	/**
	 * 找出邻居中到根节点跳数相同的节点，判断给定节点的流量上限是不是最大的，如果是，则将上限除以2，否则，不变
	 * 
	 * @param nodeID
	 *            - 给定节点的ID
	 * @param capacitys
	 *            - 存储上一次流量上限的数组
	 * @return
	 */
	public int[] setNodeCapacityByNeighbor(int nodeID, int[] capacitys) {
		Node node = nodePool.getNodeWithID(nodeID);
		nodeID = oldToNewMap.get(nodeID);
		flag = false;
		nodeCapacity = new int[newNodeNum];

		int hop = node.getHopNum();
		int flowSum = 0; // 记录给定区域的实际流量之和
		int capacitySum = 0; // 记录给定区域内的流量上限之和
		Routing.visited[nodeID] = 1;
		Set<Node> nodes = new HashSet<>();// 存储邻居中跳数相同的节点
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

		// 设置每个节点的节点上限
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		nodeCapacity[nodeID] = capacitys[nodeID] / 2;
		// 判断是否改变给定的节点上限
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

		// 伴随节点的容量为1
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
	 * 将在同一个区域内的节点的流量上限同时降低，该区域现为方形
	 * 
	 * @param nodeID
	 *            - 给定节点的ID
	 * @param capacitys
	 *            - 存储上一次流量上限的数组
	 * @return
	 */
	public int[] setNodeCapacityByArea(int x, int y, int[] capacitys) {
		flag = false;
		nodeCapacity = new int[newNodeNum];
		Set<Node> nodes = new HashSet<>();// 存储该区域的节点

		// 设置每个节点的节点上限
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		// 找出该区域的点
		for (Node node : nodePool.getNodeSet()) {
			if (node.getXCoordinate() >= Routing.areaLength * x && node.getXCoordinate() < (x + 1) * Routing.areaLength
					&& node.getYCoordinate() >= Routing.areaLength * y
					&& node.getYCoordinate() < (y + 1) * Routing.areaLength)
				nodes.add(node);
		}

		// 判断是否改变给定的节点上限
		for (Node node : nodes) {
			if (node.getNodeID() == 0)
				continue;
			// System.out.print(node.getNodeID() + " ");
			// if(node2.getChildrenNum() > node.getChildrenNum()){
			nodeCapacity[oldToNewMap.get(node.getNodeID())] = capacitys[oldToNewMap.get(node.getNodeID())] / 2;
		}
		// System.out.println();

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
		// System.out.println(nodeID + " : " + nodeCapacity[nodeID] + " " +
		// capacitys[nodeID]);
		return nodeCapacity;
	}

	/**
	 * 将在同一个区域内的节点的流量上限同时降低，该区域现为方形
	 * 
	 * @param nodeID
	 *            - 给定节点的ID
	 * @param capacitys
	 *            - 存储上一次流量上限的数组
	 * @return nodeCapacity - 调整后的流量上限
	 */
	public int[] setNodeCapacityByCircleArea(int k, int[] capacitys) {
		flag = false;
		nodeCapacity = new int[newNodeNum];
		Set<Node> nodes = new HashSet<>();// 存储该区域的节点
		double x = nodePool.getNodeWithID(0).getXCoordinate();
		double y = nodePool.getNodeWithID(0).getYCoordinate();

		// 设置每个节点的节点上限
		for (int i = 0; i < nodePool.getNodeSet().size(); i++)
			nodeCapacity[i] = capacitys[i];
		// 找出该区域的点
		for (Node node : nodePool.getNodeSet()) {
			if (Math.sqrt(Math.abs(node.getXCoordinate() - x) + Math.abs(node.getYCoordinate() - y)) <= k
					* Routing.radius
					&& Math.sqrt(Math.abs(node.getXCoordinate() - x) + Math.abs(node.getYCoordinate() - y)) > (k - 1)
							* Routing.radius)
				nodes.add(node);
		}

		// 判断是否改变给定的节点上限
		for (Node node : nodes) {
			if (node.getNodeID() == 0)
				continue;
			// if(node2.getChildrenNum() > node.getChildrenNum()){
			nodeCapacity[node.getNodeID()] = capacitys[node.getNodeID()] / 2;
		}

		// 伴随节点的容量为1
		for (int i = nodePool.getNodeSet().size(); i < nodeCapacity.length; i++)
			nodeCapacity[i] = 1;
		nodeCapacity[souNode] = maxFlow;
		nodeCapacity[desNode] = maxFlow;
		// System.out.println(nodeID + " : " + nodeCapacity[nodeID] + " " +
		// capacitys[nodeID]);
		return nodeCapacity;
	}

	/**
	 * 获取指定节点到根节点的最短路由跳数
	 * 
	 * @param newNodeID
	 *            指定的节点
	 * @return 返回指定节点到根节点的最短路由跳数
	 */
	private int getHopCountToDesNode(int newNodeID) {
		if (newNodeID == desNode) {
			return 0;
		}
		return nodePool.getNodeWithID(newToOldMap.get(newNodeID)).getTable()
				.getRestHopCount(nodePool.getNodeWithID(newToOldMap.get(desNode)));
	}
}
