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
 * �����࣬�����������ͨ�ԣ��ҵ������ڵ�֮����ھӣ���ʼ�����ڵ�֮��Ĺ�ϵ���γ��ض����������˽ṹ
 * 
 * @author��furui @version��1.0
 */

/* �����࣬���ڳ�ʼ�������нڵ�����˹�ϵ */
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
	 * Network��Ĺ��캯��
	 * 
	 * @param nodePool
	 */
	public Network(NodePool nodePool) { // stationX��stationYΪ��վ����
		this.nodePool = nodePool; // ���ݵ������ö������ڴ��ж���ĸ���
		this.classification = new ArrayList<Set<KCluster>>();
		energyParameter = 2;
		// createHoleInNetwork(); //�˷����ܳɹ��޸�nodePool������ָ��Ķ��ڴ����
		// System.out.println(this.nodePool.getNodeSet().size());
		initialNetwork();
//		setChildrenNum();  //��Dijkstra�õ���·��·��
		// test();
	}

	/**
	 * Network��Ĺ��캯��
	 * 
	 * @param nodePool
	 */
	public Network(NodePool nodePool, Landform[][] landforms) { // stationX��stationYΪ��վ����
		this.nodePool = nodePool; // ���ݵ������ö������ڴ��ж���ĸ���
		this.classification = new ArrayList<Set<KCluster>>();
		energyParameter = 2;
		this.landforms = landforms;
		// createHoleInNetwork(); //�˷����ܳɹ��޸�nodePool������ָ��Ķ��ڴ����
		// System.out.println(this.nodePool.getNodeSet().size());
		initialWeightNetwork();
		setChildrenNum();
		// test();
	}

	/* �������ھ��Ƿ�ɹ� */
	private void test() {
		Node testNode = null;
		testNode = nodePool.getNodeWithID(100);
		int i = 1;
		while (i <= testNode.iMaxNeig) {
			Iterator it = testNode.neighbors.get(i).getNeig().iterator();
			while (it.hasNext()) {
				Node currentNode = (Node) it.next();
				System.out.println("��" + testNode.getNodeID() + "�Žڵ��" + i + "���ھ���" + currentNode.getNodeID() + "�Žڵ�");
			}
			i++;
		}
		// int iHop = 2;
		// System.out.println(iHop+"�����ھ���Ϊ"+testNode.calculateMultNeigNum(iHop)+"��");
		// Iterator it1 = nodePool.getNodeList().iterator();
		// int i= 0;
		// while(it1.hasNext()){
		// Node CurrentNode = (Node)it1.next();
		// i++;
		// if(i<10){
		// if(CurrentNode.neighbors.isEmpty()){
		// System.out.println("��"+CurrentNode.getNodeID()+"�Žڵ�û���ھ�");
		// }
		// else{
		// Iterator it2 = CurrentNode.neighbors.get(1).getNeig().iterator();
		// while(it2.hasNext()){
		// Node node = (Node)it2.next();
		// System.out.println("��"+CurrentNode.getNodeID()+"�Žڵ��һ���ھ���"+node.getNodeID()+"�Žڵ�");
		// }
		// }
		// }
		// }
	}

	/**
	 * ��ʼ������
	 */
	private void initialNetwork() {
		findOneHopNeig();
		if (testConnectedness()) {
			System.out.println("��������ͨ��");
			findMultiHopNeig();
		} else
			System.out.println("���粻����ͨ��");
		initialDistanceMap();
		// initHopMap();
		runDijkstra(nodePool.getNodeWithID(0));
		// initialRoutingTableWithShortestPath();

	}

	/**
	 * ��ʼ������
	 */
	private void initialWeightNetwork() {
		findOneHopNeig();
		if (testConnectedness()) {
			System.out.println("��������ͨ��");
			findMultiHopNeig();
		} else
			System.out.println("���粻����ͨ��");
		initialWeightDistanceMap();
		// initHopMap();
		runDijkstra(nodePool.getNodeWithID(0));
		// initialRoutingTableWithShortestPath();

	}

	/**
	 * ���������Ƿ�Ϊȫ��ͨ����
	 * 
	 * @return boolean
	 */
	private boolean testConnectedness() {
		Iterator<Node> it = nodePool.getNodeSet().iterator();
		Node sourceNode = (Node) it.next();
		// currentSetΪ��ǰ�鷺��εĽڵ㼯�ϣ�nextSetΪ��һ���鷺��εĽڵ㼯�ϣ�floodedSetΪ�Ѿ��鷺���Ľڵ㼯��
		Set<Node> currentSets = new HashSet<Node>(), nextSets = new HashSet<Node>(), floodedSets = new HashSet<Node>();
		currentSets.add(sourceNode); // ���Ƚ�Դ�ڵ�����뵱ǰ�鷺������
		// ������һ�ֱ���û�нڵ����ʱ����ѭ��
		while (!currentSets.isEmpty()) {
			Iterator<Node> it1 = currentSets.iterator(); // �Ե�ǰ�ڵ㼯�Ͻ��б���
			while (it1.hasNext()) {
				Node currentNode = (Node) it1.next();
				floodedSets.add(currentNode);
				if (currentNode.neighbors.isEmpty()) { // �����ǰ�ڵ�û���ھ�,����϶�����ͨ
					return false;
				} else {
					Iterator<Node> it2 = currentNode.neighbors.get(1).getNeig().iterator(); // ������ǰ�ڵ���ھӽڵ�
					while (it2.hasNext()) {
						Node currentNodeNeig = (Node) it2.next();
						if (!floodedSets.contains(currentNodeNeig)) { // �����ǰ�ڵ��ھ����״α���
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
	 * ��ÿ���ڵ���б������ҳ����Ǹ��Ե�һ���ھӲ�����·�ɱ�
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
	 * ��ÿ���ڵ��ҳ�����ԵĶ����ھӷ����ھӱ�
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
	 * ���������������ɸ������ǽڵ�Ŀն�
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
	 * �õ������������·��
	 */
	private void initialRoutingTableWithShortestPath() {
		for (Node currentNode : nodePool.getNodeSet()) {
			runDijkstra(currentNode);
		}
	}

	/**
	 * �Ͻ�˹�����㷨
	 * 
	 * @param Node
	 */
	private void runDijkstra(Node startingNode) {
		int sourceID = startingNode.getNodeID(); // �õ���ʼ��id
		int nodeSum = nodePool.getNodeNum();
		Set<Node> minNodeSets = new HashSet<Node>(); // �����ҵ������·���㼯��
		Node nextNode = startingNode; // ����ȥstartingNode�ڵ����·������һ���ڵ�
		// System.out.println(minNodeSets.size());
		int[][] virtual_distanceMap = new int[nodeSum][nodeSum]; // ��������֮����֪�����·�������
		// double[][] virtual_distanceMap = new double[nodeSum][nodeSum];
		int temp = 10000; // �������
		int i, j;
		int finded = -1; // ����ҵ��ڵ��id
		/* ��ʼ����ά���� */
		for (i = 0; i < nodeSum; i++) {
			for (j = 0; j < nodeSum; j++) {
				virtual_distanceMap[i][j] = distanceMap[i][j];
			}
		}

		for (i = 0; i < nodeSum - 1; i++) { // ѭ��n-1�μ���
			for (j = 0; j < nodeSum; j++) {
				if (j == sourceID) { // �����ǰ�ڵ���Ƿ���ڵ�������
					continue;
				} else if ((minNodeSets.size() == 0) && (virtual_distanceMap[sourceID][j] < temp)) { // ��ǰ�ǵ�һ��ִ��
					temp = virtual_distanceMap[sourceID][j];
					finded = j;
				} else if (minNodeSets.contains(nodePool.getNodeWithID(j))) { // ��ǰ�ڵ��Ѿ�����С·���㼯��������
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
	 * ��ʼ������ͼ
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
				if (xNode.neighbors.get(1).getNeig().contains(yNode)) { // ֻ���ھӲ���ͨ��
					distanceMap2[i][j] = xNode.getDistance(yNode);
					distanceMap[i][j] = 1;
				} else {
					distanceMap[i][j] = 10000; // ��distanceMap[i][i] = 10000
				}

			}
		}
	}

	/**
	 * ��ʼ������ͼ
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
				if (xNode.neighbors.get(1).getNeig().contains(yNode)) { // ֻ���ھӲ���ͨ��
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
					distanceMap[i][j] = 10000; // ��distanceMap[i][i] = 10000
				}

			}
		}
	}

	/**
	 * ��ʼ��ȫͼ�ڵ������ͼ
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
	 * ����ÿ���ڵ���ӽڵ���Ŀ������ֱ��������������
	 */
	private void setChildrenNum() {
		nodeSet = nodePool.getNodeSet();
		Node rootNode = nodePool.getNodeWithID(0);
		for (Node node : nodeSet) {
			node.clearChildren();
		}

		for (Node node : nodeSet) {
			// ����ÿ���ڵ�ֱ���������ӽڵ�
			if (node != rootNode) {
				RoutingTable routingTable = node.getTable();// �õ��м�ڵ��·�ɱ�
				Node nextNode = routingTable.getNext(rootNode);// ��·�ɱ��еõ�ȥĿ�Ľڵ����һ��
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
	 * ����ÿ���ڵ���ӽڵ���Ŀ������ֱ��������������
	 */
	public void setHopNum() {
		nodeSet = nodePool.getNodeSet();
		Node rootNode = nodePool.getNodeWithID(0);

		for (Node node : nodeSet) {

			// ����ÿ���ڵ�ֱ���������ӽڵ�
			RoutingTable routingTable = node.getTable();// �õ��м�ڵ��·�ɱ�
			Node nextNode = node;
			int hopNum = 0;
			while (nextNode != rootNode) {
				hopNum++;
				nextNode = routingTable.getNext(rootNode);// ��·�ɱ��еõ�ȥĿ�Ľڵ����һ��
				routingTable = nextNode.getTable();
				// System.out.println(i++);
			}
			node.setHopNum(hopNum);
			// System.out.println(node.getNodeID() + " " + hopNum);
		}

	}

	/**
	 * �õݹ�ķ�ʽ��ÿ���ڵ���ӽڵ���Ŀ
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
		// ����һ��Դ�ڵ�,Ŀ�Ľڵ㣬����һ���ڵ�·��ID����
		RoutingTable routingTable = null;
		ArrayList<Integer> idList = new ArrayList<Integer>();// ��̬��ӽڵ�·��ID����
		Node nextNode = source;
		while (nextNode != destination) {
			idList.add(nodePool.getNodeID(nextNode));// ���ڵ��ID���м��붯̬����
			routingTable = nextNode.getTable();// �õ��м�ڵ��·�ɱ�
			nextNode = routingTable.getNext(destination);// ��·�ɱ��еõ�ȥĿ�Ľڵ����һ��
		}
		idList.add(nodePool.getNodeID(nextNode));// ��Ŀ�Ľڵ����ڵ�·��
		path = new int[idList.size()];

		int i = 0;
		for (Integer id : idList) { // ����̬��������ݸ���path������ӡ�ڵ�����
			path[i++] = id;
			System.out.println(i + " : " + id);
		}
		return path;
	}

}
