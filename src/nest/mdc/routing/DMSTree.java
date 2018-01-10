package nest.mdc.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nest.mdc.network.Node;

/**
 * 有向最小树形图主要算法过程
 * @author 涂方蕾
 * @version 1.0
 */
public class DMSTree{

	private Set<TreeNode> treeNodeSet = new HashSet<TreeNode>();	//树节点集合
	private Set<ManualNode> manualNodes = new HashSet<ManualNode>();  //当前迭代层次的人工顶点集合
	private final double INF = 9999;	//最大距离，表示不可直达
	private final int nodeNum;	//节点数
	private double[][] map ;	//map[i][j]中存放节点i到节点j的距离，若不可直达则用INF表示
	private TreeNode root;	
	private double cost = 0;
		
	/**
	 * Constructor
	 * @param nodeSet - 节点集合
	 */
	public DMSTree(Set<Node> nodeSet){
		for(Node node : nodeSet){
			//创建所有树的树节点
			if(node != null){
				TreeNode treenode = new TreeNode(node);
				treeNodeSet.add(treenode);
			}
		}		
		for(TreeNode treeNode : treeNodeSet){
			//将每个基本树节点包装成人工节点
			if(treeNode != null){
				Set<TreeNode> nodeSetTemp = new HashSet<TreeNode>();
				nodeSetTemp.add(treeNode);
				ManualNode manualNode = new ManualNode(nodeSetTemp);
				manualNodes.add(manualNode);
			}
		}
		nodeNum = nodeSet.size();	//获取节点数
		map = new double[nodeNum][nodeNum];
		initialMap();
		root = getRootNode();
					
		while(refreshLoop()){
			//不断的找环缩点
			refreshPaManNode();			
		}
		for(ManualNode manualNode : manualNodes)
			//当最后没有环时，开始展开加边
			if(manualNode.getMainNode() != root && manualNode.getMainNode() != null){
				linkNode(manualNode);
			}
	//	hasLoop(); //测试最后生成的图中是否有环
		System.out.println("The weight is : " + cost);
	}
	
	/**
	 * 返回最小树形图中节点的集合
	 * @return treeNodeSet - 最小树形图中的树节点集合
	 */
	public Set<TreeNode> getTreeNode(){
		return treeNodeSet;
	}
	
	/**
	 * 返回最小树形图的根节点
	 * @return root - 最小树形图的根节点
	 */
	public TreeNode getTree(){
		return root;
	}	
	
	/**
	 * 
	 * @return
	 */
	public int[] getPathToRoot(int[] path) {
		return path;
	}
	
	/**
	 * 从节点数组获取节点，根据节点之间的邻居关系获取map[MAX][MAX],如果节点i与节点j互为邻居，则map[i][j]等于getWeight(Node_i, Node_j)，否则设为INF
	 * 并且计算每一个点的最小入边                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	 * 首先假设所有的节点之间是不可达的，令map[][]里面的值都为INF
	 */
	private void initialMap(){			
		for(int i = 0; i < nodeNum; i++)
			for(int j = 0; j < nodeNum; j++)
				map[i][j] = INF;
		
		//根据节点之间的邻居关系获取map[MAX][MAX],如果节点i与节点j互为邻居，则将map[i][j]的值重置为getWeight(Node_i, Node_j)
		for(ManualNode manualNode : manualNodes){
			for(TreeNode desTreeNode : manualNode.getManualNode()){
					if(desTreeNode == null) continue;
					int desNodeID =  desTreeNode.getNetworkNode().getNodeID();//得到目的节点j的ID			
					double minWeight = INF;
					Node minNode = null;
					Set<Node> neighbors = desTreeNode.getNetworkNode().neighbors.get(1).getNeig();//依次得到节点的邻居节点
					for(Node soulNode : neighbors){
						int soulNodeID = soulNode.getNodeID(); //得到源节点i的ID
						map[soulNodeID][desNodeID] = getWeight(soulNode, desTreeNode.getNetworkNode());//重置map[i][j]为Weight
						if(map[soulNodeID][desNodeID] < minWeight){
							//计算最小入边
							minWeight = map[soulNodeID][desNodeID];
							minNode = soulNode;					
						}					
					}
					
					if(desNodeID != 0){
						cost = cost + minWeight;
						manualNode.setParentManNode(findManualNode(getTreeNode(minNode), manualNodes));//设置最小入边
						manualNode.setMainNode(desTreeNode);//设置主点
						manualNode.setParentNode(getTreeNode(minNode)); //设置父节点
					}
					if(minNode.getNodeID() == 0)
						System.out.println("initial : " + desNodeID);
			}
		}		
	}
	
