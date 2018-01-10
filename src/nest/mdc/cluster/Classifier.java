package nest.mdc.cluster;

import java.util.ArrayList;

import nest.mdc.network.Node;

/**
 * @author xiaoq
 *
 */
public class Classifier {

	/**
	 * 
	 */
	protected ArrayList<Node> dataSet;
	protected ArrayList<KCluster> clusterList;
	
	/**
	 * Constructor
	 * @param allSet
	 */
	Classifier(ArrayList<Node> allSet){
		dataSet = new ArrayList<Node>();
		clusterList = new ArrayList<KCluster>();
		this.dataSet.addAll(allSet);
	}
	
	/**
	 * @return the clusters
	 */
	public ArrayList<KCluster> getClusters() {
		return clusterList;
	}
	
	/**
	 * 聚类过程,由子类实现
	 */
	public void classify(){
		//override
	}
}	