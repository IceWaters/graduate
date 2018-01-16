package nest.mdc.field;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFrame;

import nest.mdc.algorithm.Algorithm;
import nest.mdc.algorithm.Tsp;
import nest.mdc.cluster.KCluster;
import nest.mdc.display.Display;
import nest.mdc.landform.ReadLandform;
import nest.mdc.network.Network;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;
import nest.mdc.redundant.RedundantNode;
import nest.mdc.routing.MyRouting;
import nest.mdc.routing.Routing;
import nest.mdc.timeclassifier.timeclassifier;

/**
 * the world
 * 
 * @author xiaoq
 * @version 1.0
 */
public class Field extends JFrame {
	public static int iNodeSum; // 网络中的节点数
	public static int iMaxX, iMinX, iMaxY, iMinY; // 网络大小
	public static Display display = null;
	private static Network network = null;
	static NodePool nodePool = null;
	private ServiceStation serviceStation;
	private static Node rootNode;
	private final double stationX, stationY; // 基站位置
	public static int distanceWeight1 = 30, distanceWeight2 = 30;
	public static int weight1 = 1, weight2 = 1;
	public static int clusterSize = 0;
	public static int count = 0;

	static {
		// 基本网络参数
		iNodeSum = 200;
		iMaxX = 800;
		iMinX = 0;
		iMaxY = 800;
		iMinY = 0;
		Node.commRange = 150;
	}

	{
		// 基站位置设定
		stationX = 0;
		stationY = 0;
	}

	/**
	 * Constructor
	 * 
	 * @param
	 */
	Field() {
		nodePool = new NodePool();
		network = new Network(nodePool);
		display = new Display(nodePool);
		serviceStation = new ServiceStation(stationX, stationY);
	}

	void test() {
		Node node1 = nodePool.getNodeWithID(0);
		Node node2 = nodePool.getNodeWithID(100);
		int[] path = network.routing(node1, node2, nodePool);
		display.drawLine(path, nodePool, 1, Color.black);
		System.out.println();
		System.out.println();
	}

