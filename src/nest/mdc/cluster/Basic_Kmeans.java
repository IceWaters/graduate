package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import nest.mdc.network.Node;

/**
 * ����K��ֵ����,����2��K��ֵ����ĸ�����
 * @author Lujunqiu
 * @version 1.0
 */
public class Basic_Kmeans {
	// Private variables
	private int clusterNum; //�ֳ�k��
	private int iterationNum; //����m��
	private int dataSetLength; //���ݽڵ�ĸ���
	private ArrayList<Node> dataSet;//���ݽڵ㼯����
	private ArrayList<Node> center;// ���ĵ�����
	private ArrayList<KCluster> clusterList; // ��
	private ArrayList<Double> error; //���
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
     * ����������ԭʼ���ݼ�
     * @param dataSet
     */
    public void setDataSet(ArrayList<Node> dataSet){ 
    	this.dataSet = dataSet;
	}
    
    /**
     * ��ý������
     * @return
     */
	public ArrayList<KCluster> getCluster(){  
		return clusterList;
	}
	
	/**
	 * ��ʼ�����ĵ�����
	 * @return ArrayList<Node>
	 */
	public ArrayList<Node> initCenters(){
		ArrayList<Node> center = new ArrayList<Node>();
		int[] randoms = new int[clusterNum];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0]= temp;
		for (int i = 1; i < clusterNum; i++){//����k�����ظ��������
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
	 * ��ʼ���ؼ��ϣ���Ϊk�صĿ����ݼ���
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
	 * ��ʼ������ʼ��֮ǰ������ԭʼ���ݼ�
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
	 * ��ȡ���뼯�ϵ�����������
	 * @param distance
	 * @return double[]
	 */
    public double[] sortDistance(double[] distance){
    	Arrays.sort(distance);
    	return distance;
    }
    
    /**
     * �����������ֵ������ǰ���±�
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
	 * ��2���ڵ�����
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
	 * ����SSE
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
	 * ����ǰ�ڵ�ŵ�������������ĵ������
	 */
	public void doCluster(){
    	double[] distance = new double[clusterNum];
    	for (int i = 0; i < dataSetLength; i++){
    		for(int j = 0 ; j < clusterNum; j++){   //��ʼ���㵽�������ĵ�ľ���
    			distance[j] = dataSet.get(i).getDistance(center.get(j));
    		}
    		int[] sortedClusterID = findD(distance);   //�������ĵ�ľ���ӽ���Զ����
    			clusterList.get(sortedClusterID[0]).addNode(dataSet.get(i));       
    	}
    }	
	/**
	 * �����µ����Ľڵ�
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
	 * k-means�㷨���Ĺ���
	 * @param set
	 * @return ArrayList<KCluster>
	 */
	 public void  basic_k_means(Set<Node> set){ 
	    ArrayList<Node> setList = new ArrayList<>();
	    for (Node node : set) {
			setList.add(node);
		}
		 setDataSet(setList);
	    	init();//��ʼ��
	    	while (true) {
	        	doCluster();//����ǰ�ڵ�ŵ�������������ĵ������
	    		countRule();//�������ƽ����
	    		if (iterationNum != 0 ){//��������������������
	    			if (error.get(iterationNum) - error.get(iterationNum - 1) <= 0){
	    				break;
	    			}
	    		}
	    		setNewCenter();//�����µ����Ľڵ�
	    		iterationNum++;
	    		clusterList.clear();
	    		clusterList = initCluster();//��ʼ���ؼ��ϣ���Ϊk�صĿ����ݼ���
	    	} 
	   
	    }
	 
}
