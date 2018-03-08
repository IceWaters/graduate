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
	private Set<Node> S = new HashSet<Node>();//已加入到生成树的点
//	private Set<Node> W = new HashSet<Node>();//临近点
//	private Set<Node> T = new HashSet<Node>();//剩下的点
	private NodePool nodePool;
	private Map<Integer, Set<Integer>> parentChildren = new HashMap<>();//简单存储节点的树结构信息
	
	public MyRouting(NodePool nodePool) {
		this.nodePool = nodePool;
//		T = nodePool.getNodeSet();
//		run();
	}
	
	/**
	 * 路由构造主体算法运行部分
	 */
	public  void run() {
		//加入基站到S,更新W和T
		Node baseNode = nodePool.getNodeWithID(0);
		baseNode.setParent(null);
		S.add(baseNode);
	//	W.addAll(baseNode.neighbors.get(1).getNeig());
	//	T.removeAll(S);
	//	T.removeAll(W);
		
		
		while(S.size() != nodePool.getNodeNum()) {
			Path path = new Path(null, null, -1.0, 10000);
			for(Node node : S) {
				//遍历S中的点，计算添加子节点后的权值
				double weight = 0;
				Node tempNode = node;
				//计算整条路径的权值
				while(tempNode != null) {
					if(tempNode.getNodeID() != 0) {
						//基站不需要充电
						double tempWeight = tempNode.getWeight() + 1.0;
						int expo = (int) (Math.log(tempWeight) / Math.log(2));						
						if(Math.abs(Math.pow(2, expo) - tempWeight) < 0.0001) {
							//再加一个子节点时，通信量正好是2的指数倍
							weight += Math.pow(Field.iNodeSum * expo, 2);
						}else if(Math.abs(Math.pow(2, expo) - tempWeight) < 1.0001) {
							weight += Math.pow(Field.iNodeSum * expo, 3);
						}else
							weight += Math.pow(Math.abs(Field.iNodeSum * (tempWeight - Math.pow(2, expo)) / Math.pow(2, expo))* expo, 2);
					}									
					tempNode = tempNode.getParent();
				}
				//如果最小路径的权值为零，或者不大于当前路径的权值
				if(path.getWeight() == -1.0 || path.getWeight() >= weight) {
					//从S的邻居点中任选一点作为子节点加入
					boolean flag;
					if(path.getWeight() > weight)
						flag = true;
					else
						flag = false;
					for(Node neighbor : node.neighbors.get(1).getNeig()) {  
						if(!S.contains(neighbor)) {
//							//从不存在于S中的所有邻居点中，挑选出距离最近的距离点
							
							//从不存在于S中的所有邻居点中，随意挑一个点作为子节点
							double tempDistance = Network.distanceMap2[node.getNodeID()][neighbor.getNodeID()];
							path = new Path(node, neighbor, weight, tempDistance);
							break;
						}										
					}
						
				}
			}
			//将权值最小的路径加入到现有的生成树中
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
	 * 利用节点的id简单存储子节点的信息
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
	 * 根据节点的id回复子节点的信息
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
