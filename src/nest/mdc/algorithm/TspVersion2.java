package nest.mdc.algorithm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import nest.mdc.network.CollectionNode;
import nest.mdc.network.Node;

public class TspVersion2{
	private Set<CollectionNode> collectionNodeSet = null;
	private Set<Node> nodeSet = null;
	private Set<TspPoint> allSet = new HashSet<TspPoint>();
	

	//对Node和collectionNode进行TSP
	public TspVersion2(Set nodeSet,Set collectionNodeSet){
		this.nodeSet = nodeSet;
		this.collectionNodeSet = collectionNodeSet;
	}
	
	public TspVersion2(List<Node> nodeList,Set collectionNodeSet){
		nodeSet = new HashSet<>();
//		nodeSet.addAll(nodeList);
		for(Node node : nodeList)
			this.nodeSet.add(node);
		this.collectionNodeSet = collectionNodeSet;
	}
	
	/*
	 * CollectionNode和Node整合一起放在TspPoint中,TspPoint存放在allSet中，并Return allSet;
	 */
	public Set<TspPoint> convertToTspNode(){
		Iterator<Node> nIterator = nodeSet.iterator();
		Iterator<CollectionNode> cIterator = collectionNodeSet.iterator();
		int i = 0;
		while(nIterator.hasNext()){
			Node node = (Node)(nIterator.next());
			TspPoint tspPoint = new TspPoint(node.getXCoordinate(),node.getYCoordinate(),node);
			tspPoint.setID(i);
			allSet.add(tspPoint);
			i++;
		}
		
		while(cIterator.hasNext()){
			CollectionNode collectionNode = (CollectionNode)(cIterator.next());
			TspPoint tspPoint = new TspPoint(collectionNode.getXCoordinate(),collectionNode.getYCoordinate(),collectionNode);
			tspPoint.setID(i);
			allSet.add(tspPoint);
			i++;
		}
		return allSet;
	}
}
