/**
 * 在充电时间上进行聚类
 */
/**
 * @author lujunqiu
 * @version 1.0
 */
package nest.mdc.timeclassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nest.mdc.algorithm.NewTsp;
import nest.mdc.algorithm.TspVersion2;
import nest.mdc.cluster.Basic_Kmeans;
import nest.mdc.cluster.Bisecting_k_means;
import nest.mdc.cluster.Classifier;
import nest.mdc.cluster.KCluster;
import nest.mdc.field.Field;
import nest.mdc.network.CollectionNode;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;
import nest.mdc.uav.RouteWithoutDepot;
import nest.mdc.uav.Sweep;

public class timeclassifier {
	private NodePool nodePool;
	private ArrayList<Node> dataSet;
	private ArrayList<KCluster> originalCluster; // 存放大类
	private Map<KCluster, ArrayList<KCluster>> clusterMap; // 存放大类和其所分的小类
	// private ArrayList<Set<Node>> scheduledDays = new
	// ArrayList<>();//存放每天的充电节点
	private int energyParameter;// esync算法中的参数
	final private int workerSpeed = 1;// 工人的行进速度，单位：米每秒
	final private double chargingTime = 1;// 每个传感器充电时间，单位：秒
	//final private int T = 128;
	
	
	//UAV参数
	private final int uavSpeed = 1;//无人机的飞行速度
	private HashMap<RouteWithoutDepot, Integer> hashMap;//调度的结果保存在Map之中,Integer值标识了无人机的id
    private HashMap<Integer, Set<RouteWithoutDepot>> integerSetHashMap;//调度结果的另一种保存方式，更换了Key与Value
	
	/**
	 * constructor
	 * 
	 * @param allSet
	 */
	public timeclassifier(ArrayList<Node> allSet, NodePool nodePool) { // 在field里面申明一个timeClassifier对象，传nodePool进去
		this.nodePool = nodePool;
		dataSet = new ArrayList<Node>();
		originalCluster = new ArrayList<KCluster>();
		clusterMap = new HashMap<KCluster, ArrayList<KCluster>>();
		// chargingNodes = new HashSet<>();
		energyParameter = 2;
		this.dataSet.addAll(allSet);
	}

	/**
	 * @return
	 * @throws FileNotFoundException
	 */
	public ArrayList<KCluster> getOriginalCluster() throws FileNotFoundException {
		initialOriginalCluster();
		return originalCluster;
	}

	/**
	 * @return
	 */
	public Map<KCluster, ArrayList<KCluster>> getClusterMap() {
		return clusterMap;
	}

	/**
	 * 直接赋值初始聚类情况
	 * 
	 * @param originalCluster2
	 *            ：外界传进的初始聚类情况
	 */
	public void setOriginalClusters(ArrayList<KCluster> originalCluster2) {
		this.originalCluster = originalCluster2;

		// 确保每次的第一个聚类里面的周期一样
		KCluster cluster = originalCluster.get(0);
		Node node = null;
		if (cluster != null) {
			node = cluster.getRandomNode();
		}
		System.out.println(originalCluster.size() + " the least : " + node.getChargingPeriod() + " "
				+ (int) (Math.log(node.getChargingPeriod()) / Math.log(2)));
		for (int i = (int) (Math.log(node.getChargingPeriod()) / Math.log(2)) - 1; i >= 0; i--) {
			KCluster cluster2 = new KCluster();
			Node node2 = new Node(0, 0, 1000 + i);
			// System.out.println((int)Math.pow(2,i));
			node2.setChargingPeriod((int) Math.pow(2, i));
			cluster2.addNode(node2);
			originalCluster.add(0, cluster2);
		}

		// 补充刚开始的几个空类，添加无用点
		for (int i = 0; i < originalCluster.size(); i++) {
			KCluster cluster2 = originalCluster.get(i);
			if (cluster2.getNodeSet().size() == 0) {
				Node node2 = new Node(0, 0, 1000 + i);
				// System.out.println((int)Math.pow(2,i));
				node2.setChargingPeriod((int) Math.pow(2, i));
				cluster2.addNode(node2);
				originalCluster.set(i, cluster2);
			}
			// count++;
		}
	}

