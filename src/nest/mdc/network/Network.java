/**
 * 
 */
package nest.mdc.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.cluster.KCluster;
import nest.mdc.field.Field;
import nest.mdc.landform.Landform;
import nest.mdc.routing.RoutingTable;

//import nest.mdc.routing.RoutingTable;

/**
 * 网络类，测试网络的连通性，找到各个节点之间的邻居，初始化各节点之间的关系，形成特定的网络拓扑结构
 * 
 * @author：furui @version：1.0
 */

/* 网络类，用于初始化网络中节点的拓扑关系 */
public class Network {
	private NodePool nodePool;
	private int[][] distanceMap;
	public static double[][] distanceMap2 ;
	private Set<Node> nodeSet;
	private int[][] hopMap;
	private Landform[][] landforms;
	private int weight;
	private ArrayList<Set<KCluster>> classification;
	private int energyParameter;

	/**
	 * Network类的构造函数
	 * 
	 * @param nodePool
	 */
	public Network(NodePool nodePool) { // stationX，stationY为基站坐标
		this.nodePool = nodePool; // 传递的是引用而不是内存中对象的复制
		this.classification = new ArrayList<Set<KCluster>>();
		energyParameter = 2;
		// createHoleInNetwork(); //此方法能成功修改nodePool引用所指向的堆内存对象
		// System.out.println(this.nodePool.getNodeSet().size());
		initialNetwork();
//		setChildrenNum();  //用Dijkstra得到的路由路径
		// test();
	}

	/**
	 * Network类的构造函数
	 * 
	 * @param nodePool
	 */
	public Network(NodePool nodePool, Landform[][] landforms) { // stationX，stationY为基站坐标
		this.nodePool = nodePool; // 传递的是引用而不是内存中对象的复制
		this.classification = new ArrayList<Set<KCluster>>();
		energyParameter = 2;
		this.landforms = landforms;
		// createHoleInNetwork(); //此方法能成功修改nodePool引用所指向的堆内存对象
		// System.out.println(this.nodePool.getNodeSet().size());
		initialWeightNetwork();
		setChildrenNum();
		// test();
	}

	/* 测试找邻居是否成功 */
	private void test() {
		Node testNode = null;
		testNode = nodePool.getNodeWithID(100);
		int i = 1;
		while (i <= testNode.iMaxNeig) {
			Iterator it = testNode.neighbors.get(i).getNeig().iterator();
			while (it.hasNext()) {
				Node currentNode = (Node) it.next();
				System.out.println("第" + testNode.getNodeID() + "号节点的" + i + "跳邻居有" + currentNode.getNodeID() + "号节点");
			}
			i++;
		}
		// int iHop = 2;
		// System.out.println(iHop+"跳内邻居数为"+testNode.calculateMultNeigNum(iHop)+"个");
		// Iterator it1 = nodePool.getNodeList().iterator();
		// int i= 0;
		// while(it1.hasNext()){
		// Node CurrentNode = (Node)it1.next();
		// i++;
		// if(i<10){
		// if(CurrentNode.neighbors.isEmpty()){
		// System.out.println("第"+CurrentNode.getNodeID()+"号节点没有邻居");
		// }
		// else{
		// Iterator it2 = CurrentNode.neighbors.get(1).getNeig().iterator();
		// while(it2.hasNext()){
		// Node node = (Node)it2.next();
		// System.out.println("第"+CurrentNode.getNodeID()+"号节点的一跳邻居有"+node.getNodeID()+"号节点");
		// }
		// }
		// }
		// }
	}

	/**
	 * 初始化网络
	 */
	private void initialNetwork() {
		findOneHopNeig();
		if (testConnectedness()) {
			System.out.println("网络是连通的");
			findMultiHopNeig();
		} else
			System.out.println("网络不是连通的");
		initialDistanceMap();
		// initHopMap();
		runDijkstra(nodePool.getNodeWithID(0));
		// initialRoutingTableWithShortestPath();

	}

	/**
	 * 初始化网络
	 */
	private void initialWeightNetwork() {
		findOneHopNeig();
		if (testConnectedness()) {
			System.out.println("网络是连通的");
			findMultiHopNeig();
		} else
			System.out.println("网络不是连通的");
		initialWeightDistanceMap();
		// initHopMap();
		runDijkstra(nodePool.getNodeWithID(0));
		// initialRoutingTableWithShortestPath();

	}

