package nest.mdc.algorithm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

/**
 * ��node�����Tsp
 * @author �µǿ�
 * version:1.0
 */
public class Tsp {
	public final static int TSPNUM = 15 ;     //����tsp�Ľڵ����
	
	Set nodeSetMin = null;
	int[] idStore;//�������tsp��Node��ID
	
	private int nodeNum;    //���и���
	private int popSize = 50;                 //��Ⱥ����
	private int maxgens = 100000;              //��������
	private double pxover = 0.8;              //�������
	private double pmultation = 0.2;         //�������
	private double[][] distance ;  
	private int range = 500;                 //�����жϺ�ʱֹͣ����������
	private  NodePool nodePool;
	
	
	/*
	 * ������
	 */
	private class genotype {
		int[] node = new int[nodeNum];        //��������ĳ������У�һ���������һ�ֳ�������˳��
		long fitness;                         //�û������Ӧ��
		double selectP;                       //ѡ�����
		double exceptP;                       //��������
		int isSelected;                       //�Ƿ�ѡ��
	}
	
	private genotype[] nodes = new genotype[popSize];//���genotype���͵�����,ÿ��nodes�൱��һ������
	
	/**
	 *  ���캯������ʼ����Ⱥ
	 *  ������һ������50���������Ⱥ��ÿ����������������34�����е�����
	 */
	public Tsp(NodePool nodePool) {
		this.nodePool = nodePool;
		nodeSetMin = nodePool.findNodeSetWithBattery(TSPNUM);    //�õ�����tsp����Ľڵ㼯�ϣ�����Ϊ���ϴ�С
		nodeNum = nodeSetMin.size(); 
		distance = new double[nodeNum][nodeNum];
		idStore = new int[nodeNum];     //����tsp����Ľڵ����
		                      
		System.out.println("nodeNum="+nodeNum);
		int count = 0;
		
		Iterator iterator = nodeSetMin.iterator();
		
		while(iterator.hasNext()){
			Node node = (Node)(iterator.next());
			idStore[count++] = node.getNodeID();//�����Node��ID

		}
		
		/*************������*******************/
//		System.out.print("��ʼ·��");
//		for(int i =0;i < count;i++){
//			System.out.print(idStore[i]+" ");
//		}
		
		for (int i=0;i<popSize;i++) {
			nodes[i] = new genotype();
			
			int[] num = new int[count];
			for(int j=0;j<count;j++)//����һ�����0��Conut��ֵ�������������,ÿ��ֵ��ӦidStore�е�һ��ID
				num[j] = j ;
			
			//��ʼ��citys[i]����
			int temp = nodeNum;
			for(int j=0;j<nodeNum;j++) {				
				int r = (int)(Math.random()*temp);//����0��temp����������,������temp
				nodes[i].node[j] = num[r];
				num[r] = num[temp-1];//num[r]�Ѿ���������jλ���ˣ������ڸ����������λ��,��num[temp-1]λ�õ�ֵ��û�и��裬���ø�����num[r]
				temp--;//num[temp-1]�Ѿ�������num[r]���ˣ�����Ҫ��Num[temp-1]��
			}
			
//			nodes[i].fitness = 0;//��������������Ӧ��
//			nodes[i].selectP = 0;
//			nodes[i].exceptP = 0;
//			nodes[i].isSelected = 0;
		}
		initDistance();//��ʼ�������м�ľ���
	}
	
	/**
	 * ����ÿ����Ⱥÿ������������Ӧ�ȣ�ѡ����ʣ��������ʺ��Ƿ�ѡ��
	 */
	public void CalAll() {
		for (int i=0;i<popSize;i++) {
			nodes[i].fitness = 0;
			nodes[i].selectP = 0;
			nodes[i].exceptP = 0;
			nodes[i].isSelected = 0;
		}
		CalFitness();
		CalSelectP();
		CalexceptP();
		CalIsSelected();
	}
	