	/**
	 * 测试在不同地形图下，算法结果的改变情况
	 * 
	 * @param name
	 *            ： 地形图的名称
	 * @throws IOException
	 */
	void testLandform(String name) throws IOException {
		ReadLandform readLandform = new ReadLandform(name);
		display.drawLandform(Field.iMaxX, Field.iMaxY, readLandform.getLandform());
		nodePool.addCenterNode(rootNode);// 初始化网络时，需要将中心基站，即根节点加入节点池内
		network = new Network(nodePool, readLandform.getLandform());
		try {
			Thread.sleep(100);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		drawChildren();

		nodePool.eraseNode(rootNode);
		timeclassifier timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
		timeclassifier1.initialOriginalCluster();
		int power1 = 0;
		double power2 = 0;
		for (Node node : nodePool.getNodeSet()) {
			// 计算能耗
			power1 = power1 + node.getChildrenNum();
			power2 = power2 + (double) (1 / (double) node.getChargingPeriod());
		}
		System.out.println("总能耗 ： " + power1);
		System.out.println("总能耗 ： " + power2);
		drawChargingPeriod(8);
		double averageTime = 0;
		int times = 15;
		for (int i = 0; i < times; i++) {
			System.out.print(i + " ");
			averageTime += timeclassifier1.runAlgXXX();
		}
		System.out.println("\n多次平均时间为 ：" + averageTime / times);
		testRedundantNode(500, timeclassifier1);
	}

	void drawNode(int size) {
		Color[] aColors = { Color.BLACK, Color.gray, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.yellow, Color.magenta, Color.pink, Color.darkGray };

	}

	/**
	 * 根据充电周期的大小给点画上不同的方块,叶子节点的充电周期是一样的
	 * 
	 * @param size
	 *            ： 方块的大小
	 */
	void drawHop(int size) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Color[] aColors = { Color.BLACK, Color.gray, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.yellow, Color.magenta, Color.pink, Color.darkGray };

		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;

			display.drawPoint(node, size, aColors[node.getHopNum()]);
		}
	}

	/**
	 * 将每个节点与它的子节点连接
	 * 
	 * @throws IOException
	 */
	void drawChildren() throws IOException {
		Set<Node> nodeSet = nodePool.getNodeSet();
		for (Node node : nodeSet) {
			Set<Node> childNodes = node.getChildren();
			for (Node childNode : childNodes)
				display.drawLine(node, childNode, 1, Color.black);
		}
	}

	@SuppressWarnings("unchecked")
	void drawNeighbor() {
		// Set<Node> nodeSet = nodePool.getNodeSet();
		Node node = nodePool.getNodeWithID(0);
		// System.out.println("neighbors : " + ((Set<Node>)
		// node.neighbors.get(1).getNeig()).size());
		// for (Node node : nodePool.getNodeSet()) {
		if (node.getChargingPeriod() < 2) {
			for (Node node2 : (Set<Node>) node.neighbors.get(1).getNeig()) {
				display.drawLine(node, node2, 1, Color.black);
			}
		}
		// }
	}

	/**
	 * 根据充电周期的大小给点画上不同的方块,叶子节点的充电周期是一样的
	 * 
	 * @param size
	 *            ： 方块的大小
	 */
	public static void drawChargingPeriod(int size) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Color[] aColors = { Color.BLACK, Color.gray, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.yellow, Color.magenta, Color.pink, Color.darkGray };
		int maxCharingPeriod = 0;
		// for (Node node : nodePool.getNodeSet()) {
		// if (node.getChargingPeriod() > maxCharingPeriod)
		// maxCharingPeriod = node.getChargingPeriod();
		// }

		// display.clearPicture();
		display.drawPoint(rootNode, 2, aColors[0]);// the center point
		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;

			int index = (int) (Math.log(node.getChargingPeriod()) / Math.log(2));
			// 未添加冗余节点前·
			// System.out.println(node.getNodeID() + " : " +
			// node.getChargingPeriod());
			// display.drawPoint(node, size, aColors[clusterSize - index - 1]);
			display.drawPoint(node, size, aColors[index]);
			// display.drawString(
			// String.valueOf(node.getChildrenNum()) + "," +
			// String.valueOf(node.getChargingPeriod()) + ","
			// + String.valueOf(clusterSize - index - 1),
			// (int) node.getXCoordinate(), (int) node.getYCoordinate());
		}

	}

	/**
	 * 测试增加冗余节点后，节点的充电周期是否改变
	 * 
	 * @param redundantNodeNum
	 *            ： 冗余节点总数
	 * @param timeclassifier1
	 *            ：
	 * @throws IOException
	 */
	void testRedundantNode(int redundantNodeNum, timeclassifier timeclassifier1) throws IOException {

		ArrayList<KCluster> oArrayList = timeclassifier1.getOriginalCluster();
		drawChargingPeriod(5);

		try {
			drawChildren();
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("*******Original*********");
		// for(KCluster cluster : oArrayList){
		// for(Node node : cluster.getNodeSet()){
		// System.out.println(node.getNodeID() + " : " +
		// node.getChargingPeriod());
		// }
		// System.out.println("\n");
		// }

		RedundantNode redundantNode = new RedundantNode(redundantNodeNum, oArrayList);
		// redundantNode.printResult();
		drawChargingPeriod(5);
		ArrayList<KCluster> kClusters = redundantNode.getClusters();
		// System.out.println("*******after*********");
		// System.out.println(kClusters.size());
		// for(KCluster cluster : kClusters){
		// for(Node node : cluster.getNodeSet()){
		// System.out.println(i++ + " " + node.getNodeID() + " : " +
		// node.getChargingPeriod());
		// }
		// System.out.println("\n");
		// }

		// int i = 0;
		// KCluster cluster = kClusters.get(0);
		// Node node = null;
		// if(cluster != null){
		// node = cluster.getRandomNode();
		// }
		// System.out.println(kClusters.size() + " the least : " +
		// node.getChargingPeriod() + " " +
		// (int)(Math.log(node.getChargingPeriod())/Math.log(2)));
		// for(i = (int)(Math.log(node.getChargingPeriod())/Math.log(2)) - 1; i
		// >= 0; i--){
		// KCluster cluster2 = new KCluster();
		// Node node2 = new Node(0,0, 1000 + i);
		// // System.out.println((int)Math.pow(2,i));
		// node2.setChargingPeriod((int)Math.pow(2,i));
		// cluster2.addNode(node2);
		// kClusters.add(0, cluster2);
		// count++;
		// }
		//
		// System.out.println("size : " + kClusters.size());

		// for(i = 0; i < kClusters.size(); i++){
		// System.out.println("*********");
		// System.out.println(Math.pow(2, i) + " : " +
		// kClusters.get(i).getNodeSet().size());
		//// for(Node node2 :kClusters.get(i).getNodeSet()){
		//// System.out.println(node2.getNodeID() + " : " +
		// node2.getChargingPeriod());
		//// }
		// }
		timeclassifier1.setOriginalClusters(kClusters);
		timeclassifier1.runAlgXXX();
	}

	void testTsp() {
		Tsp tsp = new Tsp(nodePool);
		int[] tspPath = tsp.run();
		display.drawLine(tspPath, nodePool, 2, Color.BLUE);
		this.setVisible(true); // 设置面板可见
	}

	void testCluster(Field field) {
		ArrayList<KCluster> clusterSet = Algorithm.k_Means_Cluster(nodePool);
		for (KCluster e : clusterSet) {
			int[] temp = e.getNodeId();
			display.drawLine(temp, nodePool, 2, Color.BLACK);
			field.setVisible(true); // 设置面板可见
		}
	}

	/**
	 * 从键盘读入想要访问的节点
	 * 
	 * @return node - the Node object is found for the id
	 */
	Node getNodeFromKeyboard() {
		int id = -1;
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.print("Please enter the node ID : ");
			id = scanner.nextInt();
			if (!nodePool.nodePoolWithID.containsKey(id)) {
				System.out.println("网络中无该id对应的节点");
			}
		} while (!nodePool.nodePoolWithID.containsKey(id)); // 判断是否存在该节点
		scanner.close();
		System.out.println("The ID of the node is in the nodePool! ");
		Node node = nodePool.getNodeWithID(id);
		return node;
	}

	/**
	 * start the application
	 * 
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		Field field = new Field();
		System.out.println(nodePool.getNodeNum());
		rootNode = nodePool.getNodeWithID(0);		
		
        //路由构造
		MyRouting routing = new MyRouting(nodePool);
		try {
			field.drawChildren();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		timeclassifier timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
//		timeclassifier1.runAlgXxxWithOneCharger();	
		timeclassifier1.runMyAlgrWithUAV();
		
//		field.clearChildren();
//		network.setChildrenNum();
//		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
//		timeclassifier1.runAlgXxxWithOneCharger();
	}
	
	
	void clearChildren() {
		for(Node node : nodePool.getNodeSet())
			node.clearChildren();
	}
}


class ServiceStation extends Point {
	private double X;
	private double Y;

	ServiceStation(double stationX, double stationY) {
		super(stationX, stationY);
	}
}
