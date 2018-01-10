package nest.mdc.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nest.mdc.network.Node;

/**
 * 2��K��ֵ����
 * @author Lujunqiu
 * @version 1.0
 */
public class Bisecting_k_means extends Classifier {
//	private ArrayList<KCluster> clusterList ;//�ؼ���
//	private ArrayList<Node> dataSet;//���ݽڵ㼯����
	private int iter = 10 ; //2��K��Ԥ��ѭ������
	
	/**
	 * Constructor
	 * @param dataset
	 */
	public Bisecting_k_means(ArrayList<Node> dataset) {
		super(dataset);
	}
	
	/**
	 * 2��K��ֵ�������  
	 * @return ArrayList<KCluster>
	 */
	public void classify() {
		KCluster kCluster = new KCluster();
		for (Node node : dataSet) {
			kCluster.addNode(node);
		}
		clusterList.add(kCluster);//�ѳ�ʼ������������Ϊһ��cluster����cluster list
//		System.out.println(clusterList);
		while (true) {
			KCluster temp = end_condition();
			if (temp == null) {
				//System.out.println(clusterList);
				return ;
			}
			ArrayList<Double> SSE = new ArrayList<>();
			Map<Double, ArrayList<KCluster>> map = new HashMap<>();
			for (int i = 0; i < iter; i++) {
				Basic_Kmeans basic_Kmeans = new Basic_Kmeans(); //Ĭ��K=2�Ļ���K����
				basic_Kmeans.basic_k_means(temp.getNodeSet());
				ArrayList<KCluster> a = basic_Kmeans.getCluster();
				double sse = 0;
				for (KCluster kCluster2 : a) {
					double i1 = kCluster2.SSE();
					sse = sse + i1;
				}
				SSE.add(sse);
				map.put(sse, a);
			}
			 Double[] aa = SSE.toArray(new Double[iter]);
			 Arrays.sort(aa);
			 clusterList.remove(temp);
			 clusterList.addAll(map.get(aa[0]));
		}
	}
	
	/**
	 * ��ֹ������cluster list����ÿ��cluster�Ĺ�ģ������MAX_NUM
	 * @return KCluster
	 */
	public KCluster end_condition() { 
		for (KCluster e : clusterList) {
			if (e.getKClusterSize() > KCluster.MAX_NUM) {
				return e;//���г���M���ڵ�Ĵ��򷵻ظôص�����
			}
		}
		return null;//���дصĽڵ���������MAX_NUM�򷵻�null
	}
}
