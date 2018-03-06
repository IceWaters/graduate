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
import nest.mdc.network.Network;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.routing.MyRouting;
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
	public static int UAVCapacity;//无人机的飞行距离
	public static int uavNumber;//无人机数量
	public static Display display = null;
	public static Display display2 = null;
	private static Network network = null;
	static NodePool nodePool = null;
	private static Node rootNode;
	public static int distanceWeight1 = 30, distanceWeight2 = 30;
	public static int weight1 = 1, weight2 = 1;
	public static int clusterSize = 0;
	public static int count = 0;
	public static int period = 7;
    //工人的行进速度和无人机的飞行速度暂定一样
	static {
		// 基本网络参数
		iNodeSum = 10;
		iMaxX = 800;
		iMinX = 0;
		iMaxY = 800;
		iMinY = 0;
		Node.commRange = 600;
		UAVCapacity = 2400;//须大于从基地到最远点的来回距离
		uavNumber = 4;
		period = 5;
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
//		display2 = new Display(nodePool);
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
	 * 在图上表明节点的id
	 */
	void drawNodeId(Display display) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;
			display.drawString(String.valueOf(node.getNodeID()), (int)node.getXCoordinate() + 5, (int)node.getYCoordinate());
		}
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
	void drawChildren(Display display) throws IOException {
		Set<Node> nodeSet = nodePool.getNodeSet();
		for (Node node : nodeSet) {
			Set<Node> childNodes = node.getChildren();
			for (Node childNode : childNodes)
				display.drawLine(node, childNode, 1, Color.black);
		}
	}

	void drawNeighbor(Display display) {
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
	public static void drawChargingPeriod(Display display, int size) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Color[] aColors = { Color.BLACK, Color.yellow, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.magenta, Color.pink, Color.darkGray };
		display.drawPoint(rootNode, 2, aColors[0]);// the center point
		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;
			int index = (int) Math.ceil(Math.log(node.getWeight()) / Math.log(2));		
			display.drawPoint(node, size, aColors[index]);		
		}
	}
	
	/**
	 * 根据充电周期的大小给点画上不同的方块,叶子节点的充电周期是一样的
	 * 
	 * @param size
	 *            ： 方块的大小
	 */
	public static void drawChargingPeriod2(Display display, int size) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Color[] aColors = { Color.BLACK, Color.yellow, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.magenta, Color.pink, Color.darkGray };
		display.drawPoint(rootNode, 2, aColors[0]);// the center point
		for (Node node : nodePool.getNodeSet()) {
			if (node.getNodeID() == 0)
				continue;
			int index = (int) (Math.log(node.getChildrenNum()) / Math.log(2));		
			display.drawPoint(node, size, aColors[index]);		
		}
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
		timeclassifier timeclassifier1;
        //路由构造
		MyRouting routing = new MyRouting(nodePool);

		double minTime = 1000000;
		for(int i = 0; i < 10; i++) {
			nodePool.clearChildren();
			routing.run();
			timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
			double time = timeclassifier1.runAlgXxxWithOneCharger();
			if(minTime > time) {
				minTime = time;
				routing.setParentChildren();				
			}
			System.out.println(minTime);
		}		
		
		System.out.println("\n\nthe old one:");
		network.setChildrenNum();
		try {
			field.drawChildren(display);
			field.drawNodeId(display);
			drawChargingPeriod(display, 7);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
		timeclassifier1.runAlgXxxWithOneCharger();
		
		

//		field.clearChildren();
//		network.setChildrenNum();
//		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
//		timeclassifier1.runAlgXXX();
		
	}
}