	/**
	 * 算法XXX
	 * 
	 * @throws FileNotFoundException
	 */
	public double runAlgXXX() throws FileNotFoundException {
		initialOriginalCluster(); //将节点按充电速率分为若干个大类
		initClusterMap(); // 将大类分成小类，这里要注意对于不同的大类其分成小类的个数也不一样
		ArrayList<Set<Node>> nodeSet = linkSubclass();// 调度算法，得到每天的充电节点的集合
		int day = 1;
		double averageTime = 0;
		double maxTime = 0;
		int maxTimeDay = 0;
		for (Set<Node> set : nodeSet) {// 对每天的充电节点进行聚类，TSP算法，计算时间，距离的值
			// if(set.size() == 0){
			// System.out.println( "第" + day +"天的充电时间总花费为 ： " + 0);
			// day++;
			// continue;
			// }
			Set<Node> removeElements = new HashSet<>();
			for (Node node : set) {
				if (node.getNodeID() >= 1000)
					removeElements.add(node);
			}
			set.removeAll(removeElements);
			// System.out.print(node.getNodeID() + " ");
			// System.out.println();
			ArrayList<Node> aList = new ArrayList<>();
			aList.addAll(set);
			Classifier classifier = new Bisecting_k_means(aList);
			classifier.classify();// 2分k均值聚类
			ArrayList<KCluster> clusterSet = new ArrayList<KCluster>();
			clusterSet = classifier.getClusters();// 2分K均值聚类的结果
			System.out.println("size : " + clusterSet.size());
			double totalTime = 0;// 每天完成充电节点的任务的总时间花费
			// 在族之间进行简化的tspn运算，得到族与族之间的路由顺序
			Set<Node> tspnNodeSet = new HashSet<Node>();
			for (KCluster kCluster : clusterSet) {// 得到每个族中距离基站最近的节点
				tspnNodeSet.add(kCluster.getTspnNode());
			}
			// 计算得到tspn的时间花费，tspn路径花费
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(tspnNodeSet, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<Node>();
			nList = nTsp.startTsp();
			for (int i = 0; i < nList.size() - 1; i++) {
				totalTime += nList.get(i).getDistance(nList.get(i + 1)) / workerSpeed;
			} // 计算得到tspn的时间花费
			totalTime += nList.get(0).getDistance(nList.get(nList.size() - 1)) / workerSpeed;
			// System.out.println( "第" + day +"天的tspn时间花费为 ： " + totalTime);
			// 每个族进行Tsp运算,得到每个族内tsp的时间

			int num = 0;
			// System.out.println("**********");
			for (KCluster kCluster : clusterSet) {
				Set<Node> tempNodes = new HashSet<>();
				for (Node node : kCluster.getNodeSet()) {
					// System.out.print(node.getNodeID() + " ");
					if (node.getNodeID() >= 1000) {
						System.out.println(node.getNodeID());
						tempNodes.add(node);
					}
				}
				for (Node node : tempNodes)
					kCluster.deleteNode(node);
				double tspTime;
				if (kCluster.getNodeSet().size() == 0)
					tspTime = 0;
				else {
					// System.out.println();
					Set<Node> aSet = kCluster.getNodeSet();
					Set<CollectionNode> collectionNodes = new HashSet<>();
					TspVersion2 tspVersion2 = new TspVersion2(aSet, collectionNodes);
					NewTsp newTsp = new NewTsp(tspVersion2.convertToTspNode());
					ArrayList<Node> nodeList = new ArrayList<>();
					nodeList = newTsp.startTsp();
					tspTime = getTspChargingTime(nodeList);// 得到Tsp的时间花费
				}
				totalTime += tspTime;// 计算入总时间
				// for(int i = 0;i < nodeList.size();i++){
				// System.out.println("nodeList第"+ i + "点的横坐标" +
				// nodeList.get(i).getXCoordinate() +
				// ",纵坐标为 "+ nodeList.get(i).getYCoordinate());
				// }
			}
			// System.out.println( "第" + day +"天的充电时间总花费为 ： " + totalTime);
			if (totalTime > maxTime) {
				maxTime = totalTime;
				maxTimeDay = day;
			}
			day++;
			averageTime += totalTime;
		}
		averageTime = averageTime / day;
		System.out.println("最大时间花费在第" + maxTimeDay + "天 为：" + maxTime);
		System.out.println("平均时间为 ：" + averageTime);
		return averageTime;
	}

	/**
	 * 测试用的数据
	 */
	public void test() {
		// ArrayList<KCluster> testArray = new ArrayList<>();
		KCluster k1 = new KCluster();
		KCluster k2 = new KCluster();
		KCluster k3 = new KCluster();
		KCluster k4 = new KCluster();
		for (Node node : dataSet) {
			double d = node.getDistance(new Point(0, 0));
			if (d <= 125 * Math.sqrt(2) && d > 0) {
				k1.addNode(node);
				node.setChargingPeriod(1);
			}
			if (d <= 250 * Math.sqrt(2) && d > 125 * Math.sqrt(2)) {
				k2.addNode(node);
				node.setChargingPeriod(2);
			}
			if (d <= 375 * Math.sqrt(2) && d > 250 * Math.sqrt(2)) {
				k3.addNode(node);
				node.setChargingPeriod(4);
			}
			if (d <= 500 * Math.sqrt(2) && d > 375 * Math.sqrt(2)) {
				k4.addNode(node);
				node.setChargingPeriod(8);
			}
		}
		k1.calCenterPoint();
		k2.calCenterPoint();
		k3.calCenterPoint();
		k4.calCenterPoint();
		originalCluster.add(k1);
		originalCluster.add(k2);
		originalCluster.add(k3);
		originalCluster.add(k4);
		// return testArray;
	}

	/**
	 * 将大类分成小类，这里要注意对于不同的大类根据充电周期其分成小类的个数也不一样
	 */
	public void initClusterMap() {
		for (KCluster kCluster : originalCluster) {
			int i;
			if (kCluster.getRandomNode() == null) {
				i = 0;
			} else {
				i = kCluster.getRandomNode().getChargingPeriod();// 充电周期
			}
			int n = kCluster.getNodeSet().size();// 每个类的节点数
			ArrayList<KCluster> subCluster = new ArrayList<>(); // 存储子类结果
			subCluster.add(kCluster);
			clusterMap.put(kCluster, subCluster);
			KCluster temp11 = new KCluster();
			temp11.getNodeSet().addAll(kCluster.getNodeSet());
			// if (n < 1.7*i) {//所分子类个数的调整
			// i = (int)Math.sqrt(n) ;
			// }
			i = (int) Math.sqrt(n) >= i ? i : (int) Math.sqrt(n);
			for (int j = 0; j < i - 1; j++) {
				ArrayList<Double> sse = new ArrayList<>();
				Map<Double, KCluster> map = new HashMap<>();
				for (KCluster kCluster2 : subCluster) {
					sse.add(kCluster2.SSE());
					map.put(kCluster2.SSE(), kCluster2);
				}
				Double[] aa = sse.toArray(new Double[sse.size()]);
				Arrays.sort(aa);
				temp11 = map.get(aa[sse.size() - 1]);
				subCluster.remove(map.get(aa[sse.size() - 1]));// 拿出sse最大的子类，继续分类；
				ArrayList<KCluster> temp = bisectKcluster(temp11);
				subCluster.addAll(temp);
				// clusterMap.put(kCluster, subCluster);
			}
		}
	}

	/**
	 * 利用2分K聚类将一个kcluster分为2个kcluster
	 */
	private ArrayList<KCluster> bisectKcluster(KCluster tCluster) {
		ArrayList<KCluster> subKcluster = new ArrayList<>();
		ArrayList<Double> SSE = new ArrayList<>();
		Map<Double, ArrayList<KCluster>> map = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			Basic_Kmeans basic_Kmeans = new Basic_Kmeans(); // 默认K=2的基本K聚类
			basic_Kmeans.basic_k_means(tCluster.getNodeSet());
			ArrayList<KCluster> a = basic_Kmeans.getCluster();
			double sse = 0;
			for (KCluster kCluster2 : a) {
				double i1 = kCluster2.SSE();
				sse = sse + i1;
			}
			SSE.add(sse); // 每次聚类的总sse
			map.put(sse, a);
		}
		Double[] aa = SSE.toArray(new Double[10]);
		Arrays.sort(aa);
		subKcluster.addAll(map.get(aa[0]));
		return subKcluster;
	}