	/**
	 * ��䣬����ѡ����䵽δѡ�ĸ��嵱��
	 * ��̭��Ⱥ�в�Ļ���
	 */
	public void pad() {
		int best = 0;
		int bad = 0;
		while(true) {
			while(nodes[best].isSelected<=1&&best<popSize-1){
				best++;
			}
				
			while(nodes[bad].isSelected!=0&&bad<popSize-1){
				bad++;
			}
				
			for (int i=0;i<nodeNum;i++){
				nodes[bad].node[i] = nodes[best].node[i];//isSelected>1�Ļ����踲��isSelected=0�Ļ�����̭�������
			}
				
			
			nodes[best].isSelected--;//isSelectedԽ������������Ⱥ�л���Խ�ã��û��򸲸ǲ����һ�Σ�������isSelected��ֵ����
			nodes[bad].isSelected++;//���Ļ��򱻸��Ǻ󣬽���isSelected��1��˵�����Ѿ�����
			
							
			if (bad==(popSize-1))
				break;
		}

	}
	
	/**
	 * �������庯��
	 * ѡ��20�Ի��򽻣�����40��������뽻�棬�������Ϊ0.8
	 */
	public void crossover() {
		int x;
		int y;
		int pop = (int)(popSize*pxover/2);
		while(pop>0) {
			x = (int)(Math.random()*popSize);
			y = (int)(Math.random()*popSize);
			executeCrossover(x,y);      //x,y������ִ�н���
			pop--;
		}
	}
	
	/**
	 * ִ�н��溯��
	 * @param����x
	 * @param����y
	 * �Ը���x�͸���yִ�мѵ㼯�Ľ��棬�Ӷ�������һ����������
	 * note:�Ƚ�x,y������dimension����ͬ�Ĳ�����-1������gp()��dimension����ͬ�ĵط����д��Ҹ�ֵ��-1��λ�ã������¸���
	 */
	private void executeCrossover(int x,int y) {
		int dimension = 0;
		//�����x,y���������в�ͬ�ĸ���
		for (int i=0;i<nodeNum;i++) {
			if (nodes[x].node[i] != nodes[y].node[i]) {
				dimension++;
			}
		}
		
		int diffItem = 0;
		double[] diff = new double[dimension];
		
		for (int i=0;i<nodeNum;i++) {
			if (nodes[x].node[i] != nodes[y].node[i]) {
				diff[diffItem] = nodes[x].node[i];
				nodes[x].node[i] = -1;
				nodes[y].node[i] = -1;
				diffItem++;
			}
		}
		
		Arrays.sort(diff);
		
		double[] temp = new double[dimension];
		temp = gp(x,dimension);
		
		for (int k=0;k<dimension;k++)
			for(int j=0;j<dimension;j++)
				if(temp[j]==k) {
					double item = temp[k];
					temp[k] = temp[j];
					temp[j] = item;
					
					item = diff[k];
					diff[k] = diff[j];
					diff[j] = item;
				}
		int tempDimension = dimension;
		int tempi = 0;
		
		while(tempDimension>0) {
			if(nodes[x].node[tempi] == -1) {
				nodes[x].node[tempi] = (int)diff[dimension-tempDimension];
				tempDimension--;
			}
			tempi++;
		}
		
		Arrays.sort(diff);
		
		temp = gp(y,dimension);
		
		for (int k=0;k<dimension;k++)
			for(int j=0;j<dimension;j++)
				if(temp[j]==k) {
					double item = temp[k];
					temp[k] = temp[j];
					temp[j] = item;
					
					item = diff[k];
					diff[k] = diff[j];
					diff[j] = item;
				}
		tempDimension = dimension;
		tempi = 0;
		
		while(tempDimension>0) {
			if(nodes[y].node[tempi] == -1) {
				nodes[y].node[tempi] = (int)diff[dimension-tempDimension];
				tempDimension--;
			}
			tempi++;
		}
			
	}
	
