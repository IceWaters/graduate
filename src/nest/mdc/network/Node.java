package nest.mdc.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.routing.RoutingTable;

//import nest.mdc.routing.RoutingTable;

/**
 * network node
 * 
 * @author xiaoq
 * @version 1.0
 */
public class Node extends Point {
	public final static int iMaxNeig = 15; // 邻居表中的最大邻居跳数
	public final static int FullBattery = 100;// 满电量是100
	public HashMap<Integer, Neighbor> neighbors; // 存放node的多跳邻居
	protected double PCR; // Power consumption rate(%/day)
	public static float commRange; // 节点的通信半径,与网络大小及节点个数有关
	private RoutingTable table;
	private int id;
	private int childrenNum = 0; // 存储该点的所有子节点，包括直接相连与间接相连的
	private double remainingBattery;// 剩余电量
	private Set<Node> childrenNodes = new HashSet<Node>(); // 存储与该点直接相连的子节点
	private int chargingPeriod;
	private int redundantNum = 0;// 该节点冗余节点的数目
	private int hopNum = 0;
	private double distance;// 该节点与给定点集的距离
	private Node parent = null;//父亲节点
	private int weight = 1;//路由构造中的权值

	public class Neighbor {
		private Set<Node> neighbor;

		Neighbor() {
			neighbor = new HashSet<Node>();
		}

		public Set<Node> getNeig() {
			return neighbor;
		}

		boolean addNeig(Node node) {
			return neighbor.add(node);
		}

		boolean remove(Node node) {
			return neighbor.remove(node);
		}
	}

	/**
	 * initialize
	 * 
	 * @param
	 */
	// 初始化块
	{
		
		// commRange = Math.pow(5, 0.5) * Field.iMaxX / Math.pow(Field.iNodeSum,
		// 0.5);//通信半径，保证全网连通
		// commRange = (float) (1.5 * Field.iMaxX / Math.pow(Field.iNodeSum,
		// 0.5));// 通信半径，保证全网连通;
		table = new RoutingTable();
		neighbors = new HashMap<Integer, Neighbor>();
	}

	/**
	 * Constructor
	 * 
	 * @param
	 */
	public Node(double x, double y, int id) {
		super(x, y);
		this.id = id;
		remainingBattery = FullBattery;
		PCR = 0; // Power consumption rate(%/day)
	}

	/**
	 * get node's id
	 * 
	 * @return id - the id of this node
	 */
	public int getNodeID() {
		return id;
	}

	/**
	 * to set the num of the children
	 * 
	 * @param childNum
	 */
	public void setChildrenNum(int childrenNum) {
		this.childrenNum = childrenNum;
	}

	/**
	 * to get the num of the children
	 * 
	 * @return
	 */
	public int getChildrenNum() {
		return this.childrenNum;
	}

	/**
	 * add a child to the node
	 * 
	 * @param node
	 */
	public void addChild(Node node) {
		childrenNodes.add(node);
	}

	public void clearChildren() {
		childrenNodes.clear();
	}
	
	/**
	 * 父亲节点
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}

	/**
	 *权值
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void addWeightByOne() {
		weight = weight + 1;
	}
	/**
	 * get the children of the node
	 * 
	 * @return childNodes - the children of the node
	 */
	public Set<Node> getChildren() {
		return childrenNodes;
	}

	public void setHopNum(int hopNum) {
		this.hopNum = hopNum;
	}

	public int getHopNum() {
		return hopNum;
	}