	/**
	 * 返回每天的充电节点集合 子类连接起来，讲节点存入Set用作一天的充电节点
	 */
	public ArrayList<Set<Node>> linkSubclass() {
		for (KCluster kCluster : originalCluster) {// 填充空子类，子类数目达到充电周期数
			// int t = kCluster.getRandomNode().getChargingPeriod();//充电周期
			int t;
			if (kCluster.getRandomNode() == null) {
				t = 0;
			} else {
				t = kCluster.getRandomNode().getChargingPeriod();// 充电周期
			}
			int n = clusterMap.get(kCluster).size();// 实际所分的子类的个数
			if (n < t) {// 填充空子类
				for (int i = 0; i < t - n; i++) {
					KCluster nullCluster = new KCluster();// 质心默认(0,0)
					nullCluster.setNodeSet(null);
					clusterMap.get(kCluster).add(nullCluster);
				}
			}
		}

		int size = originalCluster.size();// 大类的个数
		KCluster cluster = originalCluster.get(size - 1);
		Node node = null;
		if (cluster != null) {
			node = cluster.getRandomNode();
		}
		double maxPeriod = node.getChargingPeriod();// 最大的充电周期
		// 返回值
		ArrayList<Set<Node>> scheduledDays = new ArrayList<>();// 存放每天的充电节点的集合

		for (int i = 0; i < maxPeriod; i++) {
			Set<Node> scheduleDay = new HashSet<>();
			
			for (int index = 0; index < originalCluster.size() - 1; index++) {
				KCluster tCluster = new KCluster(); 
				if (clusterMap.get(originalCluster.get(index)).size() == 1) {
					tCluster = clusterMap.get(originalCluster.get(index)).get(0);
					scheduleDay.addAll(tCluster.getNodeSet());
				}
				// 选择出之前collection中没有选到的子类来进行schedule
				ArrayList<KCluster> chooseClusters = new ArrayList<>();
				for (KCluster kCluster : clusterMap.get(originalCluster.get(index + 1))) {
					if (kCluster.getTag() == 0) {
						chooseClusters.add(kCluster);
					} else {
						kCluster.addTag();
					}
					if (kCluster.getTag() == (int)Math.pow(2, index + 1)) {
						kCluster.setTag(0);
					}
				}
				//
				if(tCluster != null && chooseClusters.size() != 0)
					tCluster = chooseKclucster(chooseClusters, tCluster);
				tCluster.addTag();
				if (tCluster.getNodeSet() != null) {
					// System.out.println(tCluster.getNodeSet());
					scheduleDay.addAll(tCluster.getNodeSet());
				}
			}
			scheduledDays.add(scheduleDay);
			// System.out.println(scheduleDay.size());
		}
		return scheduledDays;
	}

