package nest.mdc.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nest.mdc.field.Field;
import nest.mdc.network.Network;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

public class MyRouting {
	private Set<Node> S = new HashSet<Node>();//�Ѽ��뵽�������ĵ�
//	private Set<Node> W = new HashSet<Node>();//�ٽ���
//	private Set<Node> T = new HashSet<Node>();//ʣ�µĵ�
	private NodePool nodePool;
	private Map<Integer, Set<Integer>> parentChildren = new HashMap<>();//�򵥴洢�ڵ�����ṹ��Ϣ
	
	public MyRouting(NodePool nodePool) {
		this.nodePool = nodePool;
//		T = nodePool.getNodeSet();
//		run();
	}
	
	/**
	 * ·�ɹ��������㷨���в���
	 */
	public  void run() {
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
				//����S�еĵ㣬��������ӽڵ���Ȩֵ
				double weight = 0;
				Node tempNode = node;
				//��������·����Ȩֵ
				while(tempNode != null) {
					if(tempNode.getNodeID() != 0) {
						//��վ����Ҫ���
						double tempWeight = tempNode.getWeight() + 1.0;
						int expo = (int) (Math.log(tempWeight) / Math.log(2));						
						if(Math.abs(Math.pow(2, expo) - tempWeight) < 0.0001) {
							//�ټ�һ���ӽڵ�ʱ��ͨ����������2��ָ����
							weight += Math.pow(Field.iNodeSum * expo, 2);
						}else if(Math.abs(Math.pow(2, expo) - tempWeight) < 1.0001) {
							weight += Math.pow(Field.iNodeSum * expo, 3);
						}else
							weight += Math.pow(Math.abs(Field.iNodeSum * (tempWeight - Math.pow(2, expo)) / Math.pow(2, expo))* expo, 2);
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
//							//�Ӳ�������S�е������ھӵ��У���ѡ����������ľ����
							
							//�Ӳ�������S�е������ھӵ��У�������һ������Ϊ�ӽڵ�
							double tempDistance = Network.distanceMap2[node.getNodeID()][neighbor.getNodeID()];
							path = new Path(node, neighbor, weight, tempDistance);
							break;
						}										
					}
						
				}
			}
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
	
	/**
	 * ���ýڵ��id�򵥴洢�ӽڵ����Ϣ
	 */
	public void setParentChildren() {
		parentChildren = new HashMap<>();
		for(Node node : nodePool.getNodeSet()) {
			if(node.getParent() != null) {
				int parentId = node.getParent().getNodeID();
				int childId = node.getNodeID();
				if(parentChildren.containsKey(parentId)) {
					parentChildren.get(parentId).add(childId);
				}else {
					Set<Integer> childSet = new HashSet<>();
					childSet.add(childId);
					parentChildren.put(parentId, childSet);
				}
			}
		}
	}
	
	/**
	 * ���ݽڵ��id�ظ��ӽڵ����Ϣ
	 */
	public void getParentChildren() {
		nodePool.clearChildren();
		for (Map.Entry<Integer, Set<Integer>> entry : parentChildren.entrySet()) { 
			int parentId = entry.getKey();
			Node parent = nodePool.getNodeWithID(parentId);
			Set<Integer> childrenId = entry.getValue();
			for(Integer childId : childrenId) {
				Node child = nodePool.getNodeWithID(childId);
				parent.addChild(child);
				child.setParent(parent);
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