	/**
	 * 得到有向环，然后进行收缩
	 * 由于每个节点只有一个父母节点，因此反向找环，不会出现路径分叉的现象
	 * @return hasLoop - 如果还有有向环，返回true；否则，false
	 */
	private boolean refreshLoop(){				
	//	System.out.println("refreshLoop");
		boolean hasLoop = false;//记录是否有有向环
		Set<ManualNode> manualNodesTemp = new HashSet<ManualNode>();  //当前迭代层次的人工顶点集合
		Set<ManualNode> confirmedManualNode = new HashSet<ManualNode>(); //存储已经验证过的人工顶点
		confirmedManualNode.add(findManualNode(root, manualNodes));//由于根节点没有入边，故不需要进行验证，可加入已经验证过的节点集合中
		for(ManualNode manualNode : manualNodes){
			if(!confirmedManualNode.contains(manualNode)){
			//如果该人工节点不在之前已验证过的环里，则继续验证；否则换下一人工节点
				Set<ManualNode> loop = new HashSet<ManualNode>(); //存数在同一个环里的树节点
				ManualNode parentNode = manualNode.getParentManNode();
				while((parentNode != null) && (parentNode != manualNode)
						&& (!loop.contains(parentNode)) ){	
					loop.add(parentNode);
					parentNode = parentNode.getParentManNode();
				}
				
				if(loop.contains(parentNode) || parentNode == null){
					//如果树节点沿父母节点搜索，最后指向null，则该树节点不在环中
					loop.clear();
					manualNodesTemp.add(manualNode);
					confirmedManualNode.add(manualNode);
				}
				else if(parentNode == manualNode){
					//有环的话，建立新的人工顶点,并更新权值
					hasLoop = true;
					loop.add(manualNode);
					ManualNode newManualNode = newManualNode(loop);  //建立新的人工顶点
					newManualNode.setInnerNodes(loop);  //设置人工顶点内部顶点
					refreshWeight(newManualNode); //更新权值
					manualNodesTemp.add(newManualNode);  //更新当前迭代次数的人工顶点
					confirmedManualNode.addAll(loop);
				}								
			}
		}
		manualNodes = manualNodesTemp;
		return hasLoop;
	}
	
	/**
	 * 迭代展开缩点连边
	 * @param manualNode - 基本树节点收缩成的人工节点
	 */
	private void linkNode(ManualNode manualNode){
		TreeNode mainNode = manualNode.getMainNode();
		if(mainNode.getParents() == null)
			setMinSide(manualNode.getParentNode(), mainNode);//连接人工顶点的入边
		if(manualNode.getManualNode().size() <= 1) 
			//为基本树节点时，返回
			return ;		
		Set<ManualNode> manualNodes = manualNode.getInnerNodes();//得到人工顶点内部更低层次的人工层次
		ManualNode mainManualNode = findManualNode(mainNode, manualNodes);//人工顶点的主点
		ManualNode manNode = mainManualNode.getParentManNode();//确定该人工顶点内的主点的父人工顶点		
		while(manNode != mainManualNode){
			//遍历内部人工节点
			linkNode(manNode);	
			manNode = manNode.getParentManNode();	
		}
		//改变主点所在的内部人工顶点的主点和父节点
		mainManualNode.setMainNode(mainNode);
		mainManualNode.setParentNode(manualNode.getParentNode());
		linkNode(mainManualNode);
	}
	
	/**
	 * 更新人工顶点的父人工顶点
	 */
	private void refreshPaManNode(){
	//	System.out.println("refreshPaManNode");
		for(ManualNode manualNode : manualNodes){
			TreeNode treeNode = manualNode.getParentNode();
			manualNode.setParentManNode(findManualNode(treeNode, manualNodes));
		}
	}		
	
	/**
	 * 判断生成最小生成树后整个节点集合中是否还有环,测试代码
	 * @return boolean - 如果节点集合中还有换，返回true；否则，为false
	 */
	private boolean hasLoop(){
		for(TreeNode treeNode : treeNodeSet){
			Set<TreeNode> loop = new HashSet<TreeNode>(); //存数在同一个环里的树节点
			TreeNode parentNode = treeNode.getParents();
			while((parentNode != treeNode) && (parentNode != null)	
				&& (!loop.contains(parentNode)) ){	
				loop.add(parentNode);
				parentNode = parentNode.getParents();
				//System.out.println("increase : " + loop.size());
			}
			
			if(parentNode == treeNode){
				System.out.println("hasLoop : " + parentNode.getNetworkNode().getNodeID() + "  " 
									+ treeNode.getParents().getNetworkNode().getNodeID());
				loop.add(treeNode);
				
				return true;
			}		
		} 
	//	System.out.println("hasnoLoop");
		return false;
	}	
	
