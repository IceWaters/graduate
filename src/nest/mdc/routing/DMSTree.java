package nest.mdc.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import nest.mdc.network.Node;

/**
 * ������С����ͼ��Ҫ�㷨����
 * @author Ϳ����
 * @version 1.0
 */
public class DMSTree{

	private Set<TreeNode> treeNodeSet = new HashSet<TreeNode>();	//���ڵ㼯��
	private Set<ManualNode> manualNodes = new HashSet<ManualNode>();  //��ǰ������ε��˹����㼯��
	private final double INF = 9999;	//�����룬��ʾ����ֱ��
	private final int nodeNum;	//�ڵ���
	private double[][] map ;	//map[i][j]�д�Žڵ�i���ڵ�j�ľ��룬������ֱ������INF��ʾ
	private TreeNode root;	
	private double cost = 0;
		
	/**
	 * Constructor
	 * @param nodeSet - �ڵ㼯��
	 */
	public DMSTree(Set<Node> nodeSet){
		for(Node node : nodeSet){
			//���������������ڵ�
			if(node != null){
				TreeNode treenode = new TreeNode(node);
				treeNodeSet.add(treenode);
			}
		}		
		for(TreeNode treeNode : treeNodeSet){
			//��ÿ���������ڵ��װ���˹��ڵ�
			if(treeNode != null){
				Set<TreeNode> nodeSetTemp = new HashSet<TreeNode>();
				nodeSetTemp.add(treeNode);
				ManualNode manualNode = new ManualNode(nodeSetTemp);
				manualNodes.add(manualNode);
			}
		}
		nodeNum = nodeSet.size();	//��ȡ�ڵ���
		map = new double[nodeNum][nodeNum];
		initialMap();
		root = getRootNode();
					
		while(refreshLoop()){
			//���ϵ��һ�����
			refreshPaManNode();			
		}
		for(ManualNode manualNode : manualNodes)
			//�����û�л�ʱ����ʼչ���ӱ�
			if(manualNode.getMainNode() != root && manualNode.getMainNode() != null){
				linkNode(manualNode);
			}
	//	hasLoop(); //����������ɵ�ͼ���Ƿ��л�
		System.out.println("The weight is : " + cost);
	}
	
	/**
	 * ������С����ͼ�нڵ�ļ���
	 * @return treeNodeSet - ��С����ͼ�е����ڵ㼯��
	 */
	public Set<TreeNode> getTreeNode(){
		return treeNodeSet;
	}
	
	/**
	 * ������С����ͼ�ĸ��ڵ�
	 * @return root - ��С����ͼ�ĸ��ڵ�
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
	 * �ӽڵ������ȡ�ڵ㣬���ݽڵ�֮����ھӹ�ϵ��ȡmap[MAX][MAX],����ڵ�i��ڵ�j��Ϊ�ھӣ���map[i][j]����getWeight(Node_i, Node_j)��������ΪINF
	 * ���Ҽ���ÿһ�������С���                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
	 * ���ȼ������еĽڵ�֮���ǲ��ɴ�ģ���map[][]�����ֵ��ΪINF
	 */
	private void initialMap(){			
		for(int i = 0; i < nodeNum; i++)
			for(int j = 0; j < nodeNum; j++)
				map[i][j] = INF;
		
		//���ݽڵ�֮����ھӹ�ϵ��ȡmap[MAX][MAX],����ڵ�i��ڵ�j��Ϊ�ھӣ���map[i][j]��ֵ����ΪgetWeight(Node_i, Node_j)
		for(ManualNode manualNode : manualNodes){
			for(TreeNode desTreeNode : manualNode.getManualNode()){
					if(desTreeNode == null) continue;
					int desNodeID =  desTreeNode.getNetworkNode().getNodeID();//�õ�Ŀ�Ľڵ�j��ID			
					double minWeight = INF;
					Node minNode = null;
					Set<Node> neighbors = desTreeNode.getNetworkNode().neighbors.get(1).getNeig();//���εõ��ڵ���ھӽڵ�
					for(Node soulNode : neighbors){
						int soulNodeID = soulNode.getNodeID(); //�õ�Դ�ڵ�i��ID
						map[soulNodeID][desNodeID] = getWeight(soulNode, desTreeNode.getNetworkNode());//����map[i][j]ΪWeight
						if(map[soulNodeID][desNodeID] < minWeight){
							//������С���
							minWeight = map[soulNodeID][desNodeID];
							minNode = soulNode;					
						}					
					}
					
					if(desNodeID != 0){
						cost = cost + minWeight;
						manualNode.setParentManNode(findManualNode(getTreeNode(minNode), manualNodes));//������С���
						manualNode.setMainNode(desTreeNode);//��������
						manualNode.setParentNode(getTreeNode(minNode)); //���ø��ڵ�
					}
					if(minNode.getNodeID() == 0)
						System.out.println("initial : " + desNodeID);
			}
		}		
	}
	
