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
	public static int iNodeSum; // �����еĽڵ���
	public static int iMaxX, iMinX, iMaxY, iMinY; // �����С
	public static Display display = null;
	private static Network network = null;
	static NodePool nodePool = null;
	private ServiceStation serviceStation;
	private static Node rootNode;
	private final double stationX, stationY; // ��վλ��
	public static int distanceWeight1 = 30, distanceWeight2 = 30;
	public static int weight1 = 1, weight2 = 1;
	public static int clusterSize = 0;
	public static int count = 0;

	static {
		// �����������
		iNodeSum = 30;
		iMaxX = 800;
		iMinX = 0;
		iMaxY = 800;
		iMinY = 0;
		Node.commRange = 300;
	}

	{
		// ��վλ���趨
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


	void drawNode(int size) {
		Color[] aColors = { Color.BLACK, Color.gray, Color.cyan, Color.red, Color.blue, Color.orange, Color.green,
				Color.yellow, Color.magenta, Color.pink, Color.darkGray };

	}
	
	/**
	 * ��ͼ�ϱ����ڵ��id
	 */
	void drawNodeId() {
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
	 * ���ݳ�����ڵĴ�С���㻭�ϲ�ͬ�ķ���,Ҷ�ӽڵ�ĳ��������һ����
	 * 
	 * @param size
	 *            �� ����Ĵ�С
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
	 * ��ÿ���ڵ��������ӽڵ�����
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
	 * ���ݳ�����ڵĴ�С���㻭�ϲ�ͬ�ķ���,Ҷ�ӽڵ�ĳ��������һ����
	 * 
	 * @param size
	 *            �� ����Ĵ�С
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
			// δ�������ڵ�ǰ��
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

	

	void testTsp() {
		Tsp tsp = new Tsp(nodePool);
		int[] tspPath = tsp.run();
		display.drawLine(tspPath, nodePool, 2, Color.BLUE);
		this.setVisible(true); // �������ɼ�
	}

	void testCluster(Field field) {
		ArrayList<KCluster> clusterSet = Algorithm.k_Means_Cluster(nodePool);
		for (KCluster e : clusterSet) {
			int[] temp = e.getNodeId();
			display.drawLine(temp, nodePool, 2, Color.BLACK);
			field.setVisible(true); // �������ɼ�
		}
	}

	/**
	 * �Ӽ��̶�����Ҫ���ʵĽڵ�
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
				System.out.println("�������޸�id��Ӧ�Ľڵ�");
			}
		} while (!nodePool.nodePoolWithID.containsKey(id)); // �ж��Ƿ���ڸýڵ�
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
		
        //·�ɹ���
		MyRouting routing = new MyRouting(nodePool);
		try {
			field.drawChildren();
			field.drawNodeId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		timeclassifier timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
//		timeclassifier1.runAlgXxxWithOneCharger();	
		timeclassifier1.runMyAlgrWithUAV();
		
		field.clearChildren();
		network.setChildrenNum();
		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
		timeclassifier1.runMyAlgrWithUAV();
		
		field.clearChildren();
		network.setChildrenNum();
		timeclassifier1 = new timeclassifier(nodePool.getNodeList(), nodePool);
		timeclassifier1.runAlgXxxWithOneCharger();
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