	/**
	 * 将给定的人工顶点集合形成更高层次的新的人工顶点
	 * @param manualNodes - 人工节点集合
	 * @return manualNode - 集合形成的新的人工顶点
	 */
	private ManualNode newManualNode(Set<ManualNode> manualNodes){
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		for(ManualNode manualNode : manualNodes)
			treeNodes.addAll(manualNode.getManualNode());
		ManualNode manualNode = new ManualNode(treeNodes);
		return manualNode;
	}
		
	/**
	 * 返回给定树节点组成的人工节点,仅为最开始人工顶点与基本树节点都相同时使用
	 * @param treeNode - 基本的树节点
	 * @param manNodes - 人工顶点集合
	 * @return manualNode - 该树节点所在的人工顶点
	 */
	private ManualNode findManualNode(TreeNode treeNode, Set<ManualNode> manNodes){
		if(treeNode == null)  
			return null;
		for(ManualNode manualNode : manNodes){
			for(TreeNode node : manualNode.getManualNode())
				if(treeNode == node )
						return manualNode;
		}
		return null;
	}	
	
	/**
	 * 获取边的权重，权重W = desNode.getRemainingBattery * soulNode.getDistance(desNode)
	 * @param souNode - 起点
	 * @param desNode - 终点
	 * @return weight - 两点之间边的权值
	 */
	private double getWeight(Node souNode, Node desNode){
		double weight = desNode.getRemainingBattery() * souNode.getDistance(desNode);
		return weight;
	}			
	
	/**
	 * 更新到新顶点的权值
	 * @param manualNode - 新形成的人工顶点
	 */
	private void refreshWeight(ManualNode manualNode){
		TreeNode minNode = null;
		TreeNode minPaNode = null;
		double minWeight = INF;
		ArrayList<Integer> nodeList = new ArrayList<Integer>();//存储人工节点中的节点ID集合
		for(TreeNode treeNode : manualNode.getManualNode()){
			nodeList.add(treeNode.getNetworkNode().getNodeID());
			//System.out.println("new Loop : " + treeNode.getNetworkNode().getNodeID());
		}
		for(ManualNode mNode : manualNode.getInnerNodes()){
			//遍历人工顶点内部的更低层次的人工顶点
			int parentNodeID = mNode.getParentNode().getNetworkNode().getNodeID();
			int mainNodeID = mNode.getMainNode().getNetworkNode().getNodeID(); 
			double inWeight = map[parentNodeID][mainNodeID];  //人工顶点的入边权值
			
			for(TreeNode treeNode : mNode.getManualNode()){
				Set<Node> neighbors = treeNode.getNetworkNode().neighbors.get(1).getNeig();//依次得到节点的邻居节点
				for(Node soulNode : neighbors){
				//遍历环中所有点的邻居的点的集合，更新权值
					if(!nodeList.contains(soulNode.getNodeID())){
						map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] = 
							map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] - inWeight;
						if(map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] < minWeight){
							//找出最小入边
							minWeight = map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()];
							minNode = treeNode;
							minPaNode = getTreeNode(soulNode);
						}
					}						
				}		
			}
		}
		cost = cost + minWeight;
		manualNode.setMainNode(minNode);//设置主点
		manualNode.setParentNode(minPaNode);//设置父节点
	}
	
	/**
	 * 测试用，打印人工顶点的内部所有树节点
	 * @param manualNode - 人工顶点
	 */
	private void printManualNode(ManualNode manualNode){
		System.out.print("manualNode : ");
		for(TreeNode treeNode : manualNode.getManualNode()){
			System.out.print(treeNode.getNetworkNode().getNodeID() + ", ");
		}
		System.out.println("");
	}

	/**
	 * 设置最小的入边
	 * @param soulTreeNode - 初始树节点
	 * @param desTreeNode - 目的树节点
	 */
	private void setMinSide(TreeNode soulTreeNode, TreeNode desTreeNode){
		soulTreeNode.addChild(desTreeNode);
		desTreeNode.addParents(soulTreeNode);
		if(soulTreeNode.getNetworkNode().getNodeID() == 0)
			System.out.println("add + " + soulTreeNode.getChildren());
	//	System.out.println("count : " + ++count);
	}
        
	/**
	 * 遍历节点数组，得到坐标和最小的节点，即根节点
	 * @return treeNode - 根节点
	 */
	private TreeNode getRootNode(){
		for(TreeNode treeNode : treeNodeSet)
			if(treeNode.getNetworkNode().getNodeID() == 0){
				return treeNode;
			}
		return null;
	}
	
	/**
	 * 根据节点得到它的树节点
	 * @param node - 节点
	 * @return treeNode - 输入节点的树节点
	 */
	private TreeNode getTreeNode(Node node){
		for(TreeNode treeNode : treeNodeSet)
			if(treeNode.getNetworkNode().getNodeID() == node.getNodeID()){
				return treeNode;
			}
		return null;
	}	
}

