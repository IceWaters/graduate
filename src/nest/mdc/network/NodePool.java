/**
 * 
 */
package nest.mdc.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import nest.mdc.field.Field;

/**
 * �ڵ���࣬����������������еĽڵ㲢�����Խڵ�Ļ�������
 * 
 * @author��furui @version��1.0
 */

/**/
public class NodePool {
	public Map<Integer, Node> nodePoolWithID = new HashMap<Integer, Node>();// �ɽڵ�ID�ҵ��ڵ��map
	private Set<Node> NodeSet = new HashSet<Node>(); // ��Žڵ��set����
	private ArrayList<Node> NodeList = new ArrayList<Node>(); // ��Žڵ��list����,��ʣ�������������

	public NodePool() {
		create_NodePool();
	}

	/**
	 * ����һ���ڵ�أ���ÿ���ڵ��ʼ��
	 */
	private void create_NodePool() {
		int xNum = (int) Math.sqrt(Field.iNodeSum);// ������ÿ�еĵ���
		int yNum = xNum;// ������ÿ�еĵ���
		int xInterveal = (int) (Field.iMaxX / xNum);// node���ڵĻ��ֵ�С����x����
		int yInterval = xInterveal;// node���ڵĻ��ֵ�С����ĳ���
		double x, y, battery;
		int count = 0;// ÿ����1��Node����count��1��Ϊ��ID
		Random Rand = new Random();
		Node node;
		node = new Node(0, 0, count);
		nodePoolWithID.put(count, node);
		NodeSet.add(node);
		count++;

		// count = setCenterNode(count);// ���������Ĳ���һ����վ
		// System.out.println("x : " + xNum + " y : " + yNum);
		/* �������о��Ȳ��ÿ��������� */
		for (int i = 0; i < xNum; i++) {
			for (int j = 0; j < yNum; j++) {
				if(j == 0 && j == 0)
					continue;
				x = Rand.nextDouble() * xInterveal + i * xInterveal;// node��x����ֵ
				y = Rand.nextDouble() * yInterval + j * yInterval;// node��y����ֵ
				// // �����ж�������
				// if (x >= 10 && x <= 100 && y >= 10 && y <= 120)
				// continue;
				// if (x >= 300 && x <= 400 && y >= 320 && y <= 400)
				// continue;
				battery = Rand.nextDouble() * Node.FullBattery; // node�ĵ���
				node = new Node(x, y, count);
				node.setRemainingBattery(battery);
				nodePoolWithID.put(count, node);
				NodeSet.add(node);
				count++;
			}
		}

		/* �޷����Ȳ�����С�����еĵ����ɢ���������� */
		for (; count < Field.iNodeSum;) {
			x = Rand.nextDouble() * Field.iMaxX;
			y = Rand.nextDouble() * Field.iMaxY;
			// // �����ж�������
			// if (x >= 10 && x <= 100 && y >= 10 && y <= 120)
			// continue;
			// if (x >= 300 && x <= 400 && y >= 320 && y <= 400)
			// continue;
			battery = Rand.nextDouble() * Node.FullBattery;
			node = new Node(x, y, count);
			node.setRemainingBattery(battery);
			nodePoolWithID.put(count, node);
			NodeSet.add(node);
			count++;
		}
		NodeList = new ArrayList<Node>(NodeSet); // ��ʼ��NodeList
		// System.out.println("�б�Ԫ�ظ���Ϊ"+NodeList.size());

		/* NodeSet�ӵ�list���水ID��С�������������ӡ */
		ArrayList<Node> list = new ArrayList<Node>(NodeSet);
		Collections.sort(list, new Comparator<Node>() {

			public int compare(Node o1, Node o2) {
				return o1.getNodeID() - o2.getNodeID(); // ����ȽϹ���
			}
		});

		/* ����ڵ����꼰ID */

		// Iterator iterator = list.iterator();
		// while(iterator.hasNext()){
		// Node node = (Node)(iterator.next());
		// System.out.println("��"+node.getNodeID()+"�Žڵ������Ϊ("
		// +node.getXCoordinate()+" , "
		// +node.getYCoordinate()+")");
		// }
	}

	/**
	 * ��ڵ������ӽڵ�
	 * 
	 * @param node
	 *            - �ڵ�
	 */
	void addNode(Node node) {
		NodeSet.add(node);
	}

