//package nest.mdc.routing;
//
//import java.util.HashMap;
//import nest.mdc.network.Node;
//
///*·�ɱ��࣬������ĳ��Ŀ�Ľڵ���ô�ߵ���Ϣ*/
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
	 * ���·��
	 * @param desNode - Ŀ�Ľڵ�
	 * @param nextNode - ��һ���ڵ�
	 * @param hopCount - ����
	 */
	public void add(Node desNode, Node nextNode, int hopCount) {
		table.put(desNode, new RoutingGuidance(nextNode, hopCount));
	}
	
//	/**
//	 * ���·��
//	 * @param desNode - Ŀ�Ľڵ�
//	 * @param nextNode - ��һ���ڵ�
//	 */
//	public void add(Node desNode, Node nextNode) {
//		table.put(desNode, new RoutingGuidance(nextNode));
//	}
	
	/**
	 * ��ȡ��һ��·��
	 * @return nextNode - ��һ���ڵ�
	 */
	public Node getNext(Node desNode){
		return table.get(desNode).getNext();
	}
	
	/**
	 * ��ȡ·������
	 * @return iHopCount - ����
	 */
	public int getRestHopCount(Node desNode) {
		return table.get(desNode).getHopCount();
	}
	
}

///*ָʾ��Ŀ�Ľڵ������Լ�·��ת������һ���ڵ�*/
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
//	 * ��ȡ��һ��·��  
//	 * @return nextNode - ��һ���ڵ�
//	 */
//	public Node getNext() {
//		return nextNode;
//	}
//	
//	/**
//	 * ��ȡ·������
//	 * @return iHopCount - ����
//	 */
//	public int getHopCount() {
//		return iHopCount;
//	}
//}
	

