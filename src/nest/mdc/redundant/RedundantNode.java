package nest.mdc.redundant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import nest.mdc.cluster.KCluster;
import nest.mdc.network.Node;

/**
 * �������в�������ڵ㣬���ı�ڵ�ĳ������
 * 
 * @author tfl
 * @version 1.0
 */
public class RedundantNode {
	private int redundantNodeNum;
	private int energyParameter = 2;
	private ArrayList<KCluster> clusters = new ArrayList<>();
	private Set<Node> redundantNodePos = new HashSet<Node>();

	public RedundantNode(int redundantNodeNum, ArrayList<KCluster> clusters) {
		// TODO Auto-generated constructor stub
		this.clusters = clusters;
		this.redundantNodeNum = redundantNodeNum;
		placeRedundantNode();
	}

	/**
	 * ��ȡ��������ڵ��ľ���
	 * 
	 * @return clusters ��������ڵ��ľ���
	 */
	public ArrayList<KCluster> getClusters() {
		// System.out.println("*******************");
		// for(KCluster cluster : clusters){
		// for(Node node : cluster.getNodeSet()){
		// System.out.println(node.getNodeID() + " : " +
		// node.getChargingPeriod());
		// }
		// System.out.println("\n");
		// }
		return clusters;
	}

	/**
	 * �������в�������ڵ㣬�ӳ��������̵Ŀ�ʼ������������
	 * 
	 * ֻ�е����������ڵ�Ľڵ��ʵ�ʳ�����ڵ�������һ���׶εĳ�����ڣ��Ż�ı��������ڣ�������������ֵ����
	 */
	private void placeRedundantNode() {
		while (redundantNodeNum >= 1) {
			KCluster cluster = clusters.get(0);
			Set<Node> nodesInCluster = cluster.getNodeSet();
			Iterator<Node> iterable = nodesInCluster.iterator();
			int times = 0;
			if (iterable.hasNext())
				times = iterable.next().getRedunantNum() + 1;
			for (; times > 0; times--) {
				if (redundantNodeNum >= nodesInCluster.size()) {
					// if the redundant nodes is enough, every node in this
					// cluster get a redundant node
					// then the node is classifed to the next cluster
					redundantNodePos.addAll(nodesInCluster);
					redundantNodeNum = redundantNodeNum - nodesInCluster.size();
					for (Node node : nodesInCluster) {
						// to increase the redundant node num and change the
						// charging period
						node.increaseRedundantNum();
						if (times == 1) {
							// System.out.println(i++ + " : " +
							// node.getChargingPeriod() * energyParameter);
							node.setChargingPeriod(node.getChargingPeriod() * energyParameter);
							if (clusters.size() == 1) {
								KCluster cluster2 = new KCluster();
								clusters.add(cluster2);
							}
							clusters.get(1).addNode(node);
						}
					}
					if (times == 1)
						clusters.remove(0);
				} else {
					Queue<Node> priorityQueue = getDistanceRank(cluster);
					for (; redundantNodeNum > 0; redundantNodeNum--) {
						Node node = priorityQueue.poll();
						node.increaseRedundantNum();
						redundantNodePos.add(node);
					}
					return;
				}
			}
		}
	}

	/**
	 * ��ÿ���ڵ�����䵽�������������е�ľ�������
	 * 
	 * @return priorityQueue - ���ݾ��������Ľڵ����ȶ���
	 */
	private Queue<Node> getDistanceRank(KCluster cluster) {
		Queue<Node> priorityQueue = new PriorityQueue<Node>(11, new Comparators());
		Set<Node> nodes = cluster.getNodeSet();
		double distance;
		for (Node souNode : nodes) {
			distance = 0;
			for (Node desNode : nodes)
				if (souNode != desNode) {
					distance = distance + souNode.getDistance(desNode);
				}
			souNode.setDistance(distance);
			priorityQueue.add(souNode);
		}
		return priorityQueue;
	}

	/**
	 * ��ȡ����������ڵ�Ľڵ㼯��
	 * 
	 * @return redundantNodePos - ����������ڵ�Ľڵ㼯��
	 */
	public Set<Node> getRedundantNodePos() {
		return redundantNodePos;
	}

	/**
	 * ��ӡ����������ڵ�Ľڵ�Id���Լ��ýڵ㲼�õ�����ڵ���Ŀ
	 */
	public void printResult() {
		for (Node node : redundantNodePos) {
			System.out.println(node.getNodeID() + " : " + node.getRedunantNum());
		}
	}
}

class Comparators implements Comparator {
	public int compare(Object arg0, Object arg1) {
		double val1 = ((Node) arg0).getDistance();
		double val2 = ((Node) arg1).getDistance();
		return val1 < val2 ? -1 : 1;
	}
}