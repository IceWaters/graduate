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
 * ������ĸ�������
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
			} // �õ�R���ڵ�����node����

			Iterator<Node> it = Rhop_Neig.iterator(); // ����
			while (it.hasNext()) {
				Node e1 = (Node) it.next();

				int a = e1.calculateMultNeigNum(K); // ����K�����ھӽڵ�����
				int b = e.calculateMultNeigNum(K);
				if (a > b)
					break;
			}
			if (!it.hasNext()) { // ��������������ĵ�����ѭ���壬����break��������ѭ������������
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
		ArrayList<KCluster> clusterSet = new ArrayList<KCluster>(); // һϵ�е��༯��
		Set<Node> NodeSet = nodePool.getNodeSet();// �����еĽڵ㼯��
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