	/**
	 * ����x��ͬ������0��dimension����������д����temp[]��
	 * @param individual - ����
	 * @param dimension - ά��
	 * @return temp - �ѵ㼯�����ڽ��溯���Ľ���㣩 ��executeCrossover()������ʹ��
	 */
	private double[] gp(int individual, int dimension) {
		double[] temp = new double[dimension];
		double[] templ = new double[dimension];
		int p = 2*dimension+3;
		
		while(!isSushu(p))
			p++;
		
		for (int i=0;i<dimension;i++) {
			temp[i] = 2*Math.cos(2*Math.PI*(i+1)/p)*(individual+1);
			temp[i] = temp[i]-(int)temp[i];
			if(temp[i]<0)
				temp[i] = 1+temp[i];
		}
		for (int i=0;i<dimension;i++) {
			templ[i] = temp[i];
		}
		Arrays.sort(templ);    //����
		for (int i=0;i<dimension;i++)
			for (int j=0;j<dimension;j++)
				if(temp[j]==templ[i])
					temp[j] = i;
		return temp;
	}
	
	/**
	 * ����
	 * ����Ⱥ��ĳһЩ��������ĳһ���ֵ�ֵ������
	 */
	public void mutate() {
		double random;
		int temp;
		int temp1;
		int temp2;
		for (int i=0;i<popSize;i++) {
			random = Math.random();
			if (random<=pmultation) {
				temp1 = (int)(Math.random()*(nodeNum));
				temp2 = (int)(Math.random()*(nodeNum));
				temp = nodes[i].node[temp1];
				nodes[i].node[temp1] = nodes[i].node[temp2];
				nodes[i].node[temp2] = temp;
			}
		}
	}
	
	/**
	 * ��ӡ��ǰ���������г������У��Լ�����صĲ���
	 */
	
	/**
	 * ��ʼ��������֮��ľ���
	 */
	private void initDistance() {
		
		Node iNode = null;
		Node jNode = null;
		for (int i=0;i<nodeNum;i++) {
			for (int j=0;j<nodeNum;j++) {
				iNode = nodePool.getNodeWithID(idStore[i]);     //�ɽڵ�ID�õ��ýڵ������
				jNode = nodePool.getNodeWithID(idStore[j]);

				double dx = iNode.getXCoordinate()-jNode.getXCoordinate();
				double dy = iNode.getYCoordinate()-jNode.getYCoordinate();
				distance[i][j] = Math.sqrt(dx*dx + dy*dy);//�����i�����е���j�����еľ���
			}
		}
	}
		
	/**
	 * �������г������е���Ӧ��
	 * ��������Ӧ���ø����������д���ĳ����������·����ʾ
	 * ��Ӧ��Խ�󣬸û���Խ��
	 */		
	private void CalFitness() {
		for (int i=0;i<popSize;i++) {
			for (int j=0;j<nodeNum-1;j++){
				nodes[i].fitness += distance[nodes[i].node[j]][nodes[i].node[j+1]];
			}
            nodes[i].fitness += distance[nodes[i].node[0]][nodes[i].node[nodeNum-1]];
		}
	}
		
	/**
	 * ����ѡ�����
	 * ��Ӧ��Խ��ѡ�����Խ��
	 */
	private void CalSelectP() {
		long sum = 0;
		for (int i=0;i<popSize;i++)
			sum += nodes[i].fitness;
		for (int i=0;i<popSize;i++)
			nodes[i].selectP = (double)nodes[i].fitness/sum;
	}
		
	/**
	 * ������������
	 */
	private void CalexceptP() {
		for (int i=0;i<popSize;i++)
			nodes[i].exceptP = (double)nodes[i].selectP*popSize;
	}
		