	/**
	 * 辅助函数 找到离某个族最近族(用质心的距离来描述)
	 */
	private KCluster chooseKclucster(ArrayList<KCluster> temp, KCluster kCluster) {
		kCluster.calCenterPoint();
		// System.out.println(temp.size());
		ArrayList<Double> dis = new ArrayList<>();
		Map<Double, KCluster> map = new HashMap<>();
		for (KCluster kCluster2 : temp) {
			kCluster2.calCenterPoint();
			double d = kCluster.getDistance(kCluster2);
			// double d =
			// kCluster.getCenterPoint().getDistance(kCluster2.getCenterPoint());
			dis.add(d);
			map.put(d, kCluster2);
		}
		Double[] dd = dis.toArray(new Double[dis.size()]);
		Arrays.sort(dd);
		// System.out.println(dis.size());
		// map.get(dd[0]).addTag();

		return map.get(dd[0]);
	}

	/**
	 * 将节点按充电速率分为若干个大类
	 * 
	 * @throws FileNotFoundException
	 */
	public void initialOriginalCluster() throws FileNotFoundException {
		File file = new File("./file.txt");// 将结果打印到txt文本中
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream p = new PrintStream(fos);
		int temp = Field.period;

		double min = 0; // 
		double max = 0;//最大的传输通信量
		for (Node node : dataSet) {
			if (node.getNodeID() == 0)
				 // 如果节点Id为0，跳过，执行下一个循环
				 continue;
			double num = node.getWeight();
			if (num > max)
				max = num;			
		}

		int m = (int)Math.ceil(Math.log(max) / Math.log((double) 2)) + 1; // 分类区间数目

		for (int i = 0; i < m; i++) {
			// 初始化originalCluster
			KCluster tempClusters = new KCluster();
			originalCluster.add(tempClusters);
		}

		for (Node node : dataSet) {
			if (node.getNodeID() == 0)
			// 如果节点Id为0，跳过，执行下一个循环
				continue;
			double pcr = node.getWeight(); // 耗电速率 power consumption rate
			p.println(node.getNodeID() + "\t:\t" + pcr); // 打印节点ID 和 耗电速率

			if (pcr > min && pcr <= 1.0) {
				// 当耗电速率在第一个区间内
				originalCluster.get(m - 1).addNode(node);
				node.setChargingPeriod((int) Math.pow(energyParameter, temp - 1));
			} else {
				// 循环求出耗电速率区间
				for (int i = 1; i < m; i++) {
					if (pcr > Math.pow(energyParameter, m - i - 1)
							&& pcr <= Math.pow(energyParameter, m - i)) {
						originalCluster.get(i - 1).addNode(node);
						node.setChargingPeriod((int) Math.pow(energyParameter, temp - m + i - 1));
						break;
					}
				}
			}
		}

		// 打印分类结果
		System.out.println("\n分类结果：");
		for (int i = 0; i < originalCluster.size() - 1; i++) {
			System.out.println("(" + (double)Math.pow(energyParameter, m - i - 2) + ","
					+ (double)  Math.pow(energyParameter, m - i - 1) + "] : "
					+ originalCluster.get(m - i - 1).getNodeSet().size());
		}
		System.out.println("(0.0,1.0] : " + originalCluster.get(0).getNodeSet().size());

		// 补充刚开始的几个空类，添加无用点
		for (int i = 0; i < m; i++) {
			KCluster cluster2 = originalCluster.get(i);
			if (cluster2.getNodeSet().size() == 0) {
				Node node2 = new Node(0, 0, 1000 + i);
				node2.setChargingPeriod((int) Math.pow(2, i));
				cluster2.addNode(node2);
				originalCluster.set(i, cluster2);
			}
		}
						
		if(originalCluster.size() != temp) {
			ArrayList<KCluster> tempCluster = new ArrayList<>();
			for (int i = 0; i < temp - m; i++) {
				KCluster clusterTemp = new KCluster();
				Node node2 = new Node(0, 0, 2000 + i);
				// System.out.println((int)Math.pow(2,i));
				node2.setChargingPeriod((int) Math.pow(2, i));
				clusterTemp.addNode(node2);
				tempCluster.add(clusterTemp);
			}
			for(int i = temp - m; i < temp; i++) {
				tempCluster.add(originalCluster.get(i - temp + m));
			}
			originalCluster = tempCluster;
			// 将分类结果打印在txt文档中
			System.out.println("\n分类结果：");
			for (int i = 0; i < originalCluster.size() - 1; i++) {
				System.out.println("(" + (double)Math.pow(energyParameter, temp - i - 2) + ","
						+ (double)  Math.pow(energyParameter, temp - i - 1) + "] : "
						+ originalCluster.get(temp - i - 1).getNodeSet().size());
			}
			System.out.println("(0.0,1.0] : " + originalCluster.get(0).getNodeSet().size());
		}
		p.close();
	}

