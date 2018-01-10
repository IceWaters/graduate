package nest.mdc.routing;

import java.util.FormatFlagsConversionMismatchException;
import java.util.HashSet;
import java.util.Set;

import nest.mdc.network.Network;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

public class MyRouting {
	private Set<Node> S = new HashSet<Node>();//�Ѽ��뵽�������ĵ�
	private Set<Node> W = new HashSet<Node>();//�ٽ���
	private Set<Node> T = new HashSet<Node>();//ʣ�µĵ�
	private NodePool nodePool;
	
	public MyRouting(NodePool nodePool) {
		this.nodePool = nodePool;
		T = nodePool.getNodeSet();
		run();
	}
	
	private void run() {
		//�����վ��S,����W��T
		Node baseNode = nodePool.getNodeWithID(0);
		baseNode.setParent(null);
		S.add(baseNode);
	//	W.addAll(baseNode.neighbors.get(1).getNeig());
	//	T.removeAll(S);
	//	T.removeAll(W);
		
		
		while(S.size() != nodePool.getNodeNum()) {
			Path path = new Path(null, null, -1.0, 10000);
			for(Node node : S) {
				//����S�еĵ㣬���������ӽڵ���Ȩֵ
				double weight = 0;
				Node tempNode = node;
				//��������·����Ȩֵ
				while(tempNode != null) {
					if(tempNode.getNodeID() != 0) {
						//��վ����Ҫ���
						double tempWeight = tempNode.getWeight() + 1.0;
						weight += tempWeight;
						
						
						int expo = (int) ((int)Math.log(tempWeight)/Math.log(2));
						weight += Math.abs(tempWeight * (tempWeight - Math.pow(2, expo)) / Math.pow(2, expo));
	//					if(Math.abs(Math.pow(2, Math.log(tempWeight)/Math.log(2)) - tempWeight) < 0.000000001)
	//						//�ټ�һ���ӽڵ�ʱ�����
	//						weight =+ tempWeight;
					}									
					tempNode = tempNode.getParent();
				}
				//�����С·����ȨֵΪ�㣬���߲����ڵ�ǰ·����Ȩֵ
				if(path.getWeight() == -1.0 || path.getWeight() >= weight) {
					//��S���ھӵ�����ѡһ����Ϊ�ӽڵ����
					boolean flag;
					if(path.getWeight() > weight)
						flag = true;
					else
						flag = false;
					for(Node neighbor : node.neighbors.get(1).getNeig()) {
						if(!S.contains(neighbor)) {
							//�Ӳ�������S�е������ھӵ��У���ѡ����������ľ����
							double tempDistance = Network.distanceMap2[node.getNodeID()][neighbor.getNodeID()];
							//System.out.println("node : " + node.getNodeID() + " neighbor2 : " + neighbor.getNodeID() + " weight : " + weight + " distance : " + tempDistance);
							if(tempDistance < path.getDistance() || flag) {
								flag = false;
								path = new Path(node, neighbor, weight, tempDistance);		
							}
						}										
					}
						
				}
			}
			//System.out.println("6666");
			//��Ȩֵ��С��·�����뵽���е���������
			if(path.getParent() != null && path.getChild() != null) {
				Node parent = path.getParent();
				parent.addChild(path.getChild());
				path.getChild().setParent(parent);
				S.add(path.getChild());
				//System.out.println("parent : " + parent.getNodeID() + " node : " + path.getChild().getNodeID() + " weight : " + path.getWeight() + " distance : " + path.getDistance() + "\n");
				while(parent != null) {
					parent.addWeightByOne();
					parent = parent.getParent();
				}
			
			}
		}
	}

}

class Path{
	private Node parent;
	private Node child;
	private double weight;
	private double distance;
	
	public Path(Node parent, Node child, double weight, double distance) {
		this.parent = parent;
		this.child = child;
		this.weight = weight;
		this.distance = distance;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public void setChild(Node child) {
		this.child = child;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public void seDistance(double distance) {
		this.distance = distance;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Node getChild() {
		return child;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public double getDistance() {
		return distance;
	}
}