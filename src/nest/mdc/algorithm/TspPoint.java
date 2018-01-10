package nest.mdc.algorithm;

import nest.mdc.network.*;
class TspPoint extends Point{
	final static int NodeType = 1;
	final static int CollectionNodeType = 2;
	public final int typeofPoint ;
	private int id ;
	Node node = null;
	CollectionNode cnode = null;
	
	TspPoint(double x, double y, Node node){
		super(x,y);
		this.node = node;
		typeofPoint = NodeType;
	}
	
	TspPoint(double x, double y, CollectionNode cnode){
		super(x,y);
		this.cnode = cnode;
		typeofPoint = CollectionNodeType;
	}
	
	Node getNode(){
		return node;
	}
	CollectionNode getCNode(){
		return cnode;
	}
	
	void setID(int id){
		this.id = id;
	}
	int getID(){
		return id;
	}
}