	/**
	 * get routing table
	 * 
	 * @return the routing table of this node
	 */
	public RoutingTable getTable() {
		return table;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	/**
	 * set the battery level of this node
	 * 
	 * @param
	 */
	/* 设置节点剩余电量 */
	void setRemainingBattery(double battery) {
		remainingBattery = battery;
	}

	/**
	 * set power consumption rate
	 * 
	 * @param pcr
	 *            - power consumption rate of node
	 */
	/* 设置节点耗电速率(%/天) */
	void setPCR(double pcr) {
		PCR = pcr;
	}

	/**
	 * set charging period
	 * 
	 * @param chargingPeriod
	 */
	public void setChargingPeriod(int chargingPeriod) {
		this.chargingPeriod = chargingPeriod;
	}

	/**
	 * get charging period
	 * 
	 * @return chargingPeriod
	 */
	public int getChargingPeriod() {
		return chargingPeriod;
	}

	/**
	 * get the battery level of this node
	 * 
	 * @param
	 * @return remainingBattery - the battery level of this node
	 */
	public /* 获得节点剩余电量 */
	double getRemainingBattery() {
		return remainingBattery;
	}

	/**
	 * count the number of this node's neighbors in x hop
	 * 
	 * @param iHop
	 *            - the hop limit of counting neighbors
	 * @return sum - the number of this node's neighbors in x hop
	 */
	public /* 计算节点的iHop跳内的邻居总数 */
	int calculateMultNeigNum(int iHop) {
		// edit
		int i = 1;
		int sum = 0; // 邻居计数变量
		while (i <= iHop) {
			Iterator it = this.neighbors.get(i).getNeig().iterator();
			while (it.hasNext()) {
				Node currentNode = (Node) it.next();
				sum++;
			}
			i++;
		}
		return sum;
	}

	/**
	 * 增加冗余节点的个数
	 */
	public void increaseRedundantNum() {
		redundantNum++;
	}

	/**
	 * 获取该点的冗余节点数目
	 * 
	 * @return redundantNum 冗余节点数目
	 */
	public int getRedunantNum() {
		return redundantNum;
	}

	/**
	 * find the neighbors of this node in one hop
	 * 
	 * @param nodePool
	 *            - set of all nodes in network
	 */
	/* 找出节点所有的一跳邻居 */
	void findOneHopNeighbors(NodePool nodePool) {
		// ArrayList nodeList = nodePool.getNodeList();
		Iterator it = nodePool.getNodeList().iterator();
		while (it.hasNext()) {
			Node currentNode = (Node) it.next();
			if (currentNode.getNodeID() != this.id) {
				if (currentNode.getDistance(this) <= commRange) {
					if (!neighbors.containsKey(1)) {
						Neighbor oneHopNeig = new Neighbor();
						oneHopNeig.addNeig(currentNode);
						neighbors.put(1, oneHopNeig);
					} else {
						neighbors.get(1).addNeig(currentNode);
					}
				}
			}
		}
	}

	/**
	 * find the neighbors of this node in x hop
	 * 
	 * @param nodePool
	 *            - set of all nodes in network
	 * @param hop
	 *            - the hop limit of finding neighbors
	 */
	/* 找出节点的多跳邻居 */
	void findMultiHopNeighbors(NodePool nodePool, int hop) {
		// 暂不需要编辑

	}

	/**
	 * flooding
	 * 
	 * @param nodePool
	 *            - set of all nodes in network
	 */
	/* 洪泛算法，找到发起洪泛节点的多跳邻居，以及其他节点到该节点的跳数和下一跳节点 */
	void flooding(NodePool nodePool) {
		// currentSet为当前洪泛层次的节点集合，nextSet为下一个洪泛层次的节点集合，floodedSet为已经洪泛到的节点集合
		Set<Node> currentSet = new HashSet<Node>(), nextSet = new HashSet<Node>(), floodedSet = new HashSet<Node>();

		int iHop = 0; // 洪泛的层数
		currentSet.add(this); // 首先将源节点添加入当前洪泛集合中
		// 洪泛结束条件为洪泛到的节点集合等于网络的节点池
		while (!floodedSet.containsAll(nodePool.getNodeSet())) {
			iHop++;
			Iterator it1 = currentSet.iterator(); // 对当前节点集合进行遍历
			while (it1.hasNext()) {
				Node currentNode = (Node) it1.next();
				floodedSet.add(currentNode); // 已经被洪泛的节点集合添加洪泛到的当前节点
				Iterator it2 = currentNode.neighbors.get(1).getNeig().iterator(); // 遍历当前节点的邻居节点
				while (it2.hasNext()) {
					Node currentNodeNeig = (Node) it2.next();
					if (!floodedSet.contains(currentNodeNeig)) { // 如果当前节点邻居是首次遍历
						nextSet.add(currentNodeNeig);
						if (iHop > 1 && iHop <= iMaxNeig) {
							if (!neighbors.containsKey(iHop)) {
								Neighbor multiHopNeig = new Neighbor();
								multiHopNeig.addNeig(currentNodeNeig);
								neighbors.put(iHop, multiHopNeig);
							} else {
								neighbors.get(iHop).addNeig(currentNodeNeig);
							}

						}
						currentNodeNeig.getTable().add(this, currentNode, iHop - 1);
					}
				}

			}
			currentSet.clear();
			currentSet = new HashSet<Node>(nextSet);
			nextSet.clear();
		}
	}
	
	public Object clone(){
        try{
            Node copy=(Node)super.clone();
            return copy;
        }catch (CloneNotSupportedException e){
            System.out.println("can not clone");
        }
        return null;
    }
}

/**
 * 可充电节点类
 * 
 * @author xiaoq
 * @version 1.0
 */
class RechargeableNode extends Node {

	private int chargingPeriod;
	private int iBattery;
	private int EDC; // expected date of confinement

	/**
	 * Constructor
	 * 
	 * @param x
	 *            - the x-coordinate of this node
	 * @param y
	 *            - the y-coordinate of this node
	 * @param id
	 *            - the id of this node
	 * @param chargingPeriod
	 *            - the charging period of this node
	 * @param iBattery
	 *            - the battery level of this node
	 */
	RechargeableNode(double x, double y, int id, int chargingPeriod, int iBattery) {
		super(x, y, id);
		this.chargingPeriod = chargingPeriod;
		this.iBattery = iBattery;
	}

	/**
	 * 计算预期充电时间（EDC）
	 * 
	 * @return EDC - expected date of charging
	 */
	int calculateEDC() {
		if (PCR > 0) {
			return (int) (iBattery / PCR);
		} else {
			return -1;
		}
	}

	/**
	 * 获取预期充电时间（EDC）
	 * 
	 * @return EDC - expected date of charging
	 */
	int getEDC() {
		return EDC;
	}
	
	
}

/// *路由表类，包含到某个目的节点怎么走的信息*/
// class RoutingTable{
// public HashMap<Node , RoutingGuidance> routingTable = new
/// HashMap<Node,RoutingGuidance>();
// public Node getNext(Node desNode){
// return ((RoutingGuidance)routingTable.get(desNode)).nextNode;
// }
// }
//
/// *指示离目的节点跳数以及路由转发的下一跳节点*/
// class RoutingGuidance{
// public Node nextNode;
// public int iHopCount = -1;
// RoutingGuidance(Node nextNode, int iHopCount){
// this.nextNode = nextNode;
// this.iHopCount = iHopCount;
// }
// RoutingGuidance(Node nextNode){
// this.nextNode = nextNode;
// }
// }