	/**
	 * ����ó��������Ƿ���ţ�������ѡ�񣬽�����һ��
	 */
	private void CalIsSelected() {
		int needSelect = popSize;
		for (int i=0;i<popSize;i++)
			//����С��1/popSize�Ļ���ѡ��,��ѡ��·�������С���ǲ��ֻ���
			//nodes[i].exceptP<1���൱��nodes[i].selectPС��1/popSize,��ô�û����ڸ���Ⱥ�н���
			if (nodes[i].exceptP<1) {
				nodes[i].isSelected++;
				needSelect--;
			}
		
		
		//�����ŵ���������������ѡ��ǰneedSelect�����ŵĻ��򣬽����ǵ�isSelected�ټ�1
		double[] temp = new double[popSize];
		for (int i=0;i<popSize;i++) {
			temp[i] = nodes[i].exceptP*10;
		}
		int j=0;
		while(needSelect!=0) {
			for (int i=0;i<popSize;i++) {
				if ((int)temp[i]==j) {
					nodes[i].isSelected++;
					needSelect--;
					if(needSelect==0)
						break;
				}
			}
			j++;
		}
	}
		
	/**
	 * @param - x
	 * @return - �ж�һ�����Ƿ��������ĺ���
	 */
	private boolean isSushu (int x) {
		if (x<2) return false;
		for (int i=2;i<=x/2;i++)
			if (x%i==0&&x!=2) return false;
		return true;
	}
		
	/**
	 * @param x - ����
	 * @return x - �����ֵ�Ƿ�ȫ����ȣ�������ʾx.length�������Ž����ͬ�����㷨����
	 * ��һ����Ⱥ�����л������Ӧ�ȶ�һ����ʱ�򣬴����Ѿ�����
	 */
	private boolean isSame(long[] x) {
		for (int i=0;i<x.length-1;i++)
			if (x[i]!=x[i+1])
				return false;
		return true;
	}
		
	/**
	 * ��ӡ��������ŵ�·������
	 */
	private int[] printBestRoute() {
		int[] cityEnd1 = new int[nodeNum];

		CalAll();
		long temp = nodes[0].fitness;
		int index = 0;
		for (int i=1;i<popSize;i++) {
			if (nodes[i].fitness<temp) {
				temp = nodes[i].fitness;
				index = i;
			}
		}
		System.out.println();
		System.out.println("���·�������У�");

		for (int j=0;j<nodeNum;j++) {
			cityEnd1[j] = idStore[nodes[index].node[j]];
		}
		for (int m=0;m<cityEnd1.length;m++) {
			System.out.print(cityEnd1[m]+" ");
		}
		System.out.println();
		return cityEnd1;
	}
		
	/**
	 * �㷨ִ��
	 */
	public int[] run() {
		long[] result = new long[range];    //result��ʼ��Ϊ���е����ֶ������
		for (int i=0;i<range;i++)
			result[i] = i;
		int index = 0;       //�����е�λ��
		int num = 1;         //��num��
		while(maxgens>0) {
			System.out.println("������������������"+num+"������������������");
			CalAll();//��ʼ��������

			pad();//��̭���в�Ļ���
			
			crossover();//���н���仯
			mutate();//�ٽ��б���
			maxgens--;
			long temp = nodes[0].fitness;
			for (int i=1;i<popSize;i++)
				if (nodes[i].fitness<temp) {
					temp = nodes[i].fitness;
				}
			System.out.println("���ŵĽ�"+temp);//�ڶ��ٴ����Ž�
			result[index] = temp;
			if (isSame(result))//������range�������Ž����Ӧ����ͬ��ʱ�򣬴����Ѿ�����
				break;
			index++;
			if (index==range)
				index = 0;
			num++;
		}
		return printBestRoute();
	}
		
	/**
	 * @param a - ��ʼʱ��
	 * @param b - ����ʱ��
	 */
	public void CalTime(Calendar a, Calendar b) {
		long x = b.getTimeInMillis()-a.getTimeInMillis();
		long y = x/1000;
		x = x-1000*y;
		System.out.println("�㷨ִ��ʱ�䣺"+y+"."+x+"��");
	}
		
	/**
	 * �������
	 */
//	public void startTsp() {
//		Calendar a = Calendar.getInstance();    //��ʼʱ��
//		//Tsp tsp = new Tsp();
//		this.run();
//		Calendar b = Calendar.getInstance();    //����ʱ��
//		this.CalTime(a,b);
//	}	

}

