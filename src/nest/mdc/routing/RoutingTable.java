//package nest.mdc.routing;
//
//import java.util.HashMap;
//import nest.mdc.network.Node;
//
///*路由表类，包含到某个目的节点怎么走的信息*/
//public class RoutingTable{
//	public HashMap<Node , RoutingGuidance> routingTable = new HashMap<Node,RoutingGuidance>();
//	public Node getNext(Node desNode){
//		return ((RoutingGuidance)routingTable.get(desNode)).nextNode;
//	}
//}


/**
 * 
 */
package nest.mdc.routing;

import java.util.HashMap;
import java.util.Map;
import nest.mdc.network.Node;

/**
 * @author xiaoq
 *
 */
public class RoutingTable {
	Map<Node, RoutingGuidance> table = new HashMap<Node, RoutingGuidance>();  
	/**
	 * 
	 */
	public RoutingTable() {
		// TODO Auto-generated constructor stub
		table = new HashMap<Node, RoutingGuidance>();
	}
	
	/**
	 * 添加路由
	 * @param desNode - 目的节点
	 * @param nextNode - 下一跳节点
	 * @param hopCount - 跳数
	 */
	public void add(Node desNode, Node nextNode, int hopCount) {
		table.put(desNode, new RoutingGuidance(nextNode, hopCount));
	}
	
//	/**
//	 * 添加路由
//	 * @param desNode - 目的节点
//	 * @param nextNode - 下一跳节点
//	 */
//	public void add(Node desNode, Node nextNode) {
//		table.put(desNode, new RoutingGuidance(nextNode));
//	}
	
	/**
	 * 获取下一跳路由
	 * @return nextNode - 下一跳节点
	 */
	public Node getNext(Node desNode){
		return table.get(desNode).getNext();
	}
	
	/**
	 * 获取路由跳数
	 * @return iHopCount - 跳数
	 */
	public int getRestHopCount(Node desNode) {
		return table.get(desNode).getHopCount();
	}
	
}

///*指示离目的节点跳数以及路由转发的下一跳节点*/
//class RoutingGuidance {
//	private Node nextNode;
//	private int iHopCount;
//	RoutingGuidance(Node nextNode, int iHopCount){
//		this.nextNode = nextNode;
//		this.iHopCount = iHopCount;
//	}
//	RoutingGuidance(Node nextNode){
//		this.nextNode = nextNode;
//	}
//	
//	/**
//	 * 获取下一跳路由  
//	 * @return nextNode - 下一跳节点
//	 */
//	public Node getNext() {
//		return nextNode;
//	}
//	
//	/**
//	 * 获取路由跳数
//	 * @return iHopCount - 跳数
//	 */
//	public int getHopCount() {
//		return iHopCount;
//	}
//}
	