	/**
	 * ���������Ĳ���һ����վ
	 * 
	 * @param count
	 *            - ��¼�ڵ�ID�ļ�����
	 */
	private int setCenterNode(int count) {
		Random Rand = new Random();
		double x = Field.iMaxX / 2;// node��x����ֵ
		double y = Field.iMaxY / 2;// node��y����ֵ
		double battery = Rand.nextDouble() * Node.FullBattery; // node�ĵ���
		Node node = new Node(x, y, 0);
		node.setRemainingBattery(battery);
		nodePoolWithID.put(0, node);
		NodeSet.add(node);
		return 1;
	}

	/**
	 * ��ڵ����������Ļ�վ
	 * 
	 * @param node
	 *            - �ڵ�
	 */
	public void addCenterNode(Node node) {
		/* ���������Ĳ���һ����վ�� */
		if (getNodeWithID(0) == null) {
			nodePoolWithID.put(0, node);
			NodeSet.add(node);
			NodeList.add(node);
		}
	}

	/**
	 * �ӽڵ����ɾ���ڵ�
	 * 
	 * @param node
	 *            - �ڵ�
	 */
	public void eraseNode(Node node) {
		NodeSet.remove(node);
		NodeList.remove(node);
		int id = getNodeID(node);
		nodePoolWithID.remove(id);
	}

	/**
	 * �õ�ָ���ڵ��id
	 * 
	 * @param node
	 *            - �ڵ�
	 * @return int - �����ڵ��id
	 */
	public int getNodeID(Node node) {
		return node.getNodeID();
	}

	/**
	 * �õ��ڵ���ڽڵ�����
	 * 
	 * @return int - �ڵ�����
	 */
	public int getNodeNum() {
		return NodeSet.size();
	}

	/**
	 * �õ��ڵ������ArrayList������ʽ��ŵ����нڵ�
	 * 
	 * @return ArrayList - �ڵ㼯��
	 */
	public ArrayList<Node> getNodeList() {
		return NodeList;
	}

	/**
	 * �õ��ڵ������Set������ʽ��ŵ����нڵ�
	 * 
	 * @return Set - �ڵ㼯��
	 */
	public Set<Node> getNodeSet() {
		return NodeSet;
	}

	/**
	 * �õ��ڵ������ArrayList���ϴ�ŵ����нڵ�
	 * 
	 * @param ID
	 *            - ��Ҫ���ʵ�ĳ���ڵ��id
	 * @return Node - �����������ͬid�Ľڵ�
	 */
	public Node getNodeWithID(int ID) {
		return nodePoolWithID.get(ID);
	}

	/**
	 * ����ʣ�������С��ĳЩ��
	 * 
	 * @param TspNodeNum
	 *            - Ŀ��ڵ㼯�ϵĴ�С
	 * @return Set - Ŀ��ڵ㼯��
	 */
	public Set<Node> findNodeSetWithBattery(int TspNodeNum) {
		Set<Node> nodeSet = new HashSet<Node>();
		int count = 0;

		Collections.sort(NodeList, new Comparator<Node>() { // ��NodeList����������

			public int compare(Node o1, Node o2) {
				if ((o1.getRemainingBattery() - o2.getRemainingBattery()) > 0) {
					return 1;
				} else if ((o1.getRemainingBattery() - o2.getRemainingBattery()) < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		// ��ӡ���е�
		// Iterator iterator = list.iterator();
		// while(iterator.hasNext()){
		// Node node = (Node)(iterator.next());
		// System.out.println("��"+node.getNodeID()+"������Ϊ"
		// +node.getXCoordinate()+","
		// +node.getYCoordinate()+"ʣ�����Ϊ"+node.getRemainingBattery());
		// }

		/* ��ӡ�����ص�����͵�ĳЩ���� */
		Iterator<Node> iterator = NodeList.iterator();
		while (iterator.hasNext()) {
			Node node = (Node) (iterator.next());
			nodeSet.add(node);
			System.out.println("��" + node.getNodeID() + "������Ϊ" + node.getXCoordinate() + "," + node.getYCoordinate()
					+ "ʣ�����Ϊ" + node.getRemainingBattery());
			count++;
			if (count == TspNodeNum) {

				break;
			}
		}
		return nodeSet;
	}
	
	/**
	 * ��սڵ���ӽڵ�
	 */
	public void clearChildren() {
		for(Node node : NodeSet)
			node.clearChildren();
	}

}