	/**
	 * �õ����򻷣�Ȼ���������
	 * ����ÿ���ڵ�ֻ��һ����ĸ�ڵ㣬��˷����һ����������·���ֲ������
	 * @return hasLoop - ����������򻷣�����true������false
	 */
	private boolean refreshLoop(){				
	//	System.out.println("refreshLoop");
		boolean hasLoop = false;//��¼�Ƿ�������
		Set<ManualNode> manualNodesTemp = new HashSet<ManualNode>();  //��ǰ������ε��˹����㼯��
		Set<ManualNode> confirmedManualNode = new HashSet<ManualNode>(); //�洢�Ѿ���֤�����˹�����
		confirmedManualNode.add(findManualNode(root, manualNodes));//���ڸ��ڵ�û����ߣ��ʲ���Ҫ������֤���ɼ����Ѿ���֤���Ľڵ㼯����
		for(ManualNode manualNode : manualNodes){
			if(!confirmedManualNode.contains(manualNode)){
			//������˹��ڵ㲻��֮ǰ����֤���Ļ���������֤��������һ�˹��ڵ�
				Set<ManualNode> loop = new HashSet<ManualNode>(); //������ͬһ����������ڵ�
				ManualNode parentNode = manualNode.getParentManNode();
				while((parentNode != null) && (parentNode != manualNode)
						&& (!loop.contains(parentNode)) ){	
					loop.add(parentNode);
					parentNode = parentNode.getParentManNode();
				}
				
				if(loop.contains(parentNode) || parentNode == null){
					//������ڵ��ظ�ĸ�ڵ����������ָ��null��������ڵ㲻�ڻ���
					loop.clear();
					manualNodesTemp.add(manualNode);
					confirmedManualNode.add(manualNode);
				}
				else if(parentNode == manualNode){
					//�л��Ļ��������µ��˹�����,������Ȩֵ
					hasLoop = true;
					loop.add(manualNode);
					ManualNode newManualNode = newManualNode(loop);  //�����µ��˹�����
					newManualNode.setInnerNodes(loop);  //�����˹������ڲ�����
					refreshWeight(newManualNode); //����Ȩֵ
					manualNodesTemp.add(newManualNode);  //���µ�ǰ�����������˹�����
					confirmedManualNode.addAll(loop);
				}								
			}
		}
		manualNodes = manualNodesTemp;
		return hasLoop;
	}
	
	/**
	 * ����չ����������
	 * @param manualNode - �������ڵ������ɵ��˹��ڵ�
	 */
	private void linkNode(ManualNode manualNode){
		TreeNode mainNode = manualNode.getMainNode();
		if(mainNode.getParents() == null)
			setMinSide(manualNode.getParentNode(), mainNode);//�����˹���������
		if(manualNode.getManualNode().size() <= 1) 
			//Ϊ�������ڵ�ʱ������
			return ;		
		Set<ManualNode> manualNodes = manualNode.getInnerNodes();//�õ��˹������ڲ����Ͳ�ε��˹����
		ManualNode mainManualNode = findManualNode(mainNode, manualNodes);//�˹����������
		ManualNode manNode = mainManualNode.getParentManNode();//ȷ�����˹������ڵ�����ĸ��˹�����		
		while(manNode != mainManualNode){
			//�����ڲ��˹��ڵ�
			linkNode(manNode);	
			manNode = manNode.getParentManNode();	
		}
		//�ı��������ڵ��ڲ��˹����������͸��ڵ�
		mainManualNode.setMainNode(mainNode);
		mainManualNode.setParentNode(manualNode.getParentNode());
		linkNode(mainManualNode);
	}
	
	/**
	 * �����˹�����ĸ��˹�����
	 */
	private void refreshPaManNode(){
	//	System.out.println("refreshPaManNode");
		for(ManualNode manualNode : manualNodes){
			TreeNode treeNode = manualNode.getParentNode();
			manualNode.setParentManNode(findManualNode(treeNode, manualNodes));
		}
	}		
	
