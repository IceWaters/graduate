package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import nest.mdc.network.Node;

/**
 * 基本K均值聚类,用于2分K均值聚类的辅助类
 * @author Lujunqiu
 * @version 1.0
 */
public class Basic_Kmeans {
	// Private variables
	private int clusterNum; //分成k簇
	private int iterationNum; //迭代m次
	private int dataSetLength; //数据节点的个数
	private ArrayList<Node> dataSet;//数据节点集链表
	private ArrayList<Node> center;// 中心点链表
	private ArrayList<KCluster> clusterList; // 簇
	private ArrayList<Double> error; //误差
	private Random random;
	
	/**
	 * Constructor
	 * @param k
	 */
    public Basic_Kmeans(int k ) { 
    	this.clusterNum = k ;
	}
    
    /**
     * Constructor
     */
    public Basic_Kmeans() {
    	this.clusterNum = 2;
    	
	}
    
    /**
     * 设置需分组的原始数据集
     * @param dataSet
     */
    public void setDataSet(ArrayList<Node> dataSet){ 
    	this.dataSet = dataSet;
	}
    
    /**
     * 获得结果分组
     * @return
     */
	public ArrayList<KCluster> getCluster(){  
		return clusterList;
	}
	
	/**
	 * 初始化中心点链表
	 * @return ArrayList<Node>
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
	 * 获取距离集合的升序排序结果
	 * @param distance
	 * @return double[]
	 */
    public double[] sortDistance(double[] distance){
    	Arrays.sort(distance);
    	return distance;
    }
    
    /**
     * 获得排序后的数值在排序前的下标
     * @param distance
     * @return int[]
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
	 * 将当前节点放到距离最近的中心的类簇中
	 */
	public void doCluster(){
    	double[] distance = new double[clusterNum];
    	for (int i = 0; i < dataSetLength; i++){
    		for(int j = 0 ; j < clusterNum; j++){   //初始化点到各个中心点的距离
    			distance[j] = dataSet.get(i).getDistance(center.get(j));
    		}
    		int[] sortedClusterID = findD(distance);   //按到中心点的距离从近至远排序
    			clusterList.get(sortedClusterID[0]).addNode(dataSet.get(i));       
    	}
    }	
	/**
	 * 设置新的中心节点
	 */
	public void setNewCenter(){ 
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
	 * k-means算法核心过程
	 * @param set
	 * @return ArrayList<KCluster>
	 */
	 public void  basic_k_means(Set<Node> set){ 
	    ArrayList<Node> setList = new ArrayList<>();
	    for (Node node : set) {
			setList.add(node);
		}
		 setDataSet(setList);
	    	init();//初始化
	    	while (true) {
	        	doCluster();//将当前节点放到距离最近的中心的类簇中
	    		countRule();//计算误差平方和
	    		if (iterationNum != 0 ){//误差满足条件，分组完成
	    			if (error.get(iterationNum) - error.get(iterationNum - 1) <= 0){
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
