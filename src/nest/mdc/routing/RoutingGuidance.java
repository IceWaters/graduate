package nest.mdc.routing;

import nest.mdc.network.Node;


public class RoutingGuidance {
	private Node nextNode;
	private int iHopCount;
	RoutingGuidance(Node nextNode, int iHopCount){
		this.nextNode = nextNode;
		this.iHopCount = iHopCount;
	}   
	
	public RoutingGuidance(Node nextNode){
		this.nextNode = nextNode;
	}
	
	
	public Node getNext() {
		return nextNode;
	}
	
	
	public int getHopCount() {
		return iHopCount;
	}
}