/**
 * 最小树形图中树的节点
 * @author 涂方蕾
 * @version 1.0
 */
class TreeNode{

	private Node networkNode;
	private Set<TreeNode> children ;	
	private TreeNode parent;
	
	/**
	 * Constructor
	 * @param node - 节点
	 */
	TreeNode(Node node){
		networkNode = node;
		children = new HashSet<TreeNode>();
		parent = null;
	}
	
	/**
	 * 添加子节点
	 * @param child - 子节点
	 */
	public void addChild(TreeNode child){
		children.add(child);
	}
		
	/**
	 * 添加父节点
	 * @param parent - 父节点
	 */
	public void addParents(TreeNode parent){
		this.parent = parent;
	}
		
	/**
	 * 删除子节点
	 * @param child - 子节点
	 */
	public void removeChild(TreeNode child){		
		children.remove(child);
	}
	
	/**
	 * 返回子节点序列
	 * @return children - 子节点序列
	 */
	public Set<TreeNode> getChildren(){	
		return children;
	}
	
	/**
	 * 返回父节点
	 * @return parent - 父节点
	 */
	public TreeNode getParents(){				
		return parent;
	}
	
	/**
	 * 返回节点
	 * @return networkNode - 节点本身
	 */
	public Node getNetworkNode(){
		return networkNode;
	}
}

/**
 * 最小树形图中的缩点，定义为人工节点
 * @author 涂方蕾
 * @version 1.0
 */
class ManualNode{
	
	private Set<TreeNode> manualNode;   //人工节点包含的基本树节点
	private ManualNode parentManualNode; //父人工节点
	private Set<ManualNode> innerManualNodes; //人工节点内部的更低层次的人工节点
	private TreeNode parentNode;  //父节点
	private TreeNode mainNode;   //人工节点的代表点，即计算中最小入边的树节点，在后面以主点表示
	
	/**
	 * Constructor
	 * @param manualNode - 人工节点包含的基本树节点集合
	 */
	ManualNode(Set<TreeNode> manualNode){
		this.manualNode = manualNode;
		parentManualNode= null;
		innerManualNodes = null;
		parentNode = null;
		mainNode = null;
	}
		
	/**
	 * 设置人工节点内部的更低层次的人工节点
	 * @param innerManualNodes - 人工节点内部的更低层次的人工节点
	 */
	public void setInnerNodes(Set<ManualNode> innerManualNodes){		
		this.innerManualNodes = innerManualNodes;
	}	
		
	/**
	 * 设置父节点所在的人工顶点
	 * @param parentManualNode - 父人工节点
	 */
	public void setParentManNode(ManualNode parentManualNode){
		this.parentManualNode = parentManualNode;
	}	
	
	/**
	 * 设置父节点
	 * @param parentNode - 父节点
	 */
	public void setParentNode(TreeNode parentNode){
		this.parentNode = parentNode;
	}	
	
	/**
	 * 设置外界进入该人工节点的节点
	 * @param mainNode - 外界进入该人工节点的节点
	 */
	public void setMainNode(TreeNode mainNode){
		this.mainNode = mainNode;
	}
	
	/**
	 * 获取人工节点内部的更低层次的人工节点
	 * @return innerManualNodes - 人工节点内部的更低层次的人工节点
	 */
	public Set<ManualNode> getInnerNodes(){
		return innerManualNodes;
	}	
	
	/**
	 * 获取父节点所在的人工顶点
	 * @return parentManualNode - 父人工顶点
	 */
	public ManualNode getParentManNode(){
		return parentManualNode;
	}	

	/**
	 * 获取人工顶点
	 * @return manualNode - 人工顶点本身
	 */
	public Set<TreeNode> getManualNode(){
		return manualNode;
	}	
	
	/**
	 * 获取父节点
	 * @return parentNode - 父节点
	 */
	public TreeNode getParentNode(){
		return parentNode;
	}	

	/**
	 * 获取外界进入该人工节点的节点
	 * @return mainNode - 外界进入该人工节点的节点
	 */
	public TreeNode getMainNode(){
		return mainNode;
	}	
}
