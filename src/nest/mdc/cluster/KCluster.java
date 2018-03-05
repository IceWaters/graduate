package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.Point;

/**
 * K��ֵ����Ĵ�
 * 
 * @author Lujunqiu
 * @version 1.0
 */
public class KCluster {
	// static constants
	static final int MAX_NUM = 1; // ��Ĺ�ģ

	// Private variables
	private Point virtual_centerPoint; // ������ĵ�
	private Set<Node> kCluster = new HashSet<Node>(); // ���еĽڵ㼯��
	private int tag = 0; // ��־λ������schedule����������

	/**
	 * Constructor
	 */
	public KCluster() {
		virtual_centerPoint = new Point(0, 0);
		// kCluster = null ;
	}

	/**
	 * Constructor
	 * 
	 * @param node
	 */
	public KCluster(Node node) {
		// System.out.println(node);
		virtual_centerPoint = new Point(node.getXCoordinate(), node.getYCoordinate());
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public void addTag() {
		tag++;
	}

	public void setNodeSet(Set<Node> set) {
		this.kCluster = set;
	}

	/**
	 * ����������һ���ڵ�
	 * 
	 * @param node
	 * @return boolean
	 */
	public boolean addNode(Node node) {
		return kCluster.add(node);
	}

	/**
	 * ������ɾ��һ���ڵ�
	 * 
	 * @param node
	 * @return boolean
	 */
	public boolean deleteNode(Node node) {
		return kCluster.remove(node);
	}

	/**
	 * �������ĵ������
	 * 
	 * @param x
	 * @param y
	 */
	public void adjustCenterPoint(double x, double y) {
		virtual_centerPoint.setCoordinate(x, y);
	}

	/**
	 * �������ĵ�����
	 */
	public void calCenterPoint() {
		if (kCluster == null) {
			return;
		}
		double x = 0;
		double y = 0;
		for (Node node : kCluster) {
			x = x + node.getXCoordinate();
			y = y + node.getYCoordinate();
		}
		x = x / getKClusterSize();
		y = y / getKClusterSize();
		adjustCenterPoint(x, y);
	}

	/**
	 * �õ����ĵ������
	 * 
	 * @return Point
	 */
	public Point getCenterPoint() {
		return virtual_centerPoint;
	}

	/**
	 * �õ��صĴ�С
	 * 
	 * @return int
	 */
	int getKClusterSize() {
		return kCluster.size();
	}

	/**
	 * �õ��ڵ㼯��
	 * 
	 * @return Set<Node>
	 */
	public Set<Node> getNodeSet() {
		return kCluster;
	}

	/**
	 * ����hashcode����õ�kcluster��һ���ڵ�
	 */
	public Node getRandomNode() {
		Iterator<Node> temp = kCluster.iterator();
		if (temp.hasNext()) {
			return temp.next();
		} else {
			return null;
		}
	}

	/**
	 * �õ����о���������Զ�Ľڵ�
	 * 
	 * @return Node
	 */
	public Node getFarthestNode() {
		Node temp = null;
		Iterator<Node> it = kCluster.iterator();
		temp = it.next();
		// System.out.println(virtual_centerPoint);
		// System.out.println(temp);
		double a = temp.getDistance(virtual_centerPoint);
		for (Node e : kCluster) {
			if (e.getDistance(virtual_centerPoint) > a) {
				temp = e;
				a = e.getDistance(virtual_centerPoint);
			}
		}
		return temp;
	}

	/**
	 * �����ã��õ����нڵ㼯�ϵ�id����
	 * 
	 * @return int[]
	 */
	public int[] getNodeId() {
		ArrayList<Integer> test = new ArrayList<Integer>();
		for (Node e : kCluster) {
			test.add(e.getNodeID());
			// if(e.getNodeID() < 0)
			// System.out.println(e.getNodeID());
			// if(e.getNodeID()>249)
			// System.out.println(e.getNodeID());
		}
		int[] test1 = new int[test.size()];
		for (int i = 0; i < test.size(); i++) {
			test1[i] = test.get(i);
		}
		return test1;
	}
	
	/**
	 * ��ӡ��������Ľڵ�id
	 */
	public void printNodeId() {
		System.out.print("[");
		for(Node node : kCluster)
			System.out.print(node.getNodeID() +" ");
		System.out.print("]\n");
	}

	/**
	 * ��2���ڵ�����
	 * 
	 * @param x
	 * @param y
	 * @return double
	 */
	public double errorSquare(Point x, Point y) {
		double a = x.getXCoordinate() - y.getXCoordinate();
		double b = x.getYCoordinate() - y.getYCoordinate();
		double errSquare = a * a + b * b;
		return errSquare;
	}

	/**
	 * ����sse
	 * 
	 * @return double
	 */
	public double SSE() {
		double jcF = 0;
		for (int j = 0; j < getKClusterSize(); j++) {
			for (Node e : getNodeSet()) {
				jcF += errorSquare(e, getCenterPoint());
			}
		}
		return jcF;
	}

	/**
	 * �õ�2����֮��ľ��룬�����ľ���Ϊ��׼
	 * 
	 * @param cluster
	 * @return
	 */
	public double getDistance(KCluster cluster) {
		if (cluster == null || this.kCluster == null) {
			return Integer.MAX_VALUE;
		}
		return virtual_centerPoint.getDistance(cluster.getCenterPoint());
	}

	/**
	 * �õ�ÿ���������վ�ڵ㣨0,0������Ľڵ㣬���ڼ򻯵�Tspn
	 * 
	 * @return
	 */
	public Node getTspnNode() {
		double dis = Integer.MAX_VALUE;
		Point baseStation = new Point(0, 0);
		Node TspnNode = null;
		for (Node node : kCluster) {
			if (node.getDistance(baseStation) < dis) {
				TspnNode = node;
				dis = node.getDistance(baseStation);
			}
		}
		return TspnNode;
	}
}

/**
 * �Ľ��Ļ���k��ֵ����
 * 
 * @author Lujunqiu
 * @version 1.0
 */

// class Kmeans{
// // Private variables
// private int clusterNum; //�ֳ�k��
// private int iterationNum; //����m��
// private int dataSetLength; //���ݽڵ�ĸ���
// private ArrayList<Node> dataSet;//���ݽڵ㼯����
// private ArrayList<Node> center;// ���ĵ�����
// private ArrayList<KCluster> allCluster; // ��
// private ArrayList<Double> error; //���
// private Random random;
//
// /**
// * ����������ԭʼ���ݼ�
// * @param dataSet
// */
// public void setDataSet(ArrayList<Node> dataSet){
// this.dataSet = dataSet;
// }
//
// /**
// * ��ȡ�������
// * @return ArrayList<KCluster>
// */
// public ArrayList<KCluster> getCluster(){
// return allCluster;
// }
// /**
// * Constructor
// * @param clusterNum
// */
// public Kmeans(int clusterNum){ //���캯������ʼ��k
// if(clusterNum <= 0){
// clusterNum = 1;
// }
// this.clusterNum = clusterNum;
// }
//
// /**
// * ��ʼ�����ĵ�����
// * @return ArrayList<Node>
// */
// public ArrayList<Node> initCenters(){
// ArrayList<Node> center = new ArrayList<Node>();
// int[] randoms = new int[clusterNum];
// boolean flag;
// int temp = random.nextInt(dataSetLength);
// randoms[0]= temp;
// for (int i = 1; i < clusterNum; i++){//����k�����ظ��������
// flag = true;
// while (flag) {
// temp = random.nextInt(dataSetLength);
// int j = 0;
// while (j < i) {
// if (temp == randoms[j]){
// break;
// }
// j++;
// }
// if (j == i) {
// flag = false;
// }
// }
// randoms[i] = temp;
// }
// for (int i = 0;i < clusterNum; i++){
// center.add(dataSet.get(randoms[i]));
// }
// return center;
//
// }
//
// /**
// * ��ʼ���ؼ��ϣ���Ϊk�صĿ����ݼ���
// * @return ArrayList<KCluster>
// */
// public ArrayList<KCluster> initCluster(){
// ArrayList<KCluster> allCluster = new ArrayList<KCluster>();
// for (int i = 0;i < clusterNum; i++){
// allCluster.add(new KCluster(center.get(i)));
// }
// return allCluster;
//
// }
//
// /**
// * ��ʼ������ʼ��֮ǰ������ԭʼ���ݼ�
// */
// public void init(){
// iterationNum = 0;
// random = new Random();
// dataSetLength = dataSet.size();
// center = initCenters();
// allCluster = initCluster();
// error = new ArrayList<Double>();
// }
//
// /**
// * /��ȡ���뼯������С������ڶ������������λ��
// * @param distance
// * @return int[]
// */
// public int[] findDistance(double[] distance){
// int[] index = {0,0};
// for(int i = 0; i < index.length; i++){
// index[i] = i;
// for(int j = 0;j < distance.length;j++){
// if(distance[index[i]]>distance[j] && (!indexOf(index , j))){
// //indexOf�Ǳ����е�һ������
// index[i] = j;
// }
// }
// }
// return index;//index[0]�洢��С���������λ�ã�index[1]�洢�����ڶ�С�ľ��������λ��
// }
//
// /**
// * /�����������ֵ������ǰ���±�
// * @param distance
// * @return int[]
// */
// public int[] findD(double[] distance){
// int[] index = new int[clusterNum];
// ArrayList<Double> oldlist = new ArrayList<Double>();
// for(double e : distance){
// oldlist.add(e);
// }
// double[] newArray = sortDistance(distance);
// for(int i = 0; i < clusterNum; i++){
// index[i] = oldlist.indexOf(newArray[i]);
// }
// return index;
// }
/// * public int findDistance2(double[] distance){
//// double findDistance = distance[0];
//// int minLocation = 0;
//// for(int i = 1;i < distance.length; i++){
//// if (distance[i] < findDistance){
//// findDistance = distance[i];
//// minLocation = i;
//// }
//// }
//// return minLocation;
//// }
// */
//
// /**
// * �ж�j�Ƿ������num������
// * @param num
// * @param j
// * @return boolean
// */
// public boolean indexOf(int[] num , int j){
// for(int i = 0;i < num.length; i++){
// if(num[i] == j){
// return true;
// }
// }
// return false;
// }
//
// /**
// * �������
// * @param distance
// * @return double
// */
// public double cost(double[] distance){
// double cost = distance[1] - distance[0];
// return cost ;
// }
//
// /**
// * ��ȡ���뼯�ϵ�����������
// * @param distance
// * @return double[]
// */
// public double[] sortDistance(double[] distance){
// Arrays.sort(distance);
// return distance;
// }
//
// /**
// * ����ǰ�ڵ�ŵ�������������ĵ������
// */
// public void doCluster(){
// double[] distance = new double[clusterNum];
// for (int i = 0; i < dataSetLength; i++){
// for(int j = 0 ; j < clusterNum; j++){ //��ʼ���㵽�������ĵ�ľ���
// distance[j] = dataSet.get(i).getDistance(center.get(j));
// }
// int[] sortedClusterID = findD(distance); //�������ĵ�ľ���ӽ���Զ����
// //int[] min_1_2_Location = findDistance(distance);
// if(allCluster.get(sortedClusterID[0]).get_KCluster_size() <
// KCluster.MAX_NUM){
// allCluster.get(sortedClusterID[0]).add_Node(dataSet.get(i));
// }
// else{
// Node farthest_node = allCluster.get(sortedClusterID[0]).get_farthestNode();
// double cost_1 = cost(distance);
// double[] temp = get_DistanceArray(clusterNum,farthest_node);//�õ�������Զ�ڵ�ľ�������
// double cost_2 = cost(temp);
// if(cost_1 >= cost_2){//����С�Ľڵ㱻���������
// allCluster.get(sortedClusterID[0]).delete_Node(farthest_node);
// allCluster.get(sortedClusterID[0]).add_Node(dataSet.get(i));
// int[] dis = findD(temp);
// int a = 1;
// while(true){
//
// if(allCluster.get(dis[a]).get_KCluster_size() < KCluster.MAX_NUM){
// allCluster.get(dis[a]).add_Node(farthest_node);
// break;
// }
// a++;
//
// }
//
//// allCluster.get(dis[1]).add_Node(farthest_node);//���������Ľڵ�����µ���
// }
// else{
// int b = 1;
// while(true){
//
// if(allCluster.get(sortedClusterID[b]).get_KCluster_size() <
// KCluster.MAX_NUM){
// allCluster.get(sortedClusterID[b]).add_Node(dataSet.get(i));
// break;
// }
// b++;
// }
// }
// }
// }
// }
/// * public Node minCost(int clusterNum ,Node[] node){//����2���ڵ��м�������С�Ľڵ�
//// Node node_0 = node[0];
//// Node node_1 = node[1];
//// double[] temp_0 = get_DistanceArray(clusterNum,node_0);
//// double[] temp_1 = get_DistanceArray(clusterNum,node_1);
//// double cost_0 = cost(temp_0);
//// double cost_1 = cost(temp_1);
//// if(cost_0 >= cost_1){
//// return node_1;
//// }
//// else return node_0;
//// }
//// public void clusterSet2(int clusterNum ,ArrayList<Node> Set,Node node ){
//// double[] distance1 = get_DistanceArray(clusterNum,node);
//// int[] min_1_2_Location = findDistance(distance1);
//// if(allCluster.get(min_1_2_Location[0]).get_KCluster_size() <
// KCluster.MAX_NUM){
//// allCluster.get(min_1_2_Location[0]).add_Node(node);
//// }
//// else{
//// Node farthest_node = allCluster.get(min_1_2_Location[0]).get_farestNode();
//// Node[] nodeset = {node,farthest_node};
//// Node popNode = minCost(clusterNum,nodeset);
//// if(node.equals(popNode)){
//// allCluster.get(min_1_2_Location[0]).delete_Node(node);
//// allCluster.get(min_1_2_Location[0]).add_Node(farthest_node);
//// }
//// if(farthest_node.equals(popNode)){
//// allCluster.get(min_1_2_Location[0]).delete_Node(farthest_node);
//// allCluster.get(min_1_2_Location[0]).add_Node(node);
//// }
//// int a = clusterNum - 1;
//// ArrayList<Node> recursionSet = new ArrayList<Node>();
//// for( Node e: allCluster.get(min_1_2_Location[0]).get_NodeSet()){
//// if(!Set.contains(e)){
//// recursionSet.add(e);
//// }
//// }
//// if (a < 3){
//// return;
//// }
//// clusterSet2(a,recursionSet,popNode);
//// }
//// return;
////
// }
// */
//
// /**
// * �õ���ǰ�ڵ㵽�������ĵľ�������
// * @param clusterNum
// * @param node
// * @return double[]
// */
// public double[] get_DistanceArray(int clusterNum,Node node){
// double[] distance1 = new double[clusterNum];
// for(int j = 0;j < clusterNum;j++){
// distance1[j] = node.getDistance(center.get(j));
// }
// return distance1;
// }
//
// /**
// * ��2���ڵ�����
// * @param x
// * @param y
// * @return double
// */
// public double errorSquare(Node x , Node y){
// double a = x.getXCoordinate() - y.getXCoordinate();
// double b = x.getYCoordinate() - y.getYCoordinate();
// double errSquare = a * a + b * b;
// return errSquare;
// }
//
// /**
// * ����SSE
// */
// public void countRule(){
// double jcF = 0;
// for(int i = 0; i < allCluster.size(); i++){
// for (int j = 0; j < allCluster.get(i).get_KCluster_size(); j++){
// for (Node e : allCluster.get(i).get_NodeSet()){
// jcF += errorSquare(e , center.get(i));
// }
// }
// }
// error.add(jcF);
// }
//
// /**
// * �����µ����Ľڵ�
// */
// public void setNewCenter(){
//// double temp_x = 0;
//// double temp_y = 0;
// for (int i = 0; i < clusterNum; i++){
// double temp_x = 0;
// double temp_y = 0;
// int n = allCluster.get(i).get_KCluster_size();
// if(n != 0){
// for(Node e: allCluster.get(i).get_NodeSet()){
// temp_x += e.getXCoordinate();
// temp_y += e.getYCoordinate();
// }
// temp_x = temp_x / n;
// temp_y = temp_y / n;
// Node newCenter = new Node (temp_x,temp_y,i);
// center.set(i, newCenter);
// }
// }
// }
//
// /**
// * kmeans�㷨���Ĺ���
// * @param nodepool
// * @return ArrayList<KCluster>
// */
// public ArrayList<KCluster> k_means(NodePool nodepool){
// setDataSet(nodepool.getNodeList());
// init();//��ʼ��
// while (true) {
//// for(Node e : dataSet){
//// clusterSet2(clusterNum,dataSet,e);
//// }
// doCluster();//����ǰ�ڵ�ŵ�������������ĵ������
// countRule();//�������ƽ����
// if (iterationNum != 0 ){//��������������������
// if (error.get(iterationNum) - error.get(iterationNum - 1) <= 1){
// break;
// }
// }
// setNewCenter();//�����µ����Ľڵ�
// iterationNum++;
// allCluster.clear();
// allCluster = initCluster();//��ʼ���ؼ��ϣ���Ϊk�صĿ����ݼ���
// }
// return allCluster ;
// }
//
// }