	/**
	 * 测试网络是否为全连通网络
	 * 
	 * @return boolean
	 */
	private boolean testConnectedness() {
		Iterator<Node> it = nodePool.getNodeSet().iterator();
		Node sourceNode = (Node) it.next();
		// currentSet为当前洪泛层次的节点集合，nextSet为下一个洪泛层次的节点集合，floodedSet为已经洪泛到的节点集合
		Set<Node> currentSets = new HashSet<Node>(), nextSets = new HashSet<Node>(), floodedSets = new HashSet<Node>();
		currentSets.add(sourceNode); // 首先将源节点添加入当前洪泛集合中
		// 当经过一轮遍历没有节点加入时结束循环
		while (!currentSets.isEmpty()) {
			Iterator<Node> it1 = currentSets.iterator(); // 对当前节点集合进行遍历
			while (it1.hasNext()) {
				Node currentNode = (Node) it1.next();
				floodedSets.add(currentNode);
				if (currentNode.neighbors.isEmpty()) { // 如果当前节点没有邻居,网络肯定不连通
					return false;
				} else {
					Iterator<Node> it2 = currentNode.neighbors.get(1).getNeig().iterator(); // 遍历当前节点的邻居节点
					while (it2.hasNext()) {
						Node currentNodeNeig = (Node) it2.next();
						if (!floodedSets.contains(currentNodeNeig)) { // 如果当前节点邻居是首次遍历
							nextSets.add(currentNodeNeig);
						}
					}
				}
			}
			currentSets.clear();
			currentSets = new HashSet<Node>(nextSets);
			nextSets.clear();
		}
		if (floodedSets.containsAll(nodePool.getNodeSet())) {
			return true;
		} else
			return false;
	}

	/**
	 * 对每个节点进行遍历，找出他们各自的一跳邻居并放入路由表
	 * 
	 * @return boolean
	 */
	private boolean findOneHopNeig() {
		Iterator it = nodePool.getNodeList().iterator();
		while (it.hasNext()) {
			Node currentNode = (Node) it.next();
			currentNode.findOneHopNeighbors(nodePool);
		}
		return true;
	}

	/**
	 * 对每个节点找出其各自的多跳邻居放入邻居表
	 * 
	 * @return boolean
	 */
	private boolean findMultiHopNeig() {
		Iterator it1 = nodePool.getNodeList().iterator();
		while (it1.hasNext()) {
			Node currentNode = (Node) it1.next();
			currentNode.flooding(nodePool);
		}
		return true;
	}

	/**
	 * 在网络中生成若干个不覆盖节点的空洞
	 */
	private void createHoleInNetwork() {
		Point hole1 = new Point(50, 50);
		Point hole2 = new Point(150, 150);
		Point hole3 = new Point(250, 250);
		Iterator<Node> it = nodePool.getNodeSet().iterator();
		while (it.hasNext()) {
			Node currentNode = (Node) it.next();
			if ((currentNode.getDistance(hole1) <= 30) || (currentNode.getDistance(hole2) <= 30)
					|| (currentNode.getDistance(hole3) <= 30)) {
				it.remove();
				nodePool.getNodeList().remove(currentNode);
				nodePool.nodePoolWithID.remove(currentNode.getNodeID());

			}
		}
	}

	/**
	 * 得到任意两点最短路径
	 */
	private void initialRoutingTableWithShortestPath() {
		for (Node currentNode : nodePool.getNodeSet()) {
			runDijkstra(currentNode);
		}
	}

