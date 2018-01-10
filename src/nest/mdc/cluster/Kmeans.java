package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

/**
 * 改进的基本k均值聚类
 * @author Lujunqiu
 * @version 1.0
 */
public class Kmeans extends Classifier{
	// Private variables
	private int clusterNum; //分成k簇
	private int iterationNum; //迭代m次
	private int dataSetLength; //数据节点的个数
//	private ArrayList<Node> dataSet;//数据节点集链表
	private ArrayList<Node> center;// 中心点链表
//	private ArrayList<KCluster> clusterList; // 簇
	private ArrayList<Double> error; //误差
	private Random random;
	
	/**
	 * 设置需分组的原始数据集
	 * @param dataSet
	 */
	public void setDataSet(ArrayList<Node> dataSet){
		this.dataSet = dataSet;
	}
	
//	/**
//	 * 获取结果分组
//	 * @return ArrayList<KCluster>
//	 */
//	public ArrayList<KCluster> getClusters(){   
//		return clusterList;
//	}
	/**
	 * Constructor
	 * @param clusterNum 
	 * @param nodepool
	 */
	public Kmeans(int clusterNum,NodePool nodepool){ //构造函数，初始化k
		super(nodepool.getNodeList());
		if(clusterNum <= 0){
			clusterNum = 1;
		}
		this.clusterNum = clusterNum;
//		setDataSet(nodepool.getNodeList());
	}
	
