/**
 * �ڳ��ʱ���Ͻ��о���
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nest.mdc.algorithm.NewTsp;
import nest.mdc.algorithm.TspVersion2;
import nest.mdc.cluster.Basic_Kmeans;
import nest.mdc.cluster.Bisecting_k_means;
import nest.mdc.cluster.Classifier;
import nest.mdc.cluster.KCluster;
import nest.mdc.network.CollectionNode;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

public class timeclassifier {
	private NodePool nodePool;
	private ArrayList<Node> dataSet;
	private ArrayList<KCluster> originalCluster; // ��Ŵ���
	private Map<KCluster, ArrayList<KCluster>> clusterMap; // ��Ŵ���������ֵ�С��
	// private ArrayList<Set<Node>> scheduledDays = new
	// ArrayList<>();//���ÿ��ĳ��ڵ�
	private int energyParameter;// esync�㷨�еĲ���
	final private int workerSpeed = 1;// ���˵��н��ٶȣ���λ����ÿ��
	final private double chargingTime = 100;// ÿ�����������ʱ�䣬��λ����
	final private int T = 64;

	/**
	 * constructor
	 * 
	 * @param allSet
	 */
	public timeclassifier(ArrayList<Node> allSet, NodePool nodePool) { // ��field��������һ��timeClassifier���󣬴�nodePool��ȥ
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
	 * ֱ�Ӹ�ֵ��ʼ�������
	 * 
	 * @param originalCluster2
	 *            ����紫���ĳ�ʼ�������
	 */
	public void setOriginalClusters(ArrayList<KCluster> originalCluster2) {
		this.originalCluster = originalCluster2;

		// ȷ��ÿ�εĵ�һ���������������һ��
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

		// ����տ�ʼ�ļ������࣬������õ�
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
	 * �㷨XXX
	 * 
	 * @throws FileNotFoundException
	 */
	public double runAlgXXX() throws FileNotFoundException {
		initialOriginalCluster(); //���ڵ㰴������ʷ�Ϊ���ɸ�����
		initClusterMap(); // ������ֳ�С�࣬����Ҫע����ڲ�ͬ�Ĵ�����ֳ�С��ĸ���Ҳ��һ��
		ArrayList<Set<Node>> nodeSet = linkSubclass();// �����㷨���õ�ÿ��ĳ��ڵ�ļ���
		int day = 1;
		double averageTime = 0;
		double maxTime = 0;
		int maxTimeDay = 0;
		for (Set<Node> set : nodeSet) {// ��ÿ��ĳ��ڵ���о��࣬TSP�㷨������ʱ�䣬�����ֵ
			// if(set.size() == 0){
			// System.out.println( "��" + day +"��ĳ��ʱ���ܻ���Ϊ �� " + 0);
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
			classifier.classify();// 2��k��ֵ����
			ArrayList<KCluster> clusterSet = new ArrayList<KCluster>();
			clusterSet = classifier.getClusters();// 2��K��ֵ����Ľ��
			double totalTime = 0;// ÿ����ɳ��ڵ���������ʱ�仨��
			// ����֮����м򻯵�tspn���㣬�õ�������֮���·��˳��
			Set<Node> tspnNodeSet = new HashSet<Node>();
			for (KCluster kCluster : clusterSet) {// �õ�ÿ�����о����վ����Ľڵ�
				tspnNodeSet.add(kCluster.getTspnNode());
			}
			// ����õ�tspn��ʱ�仨�ѣ�tspn·������
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(tspnNodeSet, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<Node>();
			nList = nTsp.startTsp();
			for (int i = 0; i < nList.size() - 1; i++) {
				totalTime += nList.get(i).getDistance(nList.get(i + 1)) / workerSpeed;
			} // ����õ�tspn��ʱ�仨��
			totalTime += nList.get(0).getDistance(nList.get(nList.size() - 1)) / workerSpeed;
			// System.out.println( "��" + day +"���tspnʱ�仨��Ϊ �� " + totalTime);
			// ÿ�������Tsp����,�õ�ÿ������tsp��ʱ��

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
					tspTime = getTspChargingTime(nodeList);// �õ�Tsp��ʱ�仨��
				}
				totalTime += tspTime;// ��������ʱ��
				// for(int i = 0;i < nodeList.size();i++){
				// System.out.println("nodeList��"+ i + "��ĺ�����" +
				// nodeList.get(i).getXCoordinate() +
				// ",������Ϊ "+ nodeList.get(i).getYCoordinate());
				// }
			}
			// System.out.println( "��" + day +"��ĳ��ʱ���ܻ���Ϊ �� " + totalTime);
			if (totalTime > maxTime) {
				maxTime = totalTime;
				maxTimeDay = day;
			}
			day++;
			averageTime += totalTime;
		}
		averageTime = averageTime / T;
		System.out.println("���ʱ�仨���ڵ�" + maxTimeDay + "�� Ϊ��" + maxTime);
		System.out.println("ƽ��ʱ��Ϊ ��" + averageTime);
		return averageTime;
	}

	/**
	 * �����õ�����
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
	 * ������ֳ�С�࣬����Ҫע����ڲ�ͬ�Ĵ�����ݳ��������ֳ�С��ĸ���Ҳ��һ��
	 */
	public void initClusterMap() {
		// test();
		for (KCluster kCluster : originalCluster) {
			int i;
			if (kCluster.getRandomNode() == null) {
				i = 0;
			} else {
				i = kCluster.getRandomNode().getChargingPeriod();// �������
			}
			int n = kCluster.getNodeSet().size();// ÿ����Ľڵ���
			ArrayList<KCluster> subCluster = new ArrayList<>(); // �洢������
			subCluster.add(kCluster);
			clusterMap.put(kCluster, subCluster);
			KCluster temp11 = new KCluster();
			temp11.getNodeSet().addAll(kCluster.getNodeSet());
			// if (n < 1.7*i) {//������������ĵ���
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
				subCluster.remove(map.get(aa[sse.size() - 1]));// �ó�sse�������࣬�������ࣻ
				ArrayList<KCluster> temp = bisectKcluster(temp11);
				subCluster.addAll(temp);
				// clusterMap.put(kCluster, subCluster);
			}
		}
	}

	/**
	 * ����2��K���ཫһ��kcluster��Ϊ2��kcluster
	 */
	private ArrayList<KCluster> bisectKcluster(KCluster tCluster) {
		ArrayList<KCluster> subKcluster = new ArrayList<>();
		ArrayList<Double> SSE = new ArrayList<>();
		Map<Double, ArrayList<KCluster>> map = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			Basic_Kmeans basic_Kmeans = new Basic_Kmeans(); // Ĭ��K=2�Ļ���K����
			basic_Kmeans.basic_k_means(tCluster.getNodeSet());
			ArrayList<KCluster> a = basic_Kmeans.getCluster();
			double sse = 0;
			for (KCluster kCluster2 : a) {
				double i1 = kCluster2.SSE();
				sse = sse + i1;
			}
			SSE.add(sse); // ÿ�ξ������sse
			map.put(sse, a);
		}
		Double[] aa = SSE.toArray(new Double[10]);
		Arrays.sort(aa);
		subKcluster.addAll(map.get(aa[0]));
		return subKcluster;
	}

	/**
	 * ����ÿ��ĳ��ڵ㼯�� �����������������ڵ����Set����һ��ĳ��ڵ�
	 */
	public ArrayList<Set<Node>> linkSubclass() {
		for (KCluster kCluster : originalCluster) {// �������࣬������Ŀ�ﵽ���������
			// int t = kCluster.getRandomNode().getChargingPeriod();//�������
			int t;
			if (kCluster.getRandomNode() == null) {
				t = 0;
			} else {
				t = kCluster.getRandomNode().getChargingPeriod();// �������
			}
			int n = clusterMap.get(kCluster).size();// ʵ�����ֵ�����ĸ���
			if (n < t) {// ��������
				for (int i = 0; i < t - n; i++) {
					KCluster nullCluster = new KCluster();// ����Ĭ��(0,0)
					nullCluster.setNodeSet(null);
					clusterMap.get(kCluster).add(nullCluster);
				}
			}
		}

		int size = originalCluster.size();// ����ĸ���
		KCluster cluster = originalCluster.get(size - 1);
		Node node = null;
		if (cluster != null) {
			node = cluster.getRandomNode();
		}
		double maxPeriod = node.getChargingPeriod();// ���ĳ������
		// ����ֵ
		ArrayList<Set<Node>> scheduledDays = new ArrayList<>();// ���ÿ��ĳ��ڵ�ļ���

		for (int i = 0; i < maxPeriod; i++) {
			Set<Node> scheduleDay = new HashSet<>();
			KCluster tCluster = null;
			for (int index = 0; index < originalCluster.size() - 1; index++) {
				if (clusterMap.get(originalCluster.get(index)).size() == 1) {
					tCluster = clusterMap.get(originalCluster.get(index)).get(0);
					scheduleDay.addAll(tCluster.getNodeSet());
				}
				// ѡ���֮ǰcollection��û��ѡ��������������schedule
				ArrayList<KCluster> chooseClusters = new ArrayList<>();
				for (KCluster kCluster : clusterMap.get(originalCluster.get(index + 1))) {
					if (kCluster.getTag() == 0) {
						chooseClusters.add(kCluster);
					} else {
						kCluster.addTag();
					}
					if (kCluster.getTag() == Math.pow(2, index + 1)) {
						kCluster.setTag(0);
					}
				}
				//
				// if(tCluster != null && chooseClusters.size() != 0)
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
	 * �������� �ҵ���ĳ���������(�����ĵľ���������)
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
	 * ���ڵ㰴������ʷ�Ϊ���ɸ�����
	 * 
	 * @throws FileNotFoundException
	 */
	public void initialOriginalCluster() throws FileNotFoundException {
		File file = new File("./file.txt");// �������ӡ��txt�ı���
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream p = new PrintStream(fos);
		int temp = 8;

		double min = 0; // 
		double max = 0;//���Ĵ���ͨ����
		for (Node node : dataSet) {
			if (node.getNodeID() == 0)
				 // ����ڵ�IdΪ0��������ִ����һ��ѭ��
				 continue;
			double num = node.getWeight();
			if (num > max)
				max = num;			
		}


		int m = (int) (Math.log(max) / Math.log((double) 2)) + 1; // ����������Ŀ
		int[] sort = new int[m];
		for (int i = 0; i < m; i++)
			sort[i] = 0;

		for (int i = 0; i < m; i++) {
			// ��ʼ��originalCluster
			KCluster tempClusters = new KCluster();
			originalCluster.add(tempClusters);
		}

		for (Node node : dataSet) {
			if (node.getNodeID() == 0)
			// ����ڵ�IdΪ0��������ִ����һ��ѭ��
				continue;
			double pcr = node.getWeight(); // �ĵ����� power consumption rate
			p.println(node.getNodeID() + "\t:\t" + pcr); // ��ӡ�ڵ�ID �� �ĵ�����

			if (pcr >= min && pcr <= 1.0) {
				// ���ĵ������ڵ�һ��������
				originalCluster.get(m - 1).addNode(node);
				node.setChargingPeriod((int) Math.pow(energyParameter, m - 1));
				sort[0]++;
			} else {
				// ѭ������ĵ���������
				for (int i = 1; i < m; i++) {
					if (pcr > Math.pow(energyParameter, m - i - 1)
							&& pcr <= Math.pow(energyParameter, m - i)) {
						originalCluster.get(m - i - 1).addNode(node);
						node.setChargingPeriod((int) Math.pow(energyParameter, m - i - 1));
						sort[i]++;
						break;
					}
				}
			}
		}

		

		// ����տ�ʼ�ļ������࣬������õ�
		for (int i = 0; i < m; i++) {
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
		// ����������ӡ��txt�ĵ���
		p.println("\n��������");
		for (int i = 0; i < originalCluster.size(); i++) {
			p.println("(" + (double)Math.pow(energyParameter, m - i -1) + ","
					+ (double)  Math.pow(energyParameter, m - i) + "] : "
					+ originalCluster.get(m - i - 1).getNodeSet().size());
		}
		
		for (int i = 0; i < temp - m; i++) {
			KCluster clusterTemp = new KCluster();
			Node node2 = new Node(0, 0, 2000 + i);
			// System.out.println((int)Math.pow(2,i));
			node2.setChargingPeriod((int) Math.pow(2, i));
			clusterTemp.addNode(node2);
			originalCluster.add(clusterTemp);			
			// count++;
		}
		
		// ����������ӡ��txt�ĵ���
		p.println("\n��������");
		for (int i = 0; i < originalCluster.size(); i++) {
			p.println("(" + (double)Math.pow(energyParameter, m - i -1) + ","
					+ (double)  Math.pow(energyParameter, m - i) + "] : "
					+ originalCluster.get(m - i - 1).getNodeSet().size());
		}
		p.close();
		// for (KCluster cluster : originalCluster) {
		// for (Node node2 : cluster.getNodeSet()) {
		// System.out.print(node2.getChildrenNum() + ":" +
		// node2.getChargingPeriod() + " ");
		// }
		// System.out.println();
		// }
	}

	/**
	 * ��esync�㷨����Ľ������tsp���㣬�õ����㷨������ʱ�仨��
	 * 
	 * @throws FileNotFoundException
	 */
	public void runAlgEsync() throws FileNotFoundException {
		initialOriginalCluster(); // ���ڵ㰴������ʷ�Ϊ���ɸ�����
		int maxPeriod = (int) Math.pow(2, originalCluster.size() - 1);
		// double esyncChargingTime = 0 ; //Esync�㷨��ʱ�仨��
		double averageTime = 0;// �洢ƽ��ʱ��
		for (int day = 1; day <= maxPeriod; day++) {
			double esyncChargingTime = 0; // Esync�㷨��ʱ�仨��
			Set<Node> dayNodes = getTheDayNodes(day);// �õ�day��ĳ��ڵ㼯��
			// �Եõ��Ľڵ㼯�Ͻ���tsp����
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(dayNodes, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<>();
			nList = nTsp.startTsp();// �õ�tsp�����Ľ��
			// ����ʱ�䵱���绨��
			esyncChargingTime = dayNodes.size() * this.chargingTime;// ÿ���ڵ�ĳ��ʱ��
			double tspDistance = 0;
			for (int i = 0; i < nList.size() - 1; i++) {
				tspDistance += nList.get(i).getDistance(nList.get(i + 1));
			}
			esyncChargingTime += tspDistance / workerSpeed;// ����tsp·���ϻ��ѵ�ʱ��
			System.out.println("��Esync����" + day + "���ʱ�仨��Ϊ��" + esyncChargingTime);
			averageTime += esyncChargingTime;
		}
		averageTime = averageTime / maxPeriod;
		System.out.println("esyncƽ��ʱ��Ϊ �� " + averageTime);
	}

	/**
	 * �����������õ��ض�day��ĳ��ڵ�ļ���
	 * 
	 * @return
	 */
	public Set<Node> getTheDayNodes(int day) {
		Map<Integer, KCluster> map = new HashMap<Integer, KCluster>();
		Set<Node> dayNodes = new HashSet<Node>();
		for (KCluster kCluster : originalCluster) {// ����һ�����������ĳ�����ڵļ�ֵ��Ӧ
			map.put(kCluster.getRandomNode().getChargingPeriod(), kCluster);
		}
		for (int i = 0; i < originalCluster.size(); i++) {
			if ((int) Math.pow(2, i) > day) {
				break;
			}
			if (day % (int) Math.pow(2, i) == 0) { // �ж��Ƿ������������Ƿ������ڵ㼯��
				dayNodes.addAll(map.get((int) Math.pow(2, i)).getNodeSet());
			}
		}
		return dayNodes;
	}

	/**
	 * �����㷨XXX��tspʱ��
	 * 
	 * @param nodeList
	 * @return
	 */
	public double getTspChargingTime(ArrayList<Node> nodeList) {
		double tspDistance = 0;
		double tspTime = 0;
		for (int i = 0; i < nodeList.size() - 1; i++) {// �õ�һ��tsp·���Ĺ��˵�·��
			tspDistance += nodeList.get(i).getDistance(nodeList.get(i + 1));
		}
		// temp��ʾһ��tsp·���У���һ���ڵ������һ���ڵ�ľ���
		double lastEdge = nodeList.get(0).getDistance(nodeList.get(nodeList.size() - 1));
		tspDistance = tspDistance + lastEdge;
		if ((tspDistance / workerSpeed) >= chargingTime) {// ��һ��Tsp��ʱ����ڳ��ʱ�䣬����Ҫ�ȴ������ɣ�ֱ�ӻ��ճ����
			tspTime = 2 * (tspDistance / workerSpeed);
		} else {// ��֮����Ҫ�ȴ������ɡ�
			tspTime = chargingTime + (tspDistance / workerSpeed);
		}
		return tspTime;
	}

	/**
	 * ֻ��һ�����������������ڱȽ�esync�㷨
	 * 
	 * @throws FileNotFoundException
	 */
	public void runAlgXxxWithOneCharger() throws FileNotFoundException {
		initialOriginalCluster(); // ���ڵ㰴������ʷ�Ϊ���ɸ�����
		// runAlgEsync();
		initClusterMap(); // ������ֳ�С�࣬����Ҫע����ڲ�ͬ�Ĵ�����ֳ�С��ĸ���Ҳ��һ��
		ArrayList<Set<Node>> nodeSet = linkSubclass();// �����㷨���õ�ÿ��ĳ��ڵ�ļ���
		int day = 1;
		double averageTime = 0;
		double maxTime = 0;
		int maxTimeDay = 0;
		for (Set<Node> set : nodeSet) {
			double totalTime = 0;
			// �����о��ֱ࣬�����е�tsp���㣬ʹ��һ����������
			Set<CollectionNode> cNodes2 = new HashSet<>();
			TspVersion2 tVersion2 = new TspVersion2(set, cNodes2);
			NewTsp nTsp = new NewTsp(tVersion2.convertToTspNode());
			ArrayList<Node> nList = new ArrayList<>();
			nList = nTsp.startTsp();// �õ�tsp�����Ľ��
			// ����day��ĳ��ʱ��
			totalTime += set.size() * chargingTime;
			double tspDistance = 0;
			for (int i = 0; i < nList.size() - 1; i++) {
				tspDistance += nList.get(i).getDistance(nList.get(i + 1));
			}
			totalTime += tspDistance / workerSpeed;
			averageTime += totalTime;
			System.out.println("��" + day + "�� one charger :" + totalTime);
			if (totalTime > maxTime) {
				maxTime = totalTime;
				maxTimeDay = day;
			}
			day++;
		}
		System.out.println("���ʱ�仨���ڵ�" + maxTimeDay + "�� Ϊ��" + maxTime);
		System.out.println("averagetime :" + averageTime / nodeSet.size());
	}

}
