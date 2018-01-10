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
 * 向网络中布置冗余节点，并改变节点的充电周期
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
	 * 获取增加冗余节点后的聚类
	 * 
	 * @return clusters 增加冗余节点后的聚类
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
	 * 向网络中布置冗余节点，从充电周期最短的开始布置冗余周期
	 * 
	 * 只有当添加了冗余节点的节点的实际充电周期到达了下一个阶段的充电周期，才会改变其充电周期，否则，其充电周期值不变
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
	 * 对每个节点根据其到聚类中其他所有点的距离排序
	 * 
	 * @return priorityQueue - 根据距离排序后的节点优先队列
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
	 * 获取布置了冗余节点的节点集合
	 * 
	 * @return redundantNodePos - 布置了冗余节点的节点集合
	 */
	public Set<Node> getRedundantNodePos() {
		return redundantNodePos;
	}

	/**
	 * 打印布置了冗余节点的节点Id，以及该节点布置的冗余节点数目
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