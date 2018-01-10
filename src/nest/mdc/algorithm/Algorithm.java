package nest.mdc.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.cluster.Classifier;
import nest.mdc.cluster.KCluster;
import nest.mdc.cluster.Kmeans;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

/*
 * 其他类的辅助方法
 * author:
 * version:1.0
 */
public class Algorithm {
	static Set findLandmarkWithRK(NodePool nodePool, final int R, final int K) {
		Set landmarkSet = new HashSet();
		// edit
		Set<Node> iset = nodePool.getNodeSet();
		int i = 0;

		for (Node e : iset) {

			Set<Node> Rhop_Neig = new HashSet();
			for (i = 1; i <= R; i++) {
				Set<Node> temp1 = e.neighbors.get(Integer.valueOf(i)).getNeig();
				Rhop_Neig.addAll(temp1);
			} // 得到R跳内的所有node集合

			Iterator<Node> it = Rhop_Neig.iterator(); // 遍历
			while (it.hasNext()) {
				Node e1 = (Node) it.next();

				int a = e1.calculateMultNeigNum(K); // 计算K跳内邻居节点总数
				int b = e.calculateMultNeigNum(K);
				if (a > b)
					break;
			}
			if (!it.hasNext()) { // 检查如何跳出上面的迭代器循环体，是由break跳出或者循环条件跳出。
				landmarkSet.add(e);
				for (Node e2 : Rhop_Neig) {
					iset.remove(e2);
				}
			}

		}

		return landmarkSet;
	}

	static double distance(Point point1, Point point2) {
		double dx = point1.getXCoordinate();
		double dy = point2.getYCoordinate();
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static ArrayList<KCluster> k_Means_Cluster(NodePool nodePool) {
		ArrayList<KCluster> clusterSet = new ArrayList<KCluster>(); // 一系列的类集合
		Set<Node> NodeSet = nodePool.getNodeSet();// 网络中的节点集合
		// int k = 30 ;
		// // int k = NodeSet.size() / KCluster.MAX_NUM ;
		// Kmeans kmeans = new Kmeans(k);
		// clusterSet = kmeans.k_means(nodePool);
		Classifier aaa = new Kmeans(30, nodePool);
		aaa.classify();
		clusterSet = aaa.getClusters();
		return clusterSet;
	}
}