	/**
	 * �ж�������С�������������ڵ㼯�����Ƿ��л�,���Դ���
	 * @return boolean - ����ڵ㼯���л��л�������true������Ϊfalse
	 */
	private boolean hasLoop(){
		for(TreeNode treeNode : treeNodeSet){
			Set<TreeNode> loop = new HashSet<TreeNode>(); //������ͬһ����������ڵ�
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
	 * ���������˹����㼯���γɸ��߲�ε��µ��˹�����
	 * @param manualNodes - �˹��ڵ㼯��
	 * @return manualNode - �����γɵ��µ��˹�����
	 */
	private ManualNode newManualNode(Set<ManualNode> manualNodes){
		Set<TreeNode> treeNodes = new HashSet<TreeNode>();
		for(ManualNode manualNode : manualNodes)
			treeNodes.addAll(manualNode.getManualNode());
		ManualNode manualNode = new ManualNode(treeNodes);
		return manualNode;
	}
		
	/**
	 * ���ظ������ڵ���ɵ��˹��ڵ�,��Ϊ�ʼ�˹�������������ڵ㶼��ͬʱʹ��
	 * @param treeNode - ���������ڵ�
	 * @param manNodes - �˹����㼯��
	 * @return manualNode - �����ڵ����ڵ��˹�����
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
	 * ��ȡ�ߵ�Ȩ�أ�Ȩ��W = desNode.getRemainingBattery * soulNode.getDistance(desNode)
	 * @param souNode - ���
	 * @param desNode - �յ�
	 * @return weight - ����֮��ߵ�Ȩֵ
	 */
	private double getWeight(Node souNode, Node desNode){
		double weight = desNode.getRemainingBattery() * souNode.getDistance(desNode);
		return weight;
	}			
	
	/**
	 * ���µ��¶����Ȩֵ
	 * @param manualNode - ���γɵ��˹�����
	 */
	private void refreshWeight(ManualNode manualNode){
		TreeNode minNode = null;
		TreeNode minPaNode = null;
		double minWeight = INF;
		ArrayList<Integer> nodeList = new ArrayList<Integer>();//�洢�˹��ڵ��еĽڵ�ID����
		for(TreeNode treeNode : manualNode.getManualNode()){
			nodeList.add(treeNode.getNetworkNode().getNodeID());
			//System.out.println("new Loop : " + treeNode.getNetworkNode().getNodeID());
		}
		for(ManualNode mNode : manualNode.getInnerNodes()){
			//�����˹������ڲ��ĸ��Ͳ�ε��˹�����
			int parentNodeID = mNode.getParentNode().getNetworkNode().getNodeID();
			int mainNodeID = mNode.getMainNode().getNetworkNode().getNodeID(); 
			double inWeight = map[parentNodeID][mainNodeID];  //�˹���������Ȩֵ
			
			for(TreeNode treeNode : mNode.getManualNode()){
				Set<Node> neighbors = treeNode.getNetworkNode().neighbors.get(1).getNeig();//���εõ��ڵ���ھӽڵ�
				for(Node soulNode : neighbors){
				//�����������е���ھӵĵ�ļ��ϣ�����Ȩֵ
					if(!nodeList.contains(soulNode.getNodeID())){
						map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] = 
							map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] - inWeight;
						if(map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()] < minWeight){
							//�ҳ���С���
							minWeight = map[soulNode.getNodeID()][treeNode.getNetworkNode().getNodeID()];
							minNode = treeNode;
							minPaNode = getTreeNode(soulNode);
						}
					}						
				}		
			}
		}
		cost = cost + minWeight;
		manualNode.setMainNode(minNode);//��������
		manualNode.setParentNode(minPaNode);//���ø��ڵ�
	}
	
	/**
	 * �����ã���ӡ�˹�������ڲ��������ڵ�
	 * @param manualNode - �˹�����
	 */
	private void printManualNode(ManualNode manualNode){
		System.out.print("manualNode : ");
		for(TreeNode treeNode : manualNode.getManualNode()){
			System.out.print(treeNode.getNetworkNode().getNodeID() + ", ");
		}
		System.out.println("");
	}

	/**
	 * ������С�����
	 * @param soulTreeNode - ��ʼ���ڵ�
	 * @param desTreeNode - Ŀ�����ڵ�
	 */
	private void setMinSide(TreeNode soulTreeNode, TreeNode desTreeNode){
		soulTreeNode.addChild(desTreeNode);
		desTreeNode.addParents(soulTreeNode);
		if(soulTreeNode.getNetworkNode().getNodeID() == 0)
			System.out.println("add + " + soulTreeNode.getChildren());
	//	System.out.println("count : " + ++count);
	}
        
	/**
	 * �����ڵ����飬�õ��������С�Ľڵ㣬�����ڵ�
	 * @return treeNode - ���ڵ�
	 */
	private TreeNode getRootNode(){
		for(TreeNode treeNode : treeNodeSet)
			if(treeNode.getNetworkNode().getNodeID() == 0){
				return treeNode;
			}
		return null;
	}
	
	/**
	 * ���ݽڵ�õ��������ڵ�
	 * @param node - �ڵ�
	 * @return treeNode - ����ڵ�����ڵ�
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
 * ��С����ͼ�����Ľڵ�
 * @author Ϳ����
 * @version 1.0
 */