	/**
	 * 迪杰斯特拉算法
	 * 
	 * @param Node
	 */
	private void runDijkstra(Node startingNode) {
		int sourceID = startingNode.getNodeID(); // 得到起始点id
		int nodeSum = nodePool.getNodeNum();
		Set<Node> minNodeSets = new HashSet<Node>(); // 保存找到的最短路径点集合
		Node nextNode = startingNode; // 保存去startingNode节点最短路径的下一跳节点
		// System.out.println(minNodeSets.size());
		int[][] virtual_distanceMap = new int[nodeSum][nodeSum]; // 储存两点之间已知的最短路径距离和
		// double[][] virtual_distanceMap = new double[nodeSum][nodeSum];
		int temp = 10000; // 保存距离
		int i, j;
		int finded = -1; // 存放找到节点的id
		/* 初始化二维数组 */
		for (i = 0; i < nodeSum; i++) {
			for (j = 0; j < nodeSum; j++) {
				virtual_distanceMap[i][j] = distanceMap[i][j];
			}
		}

		for (i = 0; i < nodeSum - 1; i++) { // 循环n-1次即可
			for (j = 0; j < nodeSum; j++) {
				if (j == sourceID) { // 如果当前节点就是发起节点则跳出
					continue;
				} else if ((minNodeSets.size() == 0) && (virtual_distanceMap[sourceID][j] < temp)) { // 当前是第一次执行
					temp = virtual_distanceMap[sourceID][j];
					finded = j;
				} else if (minNodeSets.contains(nodePool.getNodeWithID(j))) { // 当前节点已经在最小路径点集中则跳出
					continue;
				} else {
					for (Node currentNode : minNodeSets) {
						if (virtual_distanceMap[currentNode.getNodeID()][j] < 10000
								&& virtual_distanceMap[currentNode.getNodeID()][j] != 0) {
							if (virtual_distanceMap[currentNode.getNodeID()][j]
									+ virtual_distanceMap[sourceID][currentNode.getNodeID()] < temp) {
								temp = virtual_distanceMap[currentNode.getNodeID()][j]
										+ virtual_distanceMap[sourceID][currentNode.getNodeID()];
								finded = j;
								nextNode = currentNode;
							}
						} else {
							if (virtual_distanceMap[sourceID][j] < temp) {
								temp = virtual_distanceMap[sourceID][j];
								finded = j;
								nextNode = startingNode;
							}
						}
					}
				}
			}
			Node findedMinNode = nodePool.getNodeWithID(finded);
			minNodeSets.add(findedMinNode);
			findedMinNode.getTable().add(startingNode, nextNode, 1);
			// findedMinNode.getTable().add(startingNode, findedMinNode);;
			virtual_distanceMap[sourceID][finded] = temp;
			temp = 10000;

		}
		for (i = 0; i < nodeSum; i++) {
			for (j = 0; j < nodeSum; j++) {
				virtual_distanceMap[i][j] = distanceMap[i][j];
			}
		}

		// return nextNode;
	}

	/**
	 * 初始化距离图
	 */
	private void initialDistanceMap() {
		int i, j, nodeSum;
		nodeSum = nodePool.getNodeNum();
		distanceMap2 = new double[nodeSum][nodeSum];
		distanceMap = new int[nodeSum][nodeSum];

		for (i = 0; i < nodeSum; i++) {
			Node xNode = nodePool.getNodeWithID(i);
			for (j = 0; j < nodeSum; j++) {
				Node yNode = nodePool.getNodeWithID(j);
				if (xNode.neighbors.get(1).getNeig().contains(yNode)) { // 只有邻居才能通信
					distanceMap2[i][j] = xNode.getDistance(yNode);
					distanceMap[i][j] = 1;
				} else {
					distanceMap[i][j] = 10000; // 有distanceMap[i][i] = 10000
				}

			}
		}
	}

	/**
	 * 初始化距离图
	 */
	private void initialWeightDistanceMap() {
		int i, j, nodeSum;
		nodeSum = nodePool.getNodeNum();
		// distanceMap = new double[nodeSum][nodeSum];
		distanceMap = new int[nodeSum][nodeSum];

		for (i = 0; i < nodeSum; i++) {
			Node xNode = nodePool.getNodeWithID(i);
			for (j = 0; j < nodeSum; j++) {
				Node yNode = nodePool.getNodeWithID(j);
				if (xNode.neighbors.get(1).getNeig().contains(yNode)) { // 只有邻居才能通信
					// distanceMap[i][j] = xNode.getDistance(yNode);
					if (landforms[(int) xNode.getXCoordinate()][(int) xNode.getYCoordinate()] == Landform.ROAD
							&& landforms[(int) yNode.getXCoordinate()][(int) yNode.getYCoordinate()] == Landform.ROAD)
						distanceMap[i][j] = Field.weight1;
					else if (landforms[(int) xNode.getXCoordinate()][(int) xNode.getYCoordinate()] == Landform.ROAD2
							&& landforms[(int) yNode.getXCoordinate()][(int) yNode.getYCoordinate()] == Landform.ROAD2)
						distanceMap[i][j] = Field.weight2;
					// else if (landforms[(int) xNode.getXCoordinate()][(int)
					// xNode.getYCoordinate()] == Landform.HARD
					// && landforms[(int) yNode.getXCoordinate()][(int)
					// yNode.getYCoordinate()] == Landform.HARD)
					// distanceMap[i][j] = 1;
					// else {
					// distanceMap[i][j] = (Field.weight1 + Field.weight2) / 2;
					// }
					else {
						distanceMap[i][j] = 1;
					}
				} else {
					distanceMap[i][j] = 10000; // 有distanceMap[i][i] = 10000
				}

			}
		}
	}