	/**
	 * 将esync算法聚类的结果进行tsp运算，得到该算法的运行时间花费
	 * 
	 * @throws FileNotFoundException
	 */
	public void runAlgEsync() throws FileNotFoundException {
		initialOriginalCluster(); // 将节点按充电速率分为若干个大类
		int maxPeriod = (int) Math.pow(2, originalCluster.size() - 1);
		// double esyncChargingTime = 0 ; //Esync算法的时间花费
		double averageTime = 0;// 存储平均时间
		for (int day = 1; day <= maxPeriod; day++) {
			double esyncChargingTime = 0; // Esync算法的时间花费
			Set<Node> dayNodes = getTheDayNodes(day);// 得到day天的充电节点集合
			// 对得到的节点集合进行tsp运算
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(dayNodes, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<>();
			nList = nTsp.startTsp();// 得到tsp运算后的结果
			// 计算时间当天充电花费
			esyncChargingTime = dayNodes.size() * this.chargingTime;// 每个节点的充电时间
			double tspDistance = 0;
			for (int i = 0; i < nList.size() - 1; i++) {
				tspDistance += nList.get(i).getDistance(nList.get(i + 1));
			}
			esyncChargingTime += tspDistance / workerSpeed;// 加上tsp路径上花费的时间
			System.out.println("（Esync）第" + day + "天的时间花费为：" + esyncChargingTime);
			averageTime += esyncChargingTime;
		}
		averageTime = averageTime / maxPeriod;
		System.out.println("esync平均时间为 ： " + averageTime);
	}

	/**
	 * 辅助函数：得到特定day天的充电节点的集合
	 * 
	 * @return
	 */
	public Set<Node> getTheDayNodes(int day) {
		Map<Integer, KCluster> map = new HashMap<Integer, KCluster>();
		Set<Node> dayNodes = new HashSet<Node>();
		for (KCluster kCluster : originalCluster) {// 建立一个大类与大类的充电周期的键值对应
			map.put(kCluster.getRandomNode().getChargingPeriod(), kCluster);
		}
		for (int i = 0; i < originalCluster.size(); i++) {
			if ((int) Math.pow(2, i) > day) {
				break;
			}
			if (day % (int) Math.pow(2, i) == 0) { // 判断是否能整除决定是否加入充电节点集合
				dayNodes.addAll(map.get((int) Math.pow(2, i)).getNodeSet());
			}
		}
		return dayNodes;
	}

	/**
	 * 计算算法XXX的tsp时间
	 * 
	 * @param nodeList
	 * @return
	 */
	public double getTspChargingTime(ArrayList<Node> nodeList) {
		double tspDistance = 0;
		double tspTime = 0;
		for (int i = 0; i < nodeList.size() - 1; i++) {// 得到一个tsp路径的工人的路程
			tspDistance += nodeList.get(i).getDistance(nodeList.get(i + 1));
		}
		// temp表示一个tsp路径中，第一个节点与最后一个节点的距离
		double lastEdge = nodeList.get(0).getDistance(nodeList.get(nodeList.size() - 1));
		tspDistance = tspDistance + lastEdge;
		if ((tspDistance / workerSpeed) >= chargingTime) {// 若一个Tsp的时间大于充电时间，则不需要等待充电完成，直接回收充电器
			tspTime = 2 * (tspDistance / workerSpeed);
		} else {// 反之，需要等待充电完成。
			tspTime = chargingTime + (tspDistance / workerSpeed);
		}
		return tspTime;
	}

	/**
	 * 只有一个充电器的情况，用于比较esync算法
	 * 
	 * @throws FileNotFoundException
	 */
	public double runAlgXxxWithOneCharger() throws FileNotFoundException {
		initialOriginalCluster(); // 将节点按充电速率分为若干个大类
		initClusterMap(); // 将大类分成小类，这里要注意对于不同的大类其分成小类的个数也不一样
		ArrayList<Set<Node>> nodeSet = linkSubclass();// 调度算法，得到每天的充电节点的集合
		int day = 0;
		double averageTime = 0;
		double maxTime = 0;
		double distance = 0;
		int maxTimeDay = 0;
		Node start = nodePool.getNodeWithID(0);
		for (Set<Node> set : nodeSet) {
			Set<Node> removeElements = new HashSet<>();
			for (Node node : set) {
				if (node.getNodeID() >= 1000)
					removeElements.add(node);
			}
			set.removeAll(removeElements);
			if(set.size() == 0)
				continue;
			set.add(start);
//			for(Node node : set)
//				System.out.print(node.getNodeID() + " ");
//			System.out.println();
			double totalTime = 0;
			// 不进行聚类，直接所有点tsp运算，使用一个充电器充电
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(set, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<>();
			nList = nTsp.startTsp();// 得到tsp运算后的结果
			// 计算day天的充电时间
			totalTime += (set.size() - 1) * chargingTime;//除去基站的充电时间
			double tspDistance = 0;
			for (int i = 0; i < nList.size() - 1; i++) {
				tspDistance += nList.get(i).getDistance(nList.get(i + 1));
			}
			tspDistance += nList.get(0).getDistance(nList.get(nList.size() - 1));
			distance += tspDistance;
			totalTime += tspDistance / workerSpeed;
			averageTime += totalTime;
//			System.out.println("第" + day + "天 one charger :" + totalTime);
			if (totalTime > maxTime) {
				maxTime = totalTime;
				maxTimeDay = day;
			}
			day++;
		}
		System.out.println("天数:" + day);
		System.out.println("总共距离 : " + distance);
		System.out.println("总共时间 : " + averageTime);
		System.out.println("最大时间花费在第" + maxTimeDay + "天 为：" + maxTime);
		System.out.println("averagetime :" + averageTime / nodeSet.size());
		
		return averageTime / nodeSet.size();
	}
	
	public double runMyAlgrWithUAV() throws FileNotFoundException {
		initialOriginalCluster(); // 将节点按充电速率分为若干个大类
		initClusterMap(); // 将大类分成小类，这里要注意对于不同的大类其分成小类的个数也不一样
		ArrayList<Set<Node>> nodeSet = linkSubclass();// 调度算法，得到每天的充电节点的集合
		double distance = 0;
		double totalTime = 0;
		int day = 0;
		Node start = nodePool.getNodeWithID(0);
		for (Set<Node> set : nodeSet) {
			Set<Node> removeElements = new HashSet<>();
			for (Node node : set) {
				if (node.getNodeID() >= 1000)
					removeElements.add(node);
			}
			set.removeAll(removeElements);		
			Sweep mySweep = new Sweep(start);
			ArrayList<RouteWithoutDepot> routeWithoutDepots = mySweep.initialize(set);//得到无人机飞行路径
			for (int i = 0; i < routeWithoutDepots.size(); i++) {
				routeWithoutDepots.get(i).optimize();//对子路径进行优化
				distance += routeWithoutDepots.get(i).getDistance();
//				ArrayList<Node> route = routeWithoutDepots.get(i).getRoute();
//				System.out.print("[");
//				for(int j = 0; j < route.size(); j++) {
//					System.out.print(route.get(j).getNodeID() + " ");
//				}
//				System.out.print("]  ");
			}
//			System.out.println();
//			System.out.print("距离 : " + distance);
			doSchedule(routeWithoutDepots);
			totalTime += getAllCostWithChargingtime();				
//			System.out.print("\t时间 : " + totalTime + " \n");
			day++;
		}
		System.out.println("天数:" + day);
		System.out.println("总共距离 : " + distance);
		System.out.println("总共时间 : " + totalTime);
		System.out.println("平均时间 : " + totalTime / nodeSet.size());
		
		
		return totalTime / nodeSet.size();
					
	}
	
	/**
	 * 进行无人机的调度规划
	 * @param routes
	 */
	private void doSchedule(ArrayList<RouteWithoutDepot> routes){
		//Bin packing problem 集装箱问题，贪心算法求解即可
		hashMap = new HashMap<>();
		integerSetHashMap = new HashMap<>();
        if (routes.size() <= Field.uavNumber) {
        	//无人机的数量大于等于routes的数量，直接分配即可
            for (int i = 0; i < routes.size(); i++) {
                hashMap.put(routes.get(i), i);
            }
        } else {
        	//无人机数量小于routes的数量，意味着有的无人机需要跑2个或者以上的routes
            //将routes里面的UAVRoute排序，这里我们需要降序,默认是升序排序，需要重新给定比较器
            Collections.sort(routes, new Comparator<RouteWithoutDepot>() {
                public int compare(RouteWithoutDepot o1, RouteWithoutDepot o2) {
                    if (o1.getDistance() > o2.getDistance()) {
                        return -1;
                    }
                    if (o1.getDistance() < o2.getDistance()) {
                        return 1;
                    }
                    return 0;
                }
            });

            // 表示平均每个无人机需要访问的UAVRoute的个数
            //int times = (int) routes.size() / uavNumber + 1;
            //选出路径长度最长的uavNumber条路径并派遣无人机对这些路径并发访问，最先回到基地的无人机更换电池之后继续分配UAVRoute访问

            //以下为简易实现算法
            int n = 0;//无人机id
            int tag = 0;//标记
            for (int i = 0; i < routes.size(); i++) {
                if (tag % 2 == 0) {
                    hashMap.put(routes.get(i), n);
                    n++;
                    if (n == Field.uavNumber) {
                        tag++;
                    }
                } else {
                    n--;
                    hashMap.put(routes.get(i), n);
                    if (n == 0) {
                        tag++;
                    }
                }
            }
        }
        
        //把hashmap中保存的结果转存入integerSetHashMap
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();//UAVRoute
            Object value = entry.getValue();//Integer
            if (!integerSetHashMap.containsKey((Integer) value)) {
                Set<RouteWithoutDepot> set = new HashSet<RouteWithoutDepot>();
                set.add((RouteWithoutDepot) key);
                integerSetHashMap.put((Integer) value, set);
            } else {
                integerSetHashMap.get((Integer) value).add((RouteWithoutDepot) key);
            }
        }
    }
	
	

   /**
    * 无人机并行完成这些任务，计算从所有无人机起飞到完成访问的最长的时间即可,无人机更换的电池的时间忽略
    * 无人机到达节点后对节点的服务时间忽略不计
    * @return
    */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public double getAllCost(){
        double maxCost = 0;
        Set<Map.Entry<Integer, Set<RouteWithoutDepot>>> set = integerSetHashMap.entrySet();
        for (Map.Entry entry : set) {
            Set<RouteWithoutDepot> set1 = (Set<RouteWithoutDepot>) entry.getValue();
            double cost = 0;
            for (RouteWithoutDepot e : set1) {
                cost = cost + e.getDistance() / uavSpeed;
            }
            if(maxCost < cost)
            	maxCost = cost;
        }
        return maxCost;
    }
    
    /**
     * 无人机并行完成这些任务，计算从所有无人机起飞到完成访问的最长的时间即可,无人机更换的电池的时间忽略
     * 无人机到达节点后对节点的服务时间与其他的方法的服务时间相同
     * @return
     */
     @SuppressWarnings({ "unchecked", "rawtypes" })
 	public double getAllCostWithChargingtime(){
         double maxCost = 0;
         Set<Map.Entry<Integer, Set<RouteWithoutDepot>>> set = integerSetHashMap.entrySet();
         for (Map.Entry entry : set) {
             Set<RouteWithoutDepot> set1 = (Set<RouteWithoutDepot>) entry.getValue();
             double cost = 0;
             for (RouteWithoutDepot e : set1) {
                 cost = cost + e.getDistance() / uavSpeed + chargingTime * (e.getRoute().size() - 1);
             }
             if(maxCost < cost)
             	maxCost = cost;
         }
         return maxCost;
     }

}
