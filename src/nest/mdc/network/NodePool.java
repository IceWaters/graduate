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
 * 节点池类，这个类用来生成所有的节点并包含对节点的基本操作
 * 
 * @author：furui @version：1.0
 */

/**/
public class NodePool {
	public Map<Integer, Node> nodePoolWithID = new HashMap<Integer, Node>();// 由节点ID找到节点的map
	private Set<Node> NodeSet = new HashSet<Node>(); // 存放节点的set集合
	private ArrayList<Node> NodeList = new ArrayList<Node>(); // 存放节点的list集合,按剩余电量升序排列

	public NodePool() {
		create_NodePool();
	}

	/**
	 * 创造一个节点池，给每个节点初始化
	 */
	private void create_NodePool() {
		int xNum = (int) Math.sqrt(Field.iNodeSum);// 区域内每行的点数
		int yNum = xNum;// 区域内每列的点数
		int xInterveal = (int) (Field.iMaxX / xNum);// node所在的划分的小区域x长度
		int yInterval = xInterveal;// node所在的划分的小区域的长度
		double x, y, battery;
		int count = 0;// 每生成1个Node对象，count加1作为其ID
		Random Rand = new Random();
		Node node;
		node = new Node(0, 0, count);
		nodePoolWithID.put(count, node);
		NodeSet.add(node);
		count++;

		// count = setCenterNode(count);// 在网络中心布置一个基站
		// System.out.println("x : " + xNum + " y : " + yNum);
		/* 在网络中均匀布置开方数个点 */
		for (int i = 0; i < xNum; i++) {
			for (int j = 0; j < yNum; j++) {
				if(j == 0 && j == 0)
					continue;
				x = Rand.nextDouble() * xInterveal + i * xInterveal;// node的x坐标值
				y = Rand.nextDouble() * yInterval + j * yInterval;// node的y坐标值
				// // 制造有洞的网络
				// if (x >= 10 && x <= 100 && y >= 10 && y <= 120)
				// continue;
				// if (x >= 300 && x <= 400 && y >= 320 && y <= 400)
				// continue;
				battery = Rand.nextDouble() * Node.FullBattery; // node的电量
				node = new Node(x, y, count);
				node.setRemainingBattery(battery);
				nodePoolWithID.put(count, node);
				NodeSet.add(node);
				count++;
			}
		}

		/* 无法均匀布置于小区域中的点随机散步在网络中 */
		for (; count < Field.iNodeSum;) {
			x = Rand.nextDouble() * Field.iMaxX;
			y = Rand.nextDouble() * Field.iMaxY;
			// // 制造有洞的网络
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
		NodeList = new ArrayList<Node>(NodeSet); // 初始化NodeList
		// System.out.println("列表元素个数为"+NodeList.size());

		/* NodeSet加到list里面按ID大小进行升序排序打印 */
		ArrayList<Node> list = new ArrayList<Node>(NodeSet);
		Collections.sort(list, new Comparator<Node>() {

			public int compare(Node o1, Node o2) {
				return o1.getNodeID() - o2.getNodeID(); // 定义比较规则
			}
		});

		/* 输出节点坐标及ID */

		// Iterator iterator = list.iterator();
		// while(iterator.hasNext()){
		// Node node = (Node)(iterator.next());
		// System.out.println("第"+node.getNodeID()+"号节点的坐标为("
		// +node.getXCoordinate()+" , "
		// +node.getYCoordinate()+")");
		// }
	}

	/**
	 * 向节点池里添加节点
	 * 
	 * @param node
	 *            - 节点
	 */
	void addNode(Node node) {
		NodeSet.add(node);
	}

	/**
	 * 在网络中心布置一个基站
	 * 
	 * @param count
	 *            - 记录节点ID的计数器
	 */
	private int setCenterNode(int count) {
		Random Rand = new Random();
		double x = Field.iMaxX / 2;// node的x坐标值
		double y = Field.iMaxY / 2;// node的y坐标值
		double battery = Rand.nextDouble() * Node.FullBattery; // node的电量
		Node node = new Node(x, y, 0);
		node.setRemainingBattery(battery);
		nodePoolWithID.put(0, node);
		NodeSet.add(node);
		return 1;
	}

	/**
	 * 向节点池里添加中心基站
	 * 
	 * @param node
	 *            - 节点
	 */
	public void addCenterNode(Node node) {
		/* 在网络中心布置一个基站点 */
		if (getNodeWithID(0) == null) {
			nodePoolWithID.put(0, node);
			NodeSet.add(node);
			NodeList.add(node);
		}
	}

	/**
	 * 从节点池里删除节点
	 * 
	 * @param node
	 *            - 节点
	 */
	public void eraseNode(Node node) {
		NodeSet.remove(node);
		NodeList.remove(node);
		int id = getNodeID(node);
		nodePoolWithID.remove(id);
	}

	/**
	 * 得到指定节点的id
	 * 
	 * @param node
	 *            - 节点
	 * @return int - 参数节点的id
	 */
	public int getNodeID(Node node) {
		return node.getNodeID();
	}

	/**
	 * 得到节点池内节点总数
	 * 
	 * @return int - 节点总数
	 */
	public int getNodeNum() {
		return NodeSet.size();
	}

	/**
	 * 得到节点池内以ArrayList集合形式存放的所有节点
	 * 
	 * @return ArrayList - 节点集合
	 */
	public ArrayList<Node> getNodeList() {
		return NodeList;
	}

	/**
	 * 得到节点池内以Set集合形式存放的所有节点
	 * 
	 * @return Set - 节点集合
	 */
	public Set<Node> getNodeSet() {
		return NodeSet;
	}

	/**
	 * 得到节点池内以ArrayList集合存放的所有节点
	 * 
	 * @param ID
	 *            - 需要访问的某个节点的id
	 * @return Node - 返回与参数相同id的节点
	 */
	public Node getNodeWithID(int ID) {
		return nodePoolWithID.get(ID);
	}

	/**
	 * 返回剩余电量最小的某些点
	 * 
	 * @param TspNodeNum
	 *            - 目标节点集合的大小
	 * @return Set - 目标节点集合
	 */
	public Set<Node> findNodeSetWithBattery(int TspNodeNum) {
		Set<Node> nodeSet = new HashSet<Node>();
		int count = 0;

		Collections.sort(NodeList, new Comparator<Node>() { // 对NodeList按电量排序

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
		// 打印所有点
		// Iterator iterator = list.iterator();
		// while(iterator.hasNext()){
		// Node node = (Node)(iterator.next());
		// System.out.println("第"+node.getNodeID()+"的坐标为"
		// +node.getXCoordinate()+","
		// +node.getYCoordinate()+"剩余电量为"+node.getRemainingBattery());
		// }

		/* 打印并返回电量最低的某些个点 */
		Iterator<Node> iterator = NodeList.iterator();
		while (iterator.hasNext()) {
			Node node = (Node) (iterator.next());
			nodeSet.add(node);
			System.out.println("第" + node.getNodeID() + "的坐标为" + node.getXCoordinate() + "," + node.getYCoordinate()
					+ "剩余电量为" + node.getRemainingBattery());
			count++;
			if (count == TspNodeNum) {

				break;
			}
		}
		return nodeSet;
	}
	
	/**
	 * 清空节点的子节点
	 */
	public void clearChildren() {
		for(Node node : NodeSet)
			node.clearChildren();
	}

}