	/**
	 * 初始化全图节点的跳数图
	 */
	// public void initHopMap() {
	// int i,j,nodeSum;
	// nodeSum = nodePool.getNodeNum();
	// hopMap = new int[nodeSum][nodeSum];
	//
	// for (i = 0; i < nodeSum; i++){
	// Node xNode = nodePool.getNodeWithID(i);
	// for(j = 0; j < nodeSum; j++){
	// Node yNode = nodePool.getNodeWithID(j);
	// for (int k = 1 ; k < 16; k++) {
	// if(xNode.neighbors.get(k) == null)
	// continue;
	// if (xNode.neighbors.get(k).getNeig().contains(yNode)) {
	// hopMap[i][j] = k ;
	// break ;
	// }
	// }
	//
	// }
	// }
	// }

	/**
	 * 设置每个节点的子节点数目，包括直接相连与间接相连
	 */
	private void setChildrenNum() {
		nodeSet = nodePool.getNodeSet();
		Node rootNode = nodePool.getNodeWithID(0);
		for (Node node : nodeSet) {
			node.clearChildren();
		}

		for (Node node : nodeSet) {
			// 设置每个节点直接相连的子节点
			if (node != rootNode) {
				RoutingTable routingTable = node.getTable();// 得到中间节点的路由表
				Node nextNode = routingTable.getNext(rootNode);// 从路由表中得到去目的节点的下一跳
				nextNode.addChild(node);
				// System.out.println(i++);
			}
		}

		for (Node node : nodeSet) {
			int num = findChildrenNum(node);
			node.setChildrenNum(num);
		}
	}

	/**
	 * 设置每个节点的子节点数目，包括直接相连与间接相连
	 */
	public void setHopNum() {
		nodeSet = nodePool.getNodeSet();
		Node rootNode = nodePool.getNodeWithID(0);

		for (Node node : nodeSet) {

			// 设置每个节点直接相连的子节点
			RoutingTable routingTable = node.getTable();// 得到中间节点的路由表
			Node nextNode = node;
			int hopNum = 0;
			while (nextNode != rootNode) {
				hopNum++;
				nextNode = routingTable.getNext(rootNode);// 从路由表中得到去目的节点的下一跳
				routingTable = nextNode.getTable();
				// System.out.println(i++);
			}
			node.setHopNum(hopNum);
			// System.out.println(node.getNodeID() + " " + hopNum);
		}

	}

	/**
	 * 用递归的方式找每个节点的子节点数目
	 * 
	 * @param node
	 * @return
	 */
	private int findChildrenNum(Node node) {
		Set<Node> nodes = node.getChildren();
		int num = nodes.size();

		for (Node childNode : nodes) {
			num = num + findChildrenNum(childNode);
		}

		return num;
	}

	public int[] routing(Node source, Node destination, NodePool nodePool) {
		int[] path = null;
		// 输入一个源节点,目的节点，返回一条节点路径ID序列
		RoutingTable routingTable = null;
		ArrayList<Integer> idList = new ArrayList<Integer>();// 动态添加节点路径ID序列
		Node nextNode = source;
		while (nextNode != destination) {
			idList.add(nodePool.getNodeID(nextNode));// 将节点的ID序列加入动态数组
			routingTable = nextNode.getTable();// 得到中间节点的路由表
			nextNode = routingTable.getNext(destination);// 从路由表中得到去目的节点的下一跳
		}
		idList.add(nodePool.getNodeID(nextNode));// 将目的节点加入节点路径
		path = new int[idList.size()];

		int i = 0;
		for (Integer id : idList) { // 将动态数组的内容赋给path，并打印节点序列
			path[i++] = id;
			System.out.println(i + " : " + id);
		}
		return path;
	}

}