	/**
	 * 初始化中心点链表
	 * @return  ArrayList<Node>
	 */
	public ArrayList<Node> initCenters(){ 
		ArrayList<Node> center = new ArrayList<Node>();
		int[] randoms = new int[clusterNum];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0]= temp;
		for (int i = 1; i < clusterNum; i++){//产生k个不重复的随机数
			flag = true;
			while (flag) {
				temp = random.nextInt(dataSetLength);
				int j = 0;
				while (j < i) {
					if (temp == randoms[j]){
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}
		for (int i = 0;i < clusterNum; i++){
			center.add(dataSet.get(randoms[i]));
		}
		return center;
		
	}
	
	/**
	 * 初始化簇集合，分为k簇的空数据集合
	 * @return ArrayList<KCluster>
	 */
	public ArrayList<KCluster> initCluster(){ 
		ArrayList<KCluster> clusterList = new ArrayList<KCluster>();
		for (int i = 0;i < clusterNum; i++){
			clusterList.add(new KCluster(center.get(i)));
		}
		return clusterList;
		
	}
	
	/**
	 * 初始化，初始化之前需设置原始数据集
	 */
	public void init(){ 
		iterationNum = 0;
		random = new Random();
		dataSetLength = dataSet.size();
		center = initCenters();
		clusterList = initCluster();
		error = new ArrayList<Double>();	
	}
	
	/**
	 * /获取距离集合中最小距离与第二近距离的质心位置
	 * @param distance
	 * @return int[]
	 */
	public int[] findDistance(double[] distance){
		int[] index = {0,0};
		for(int i = 0; i < index.length; i++){
			index[i] = i;
			for(int j = 0;j < distance.length;j++){
				if(distance[index[i]]>distance[j] && (!indexOf(index , j))){   //indexOf是本类中的一个方法
					index[i] = j;
				}
			}
		}
		return index;//index[0]存储最小距离的质心位置，index[1]存储倒数第二小的距离的质心位置
	}
	
	/**
	 * /获得排序后的数值在排序前的下标
	 * @param distance
	 * @return  int[]
	 */
	public int[] findD(double[] distance){
		int[] index = new int[clusterNum];
		ArrayList<Double> oldlist = new ArrayList<Double>();
		for(double e : distance){
			oldlist.add(e);
		}
		double[] newArray = sortDistance(distance);
		for(int i = 0; i < clusterNum; i++){
			index[i] = oldlist.indexOf(newArray[i]);
		}
		return index;
	}
/*	public int findDistance2(double[] distance){
//		double findDistance = distance[0];
//		int minLocation = 0;
//		for(int i = 1;i < distance.length; i++){
//			if (distance[i] < findDistance){
//				findDistance = distance[i];
//				minLocation = i;
//			}
//		}
//		return minLocation;
//	}
*/
		
	/**
	 * 判断j是否存在与num数组中
	 * @param num
	 * @param j
	 * @return boolean
	 */
	public boolean indexOf(int[] num , int j){ 
		for(int i = 0;i < num.length; i++){
			if(num[i] == j){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 计算代价
	 * @param distance
	 * @return double
	 */
	public double cost(double[] distance){ 	
		double cost = distance[1] - distance[0];
		return cost ;
	}
	
	/**
	 * 获取距离集合的升序排序结果
	 * @param distance
	 * @return double[]
	 */
    public double[] sortDistance(double[] distance){ 
    	Arrays.sort(distance);
    	return distance;
    }
    
    /**
     * 将当前节点放到距离最近的中心的类簇中
     */
	public void doCluster(){ 
    	double[] distance = new double[clusterNum];
    	for (int i = 0; i < dataSetLength; i++){
    		for(int j = 0 ; j < clusterNum; j++){   //初始化点到各个中心点的距离
    			distance[j] = dataSet.get(i).getDistance(center.get(j));
    		}
    		int[] sortedClusterID = findD(distance);   //按到中心点的距离从近至远排序
    		//int[] min_1_2_Location = findDistance(distance);
    		if(clusterList.get(sortedClusterID[0]).getKClusterSize() < KCluster.MAX_NUM){
    			clusterList.get(sortedClusterID[0]).addNode(dataSet.get(i));
    		}
    		else{
    			Node farthest_node = clusterList.get(sortedClusterID[0]).getFarthestNode();
    			double cost_1 = cost(distance);
    			double[] temp = get_DistanceArray(clusterNum,farthest_node);//得到类中最远节点的距离数组
    			double cost_2 = cost(temp);
    			if(cost_1 >= cost_2){//代价小的节点被挤出这个类
    				clusterList.get(sortedClusterID[0]).deleteNode(farthest_node);
    				clusterList.get(sortedClusterID[0]).addNode(dataSet.get(i));
    				int[] dis = findD(temp);
    				int a = 1;
    				while(true){
    					
    					if(clusterList.get(dis[a]).getKClusterSize() < KCluster.MAX_NUM){
    						clusterList.get(dis[a]).addNode(farthest_node);
    						break;
    					}
    					a++;
    					
    				}

//    				clusterList.get(dis[1]).add_Node(farthest_node);//将被挤出的节点加入新的类		
    			}
    			else{
    					int b = 1;
	                    while(true){
    					
	                    	if(clusterList.get(sortedClusterID[b]).getKClusterSize() < KCluster.MAX_NUM){
	                    		clusterList.get(sortedClusterID[b]).addNode(dataSet.get(i));
	                    		break;
	                    	}
	                    	b++;
	                    }
    			}
    		}
    	}
    }	
/*	public Node minCost(int clusterNum ,Node[] node){//返回2个节点中挤出代价小的节点
//		Node node_0 = node[0];
//		Node node_1 = node[1];
//		double[] temp_0 = get_DistanceArray(clusterNum,node_0);
//		double[] temp_1 = get_DistanceArray(clusterNum,node_1);
//		double cost_0 = cost(temp_0);
//		double cost_1 = cost(temp_1);
//		if(cost_0 >= cost_1){
//			return node_1;
//		}
//		else return node_0;
//	}
//	public void clusterSet2(int clusterNum ,ArrayList<Node> Set,Node node ){
//			double[] distance1 = get_DistanceArray(clusterNum,node);
//			int[] min_1_2_Location = findDistance(distance1);
//			if(clusterList.get(min_1_2_Location[0]).get_KCluster_size() < KCluster.MAX_NUM){
//    			clusterList.get(min_1_2_Location[0]).add_Node(node);
//    		}
//			else{
//				Node farthest_node = clusterList.get(min_1_2_Location[0]).get_farestNode();
//				Node[] nodeset = {node,farthest_node};
//				Node popNode = minCost(clusterNum,nodeset);
//				if(node.equals(popNode)){
//					clusterList.get(min_1_2_Location[0]).delete_Node(node);
//    				clusterList.get(min_1_2_Location[0]).add_Node(farthest_node);
//				}
//				if(farthest_node.equals(popNode)){
//					clusterList.get(min_1_2_Location[0]).delete_Node(farthest_node);
//    				clusterList.get(min_1_2_Location[0]).add_Node(node);
//				}
//				int  a = clusterNum - 1;
//				ArrayList<Node> recursionSet = new ArrayList<Node>();
//				for( Node e: clusterList.get(min_1_2_Location[0]).get_NodeSet()){
//					if(!Set.contains(e)){
//						recursionSet.add(e);
//					}
//				}
//				if (a < 3){
//					return;
//				}
//				clusterSet2(a,recursionSet,popNode);
//			}
//		return;
//		
	}
*/

	/**
	 * 得到当前节点到各个质心的距离数组
	 * @param clusterNum
	 * @param node
	 * @return double[] 
	 */
	public double[] get_DistanceArray(int clusterNum,Node node){
		double[] distance1 = new double[clusterNum];
		for(int j = 0;j < clusterNum;j++){
			distance1[j] = node.getDistance(center.get(j));
		}
		return distance1;
	}
	
	/**
	 * 求2个节点的误差
	 * @param x
	 * @param y
	 * @return double
	 */
	public double errorSquare(Node x , Node y){
		double a = x.getXCoordinate() - y.getXCoordinate();
		double b = x.getYCoordinate() - y.getYCoordinate();
		double errSquare = a * a + b * b;
		return errSquare;
	}
	
	/**
	 * 计算SSE
	 */
	public void countRule(){
		double jcF = 0;
		for(int i = 0; i < clusterList.size(); i++){
			for (int j = 0; j < clusterList.get(i).getKClusterSize(); j++){
				for (Node e : clusterList.get(i).getNodeSet()){
					jcF += errorSquare(e , center.get(i));
				}
			}
		}
		error.add(jcF);
	}
	
	/**
	 * 设置新的中心节点
	 */
	public void setNewCenter(){ 
//		double temp_x = 0;
//		double temp_y = 0;
		for (int i = 0; i < clusterNum; i++){
			double temp_x = 0;
			double temp_y = 0;
			int n = clusterList.get(i).getKClusterSize();
			if(n != 0){
				for(Node e: clusterList.get(i).getNodeSet()){
					temp_x += e.getXCoordinate();
					temp_y += e.getYCoordinate();
				}
				temp_x = temp_x / n;
				temp_y = temp_y / n;
				Node newCenter = new Node (temp_x,temp_y,i);
				center.set(i, newCenter);
			}
		}
	}
	
	/**
	 * kmeans算法核心过程
	 * @param nodepool
	 * @return ArrayList<KCluster>
	 */
    public void classify(){ 
    	init();//初始化
    	while (true) {
//    		for(Node e : dataSet){
//    			clusterSet2(clusterNum,dataSet,e);
//    		}
        	doCluster();//将当前节点放到距离最近的中心的类簇中
    		countRule();//计算误差平方和
    		if (iterationNum != 0 ){//误差满足条件，分组完成
    			if (error.get(iterationNum) - error.get(iterationNum - 1) <= 1){
    				break;
    			}
    		}
    		setNewCenter();//设置新的中心节点
    		iterationNum++;
    		clusterList.clear();
    		clusterList = initCluster();//初始化簇集合，分为k簇的空数据集合
    	} 
    }
      
}
