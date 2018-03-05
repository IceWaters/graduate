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
	public final static int iMaxNeig = 15; // �ھӱ��е�����ھ�����
	public final static int FullBattery = 100;// ��������100
	public HashMap<Integer, Neighbor> neighbors; // ���node�Ķ����ھ�
	protected double PCR; // Power consumption rate(%/day)
	public static float commRange; // �ڵ��ͨ�Ű뾶,�������С���ڵ�����й�
	private RoutingTable table;
	private int id;
	private int childrenNum = 0; // �洢�õ�������ӽڵ㣬����ֱ����������������
	private double remainingBattery;// ʣ�����
	private Set<Node> childrenNodes = new HashSet<Node>(); // �洢��õ�ֱ���������ӽڵ�
	private int chargingPeriod;
	private int redundantNum = 0;// �ýڵ�����ڵ����Ŀ
	private int hopNum = 0;
	private double distance;// �ýڵ�������㼯�ľ���
	private Node parent = null;//���׽ڵ�
	private int weight = 1;//·�ɹ����е�Ȩֵ

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
	// ��ʼ����
	{
		
		// commRange = Math.pow(5, 0.5) * Field.iMaxX / Math.pow(Field.iNodeSum,
		// 0.5);//ͨ�Ű뾶����֤ȫ����ͨ
		// commRange = (float) (1.5 * Field.iMaxX / Math.pow(Field.iNodeSum,
		// 0.5));// ͨ�Ű뾶����֤ȫ����ͨ;
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
	 * ���׽ڵ�
	 * @param parent
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}

	/**
	 *Ȩֵ
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
	/* ���ýڵ�ʣ����� */
	void setRemainingBattery(double battery) {
		remainingBattery = battery;
	}

	/**
	 * set power consumption rate
	 * 
	 * @param pcr
	 *            - power consumption rate of node
	 */
	/* ���ýڵ�ĵ�����(%/��) */
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
	public /* ��ýڵ�ʣ����� */
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
	public /* ����ڵ��iHop���ڵ��ھ����� */
	int calculateMultNeigNum(int iHop) {
		// edit
		int i = 1;
		int sum = 0; // �ھӼ�������
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
	 * ��������ڵ�ĸ���
	 */
	public void increaseRedundantNum() {
		redundantNum++;
	}

	/**
	 * ��ȡ�õ������ڵ���Ŀ
	 * 
	 * @return redundantNum ����ڵ���Ŀ
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
	/* �ҳ��ڵ����е�һ���ھ� */
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
	/* �ҳ��ڵ�Ķ����ھ� */
	void findMultiHopNeighbors(NodePool nodePool, int hop) {
		// �ݲ���Ҫ�༭

	}

	/**
	 * flooding
	 * 
	 * @param nodePool
	 *            - set of all nodes in network
	 */
	/* �鷺�㷨���ҵ�����鷺�ڵ�Ķ����ھӣ��Լ������ڵ㵽�ýڵ����������һ���ڵ� */
	void flooding(NodePool nodePool) {
		// currentSetΪ��ǰ�鷺��εĽڵ㼯�ϣ�nextSetΪ��һ���鷺��εĽڵ㼯�ϣ�floodedSetΪ�Ѿ��鷺���Ľڵ㼯��
		Set<Node> currentSet = new HashSet<Node>(), nextSet = new HashSet<Node>(), floodedSet = new HashSet<Node>();

		int iHop = 0; // �鷺�Ĳ���
		currentSet.add(this); // ���Ƚ�Դ�ڵ�����뵱ǰ�鷺������
		// �鷺��������Ϊ�鷺���Ľڵ㼯�ϵ�������Ľڵ��
		while (!floodedSet.containsAll(nodePool.getNodeSet())) {
			iHop++;
			Iterator it1 = currentSet.iterator(); // �Ե�ǰ�ڵ㼯�Ͻ��б���
			while (it1.hasNext()) {
				Node currentNode = (Node) it1.next();
				floodedSet.add(currentNode); // �Ѿ����鷺�Ľڵ㼯����Ӻ鷺���ĵ�ǰ�ڵ�
				Iterator it2 = currentNode.neighbors.get(1).getNeig().iterator(); // ������ǰ�ڵ���ھӽڵ�
				while (it2.hasNext()) {
					Node currentNodeNeig = (Node) it2.next();
					if (!floodedSet.contains(currentNodeNeig)) { // �����ǰ�ڵ��ھ����״α���
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
 * �ɳ��ڵ���
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
	 * ����Ԥ�ڳ��ʱ�䣨EDC��
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
	 * ��ȡԤ�ڳ��ʱ�䣨EDC��
	 * 
	 * @return EDC - expected date of charging
	 */
	int getEDC() {
		return EDC;
	}
	
	
}

/// *·�ɱ��࣬������ĳ��Ŀ�Ľڵ���ô�ߵ���Ϣ*/
// class RoutingTable{
// public HashMap<Node , RoutingGuidance> routingTable = new
/// HashMap<Node,RoutingGuidance>();
// public Node getNext(Node desNode){
// return ((RoutingGuidance)routingTable.get(desNode)).nextNode;
// }
// }
//
/// *ָʾ��Ŀ�Ľڵ������Լ�·��ת������һ���ڵ�*/
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
