package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nest.mdc.network.Node;

/**
 * 2分K均值聚类
 * @author Lujunqiu
 * @version 1.0
 */
public class Bisecting_k_means extends Classifier {
//	private ArrayList<KCluster> clusterList ;//簇集合
//	private ArrayList<Node> dataSet;//数据节点集链表
	private int iter = 10 ; //2分K的预设循环次数
	
	/**
	 * Constructor
	 * @param dataset
	 */
	public Bisecting_k_means(ArrayList<Node> dataset) {
		super(dataset);
	}
	
	/**
	 * 2分K均值聚类过程  
	 * @return ArrayList<KCluster>
	 */
	public void classify() {
		KCluster kCluster = new KCluster();
		for (Node node : dataSet) {
			kCluster.addNode(node);
		}
		clusterList.add(kCluster);//把初始的所有数据作为一个cluster加入cluster list
//		System.out.println(clusterList);
		while (true) {
			KCluster temp = end_condition();
			if (temp == null) {
				//System.out.println(clusterList);
				return ;
			}
			ArrayList<Double> SSE = new ArrayList<>();
			Map<Double, ArrayList<KCluster>> map = new HashMap<>();
			for (int i = 0; i < iter; i++) {
				Basic_Kmeans basic_Kmeans = new Basic_Kmeans(); //默认K=2的基本K聚类
				basic_Kmeans.basic_k_means(temp.getNodeSet());
				ArrayList<KCluster> a = basic_Kmeans.getCluster();
				double sse = 0;
				for (KCluster kCluster2 : a) {
					double i1 = kCluster2.SSE();
					sse = sse + i1;
				}
				SSE.add(sse);
				map.put(sse, a);
			}
			 Double[] aa = SSE.toArray(new Double[iter]);
			 Arrays.sort(aa);
			 clusterList.remove(temp);
			 clusterList.addAll(map.get(aa[0]));
		}
	}
	
	/**
	 * 终止条件，cluster list里面每个cluster的规模不超过MAX_NUM
	 * @return KCluster
	 */
	public KCluster end_condition() { 
		for (KCluster e : clusterList) {
			if (e.getKClusterSize() > KCluster.MAX_NUM) {
				return e;//若有超过M个节点的簇则返回该簇的引用
			}
		}
		return null;//所有簇的节点数都少于MAX_NUM则返回null
	}
}