class TreeNode{

	private Node networkNode;
	private Set<TreeNode> children ;	
	private TreeNode parent;
	
	/**
	 * Constructor
	 * @param node - �ڵ�
	 */
	TreeNode(Node node){
		networkNode = node;
		children = new HashSet<TreeNode>();
		parent = null;
	}
	
	/**
	 * ����ӽڵ�
	 * @param child - �ӽڵ�
	 */
	public void addChild(TreeNode child){
		children.add(child);
	}
		
	/**
	 * ��Ӹ��ڵ�
	 * @param parent - ���ڵ�
	 */
	public void addParents(TreeNode parent){
		this.parent = parent;
	}
		
	/**
	 * ɾ���ӽڵ�
	 * @param child - �ӽڵ�
	 */
	public void removeChild(TreeNode child){		
		children.remove(child);
	}
	
	/**
	 * �����ӽڵ�����
	 * @return children - �ӽڵ�����
	 */
	public Set<TreeNode> getChildren(){	
		return children;
	}
	
	/**
	 * ���ظ��ڵ�
	 * @return parent - ���ڵ�
	 */
	public TreeNode getParents(){				
		return parent;
	}
	
	/**
	 * ���ؽڵ�
	 * @return networkNode - �ڵ㱾��
	 */
	public Node getNetworkNode(){
		return networkNode;
	}
}

/**
 * ��С����ͼ�е����㣬����Ϊ�˹��ڵ�
 * @author Ϳ����
 * @version 1.0
 */
class ManualNode{
	
	private Set<TreeNode> manualNode;   //�˹��ڵ�����Ļ������ڵ�
	private ManualNode parentManualNode; //���˹��ڵ�
	private Set<ManualNode> innerManualNodes; //�˹��ڵ��ڲ��ĸ��Ͳ�ε��˹��ڵ�
	private TreeNode parentNode;  //���ڵ�
	private TreeNode mainNode;   //�˹��ڵ�Ĵ���㣬����������С��ߵ����ڵ㣬�ں����������ʾ
	
	/**
	 * Constructor
	 * @param manualNode - �˹��ڵ�����Ļ������ڵ㼯��
	 */
	ManualNode(Set<TreeNode> manualNode){
		this.manualNode = manualNode;
		parentManualNode= null;
		innerManualNodes = null;
		parentNode = null;
		mainNode = null;
	}
		
	/**
	 * �����˹��ڵ��ڲ��ĸ��Ͳ�ε��˹��ڵ�
	 * @param innerManualNodes - �˹��ڵ��ڲ��ĸ��Ͳ�ε��˹��ڵ�
	 */
	public void setInnerNodes(Set<ManualNode> innerManualNodes){		
		this.innerManualNodes = innerManualNodes;
	}	
		
	/**
	 * ���ø��ڵ����ڵ��˹�����
	 * @param parentManualNode - ���˹��ڵ�
	 */
	public void setParentManNode(ManualNode parentManualNode){
		this.parentManualNode = parentManualNode;
	}	
	
	/**
	 * ���ø��ڵ�
	 * @param parentNode - ���ڵ�
	 */
	public void setParentNode(TreeNode parentNode){
		this.parentNode = parentNode;
	}	
	
	/**
	 * ������������˹��ڵ�Ľڵ�
	 * @param mainNode - ��������˹��ڵ�Ľڵ�
	 */
	public void setMainNode(TreeNode mainNode){
		this.mainNode = mainNode;
	}
	
	/**
	 * ��ȡ�˹��ڵ��ڲ��ĸ��Ͳ�ε��˹��ڵ�
	 * @return innerManualNodes - �˹��ڵ��ڲ��ĸ��Ͳ�ε��˹��ڵ�
	 */
	public Set<ManualNode> getInnerNodes(){
		return innerManualNodes;
	}	
	
	/**
	 * ��ȡ���ڵ����ڵ��˹�����
	 * @return parentManualNode - ���˹�����
	 */
	public ManualNode getParentManNode(){
		return parentManualNode;
	}	

	/**
	 * ��ȡ�˹�����
	 * @return manualNode - �˹����㱾��
	 */
	public Set<TreeNode> getManualNode(){
		return manualNode;
	}	
	
	/**
	 * ��ȡ���ڵ�
	 * @return parentNode - ���ڵ�
	 */
	public TreeNode getParentNode(){
		return parentNode;
	}	

	/**
	 * ��ȡ��������˹��ڵ�Ľڵ�
	 * @return mainNode - ��������˹��ڵ�Ľڵ�
	 */
	public TreeNode getMainNode(){
		return mainNode;
	}	
}
