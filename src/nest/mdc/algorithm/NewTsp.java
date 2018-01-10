package nest.mdc.algorithm;

/*
 * date:2015/12/16
 * note:��startTsp()��run()��printBestRoute()����ֵ�޸�Ϊ�洢CollectionNode��ArrayList.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.network.*;


/*
 * input:����TspPoint��Set,��convertToTspNode���в���
 * output:��startTsp()�����õ�tsp���tspPoint����
 * ����tspPoint����Node��colletionNode�ĸ���Ϊ�գ����жϸ�tspPoint����Node����collectionNodeת��������
 */

public class NewTsp {
	private int tspPointNum;    //���и���
	private int popSize = 50;                 //��Ⱥ����
	private int maxgens = 20000;              //��������
	private double pxover = 0.8;              //�������
	private double pmultation = 0.05;         //�������
	private double[][] distance ;  
	private int range = 500;                 //�����жϺ�ʱֹͣ����������
	Set<TspPoint> tspPointSetMin = null;
	int[] idStore ;//�������tsp�ڵ�Node��ID
	
	private class genotype {
		int[] tspPoint = new int[tspPointNum];        //��������ĳ������У�һ���������һ�ֳ�������˳��
		double fitness;                         //�û������Ӧ��
		double selectP;                       //ѡ�����
		double exceptP;                       //��������
		int isSelected;                       //�Ƿ�ѡ��
	}
	
	private genotype[] tspPoints = new genotype[popSize];//���genotype���͵�����,ÿ��nodes�൱��һ������
	
	/**
	 *  ���캯������ʼ����Ⱥ
	 *  
	 */
	public NewTsp(Set<TspPoint> set) {
		tspPointSetMin = set;    //�õ�����tsp����Ľڵ㼯�ϣ�����Ϊ���ϴ�С
		tspPointNum = tspPointSetMin.size();                       //����tsp����Ľڵ����
		idStore = new int[tspPointNum];
		distance = new double[tspPointNum][tspPointNum];
		
		int count = 0;
		
		Iterator iterator = tspPointSetMin.iterator();
		while(iterator.hasNext()){
			TspPoint tspPoint = (TspPoint)(iterator.next());
			idStore[count++] = tspPoint.getID();//�����Node��ID

		}
		
		for (int i=0;i<popSize;i++) {
			tspPoints[i] = new genotype();
			
			int[] num = new int[count];
			for(int j=0;j<count;j++)//����һ�����0��Conut��ֵ�������������,ÿ��ֵ��ӦidStore�е�һ��ID
				num[j] = j ;
			
			//��ʼ��citys[i]����
			int temp = tspPointNum;
			for(int j=0;j<tspPointNum;j++) {				
				int r = (int)(Math.random()*temp);//����0��temp����������,������temp
				tspPoints[i].tspPoint[j] = num[r];
				num[r] = num[temp-1];//num[r]�Ѿ���������jλ���ˣ������ڸ����������λ��,��num[temp-1]λ�õ�ֵ��û�и��裬���ø�����num[r]
				temp--;//num[temp-1]�Ѿ�������num[r]���ˣ�����Ҫ��Num[temp-1]��
			}
		}
		initDistance();//��ʼ�������м�ľ���
	}
	
	public int getPointNum() {
		return tspPointNum;
	}

	/**
	 * ����ÿ����Ⱥÿ������������Ӧ�ȣ�ѡ����ʣ��������ʺ��Ƿ�ѡ��
	 */
	public void CalAll() {
		for (int i=0;i<popSize;i++) {
			tspPoints[i].fitness = 0;
			tspPoints[i].selectP = 0;
			tspPoints[i].exceptP = 0;
			tspPoints[i].isSelected = 0;
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
			while(tspPoints[best].isSelected<=1&&best<popSize-1){
				best++;
			}
				
			while(tspPoints[bad].isSelected!=0&&bad<popSize-1){
				bad++;
			}
				
			for (int i=0;i<tspPointNum;i++){
				tspPoints[bad].tspPoint[i] = tspPoints[best].tspPoint[i];//isSelected>1�Ļ����踲��isSelected=0�Ļ�����̭�������
			}
				
			
			tspPoints[best].isSelected--;//isSelectedԽ������������Ⱥ�л���Խ�ã��û��򸲸ǲ����һ�Σ�������isSelected��ֵ����
			tspPoints[bad].isSelected++;//���Ļ��򱻸��Ǻ󣬽���isSelected��1��˵�����Ѿ�����
							
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
		for (int i=0;i<tspPointNum;i++) {
			if (tspPoints[x].tspPoint[i] != tspPoints[y].tspPoint[i]) {
				dimension++;
			}
		}
		
		int diffItem = 0;
		double[] diff = new double[dimension];
		
		for (int i=0;i<tspPointNum;i++) {
			if (tspPoints[x].tspPoint[i] != tspPoints[y].tspPoint[i]) {
				diff[diffItem] = tspPoints[x].tspPoint[i];
				tspPoints[x].tspPoint[i] = -1;
				tspPoints[y].tspPoint[i] = -1;
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
			if(tspPoints[x].tspPoint[tempi] == -1) {
				tspPoints[x].tspPoint[tempi] = (int)diff[dimension-tempDimension];
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
			if(tspPoints[y].tspPoint[tempi] == -1) {
				tspPoints[y].tspPoint[tempi] = (int)diff[dimension-tempDimension];
				tempDimension--;
			}
			tempi++;
		}
			
	}
	
	/**
	 * @param individual ����
	 * @param dimension ά��
	 * @return �ѵ㼯�����ڽ��溯���Ľ���㣩 ��executeCrossover()������ʹ��
	 * ����x��ͬ������0��dimension����������д����temp[]��
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
				temp1 = (int)(Math.random()*(tspPointNum));
				temp2 = (int)(Math.random()*(tspPointNum));
				temp = tspPoints[i].tspPoint[temp1];
				tspPoints[i].tspPoint[temp1] = tspPoints[i].tspPoint[temp2];
				tspPoints[i].tspPoint[temp2] = temp;
			}
		}
	}

	/**
	 * ��ʼ��������֮��ľ���
	 */
	private void initDistance() {
		
		TspPoint iTspPoint = null;
		TspPoint jTspPoint = null;
		for (int i=0;i<tspPointNum;i++) {
			for (int j=0;j<tspPointNum;j++) {
				Iterator iterator = tspPointSetMin.iterator();
				while(iterator.hasNext()){
					TspPoint tspPoint = (TspPoint)(iterator.next());
					if(tspPoint.getID()==idStore[i])
						iTspPoint = tspPoint;
					
					if(tspPoint.getID()==idStore[j])
						jTspPoint = tspPoint;
				}

				double dx = iTspPoint.getXCoordinate()-jTspPoint.getXCoordinate();
				double dy = iTspPoint.getYCoordinate()-jTspPoint.getYCoordinate();
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
			for (int j=0;j<tspPointNum-1;j++){
				tspPoints[i].fitness += distance[tspPoints[i].tspPoint[j]][tspPoints[i].tspPoint[j+1]];
			}
			tspPoints[i].fitness += distance[tspPoints[i].tspPoint[0]][tspPoints[i].tspPoint[tspPointNum-1]];
		}
	}
		
	/**
	 * ����ѡ�����
	 * ��Ӧ��Խ��ѡ�����Խ��
	 */
	private void CalSelectP() {
		long sum = 0;
		for (int i=0;i<popSize;i++)
			sum += tspPoints[i].fitness;
		for (int i=0;i<popSize;i++)
			tspPoints[i].selectP = (double)tspPoints[i].fitness/sum;
	}
		
	/**
	 * ������������
	 */
	private void CalexceptP() {
		for (int i=0;i<popSize;i++)
			tspPoints[i].exceptP = (double)tspPoints[i].selectP*popSize;
	}
		
	/**
	 * ����ó��������Ƿ���ţ�������ѡ�񣬽�����һ��
	 */
	private void CalIsSelected() {
		int needSelect = popSize;
		for (int i=0;i<popSize;i++)
			//����С��1/popSize�Ļ���ѡ��,��ѡ��·�������С���ǲ��ֻ���
			//nodes[i].exceptP<1���൱��nodes[i].selectPС��1/popSize,��ô�û����ڸ���Ⱥ�н���
			if (tspPoints[i].exceptP<1) {
				tspPoints[i].isSelected++;
				needSelect--;
			}
		
		
		//�����ŵ���������������ѡ��ǰneedSelect�����ŵĻ��򣬽����ǵ�isSelected�ټ�1
		double[] temp = new double[popSize];
		for (int i=0;i<popSize;i++) {
			temp[i] = tspPoints[i].exceptP*10;
		}
		int j=0;
		while(needSelect!=0) {
			for (int i=0;i<popSize;i++) {
				if ((int)temp[i]==j) {
					tspPoints[i].isSelected++;
					needSelect--;
					if(needSelect==0)
						break;
				}
			}
			j++;
		}
	}
		
	/**
	 * @param x
	 * @return �ж�һ�����Ƿ��������ĺ���
	 */
	private boolean isSushu (int x) {
		if (x<2) return false;
		for (int i=2;i<=x/2;i++)
			if (x%i==0&&x!=2) return false;
		return true;
	}
		
	/**
	 * @param x ����
	 * @return x �����ֵ�Ƿ�ȫ����ȣ�������ʾx.length�������Ž����ͬ�����㷨����
	 * ��һ����Ⱥ�����л������Ӧ�ȶ�һ����ʱ�򣬴����Ѿ�����
	 */
	private boolean isSame(double[] x) {
		for (int i=0;i<x.length-1;i++)
			if (x[i]!=x[i+1])
				return false;
		return true;
	}
		
	/**
	 * ��ӡ��������ŵ�·������
	 */
	private ArrayList printBestRoute() {
		int[] cityEnd1 = new int[tspPointNum];
		CalAll();
		double temp = tspPoints[0].fitness;
		int index = 0;
		ArrayList<CollectionNode> cList = new ArrayList<CollectionNode>();
		ArrayList<Node> nList = new ArrayList<Node>();
		for (int i=1;i<popSize;i++) {
			if (tspPoints[i].fitness<temp) {
				temp = tspPoints[i].fitness;
				index = i;
			}
		}
		//System.out.println();
		//System.out.println("���·�������У�");
		
		for (int j=0;j<tspPointNum;j++) {
			cityEnd1[j] = idStore[tspPoints[index].tspPoint[j]];
		}
		for (int m=0;m<cityEnd1.length;m++) {
			
				Iterator iterator = tspPointSetMin.iterator();
				while(iterator.hasNext()){
					TspPoint tspPoint = (TspPoint)(iterator.next());
					if(tspPoint.getID()==cityEnd1[m]){
						if(tspPoint.getNode() != null){
							//System.out.print("Node:"+tspPoint.getNode().getNodeID()+" ");
							nList.add(tspPoint.getNode());
						} else if(tspPoint.getCNode() != null){
							//System.out.println("collectionNode:"+tspPoint.getCNode().getNodeID()+" ");
							cList.add(tspPoint.getCNode());
						}
					}
						
				}
		}
		if(nList.size() != 0 && cList.size() != 0){
			System.out.println("���޸�tspVersion2����");
		}
		//System.out.println();
		if(nList.size() != 0){
			return nList;
		} else {
			return cList;
		}
		
	}
		
	/**
	 * �㷨ִ��
	 */
	public ArrayList run() {
		double[] result = new double[range];    //result��ʼ��Ϊ���е����ֶ������
		for (int i=0;i<range;i++)
			result[i] = i;
		int index = 0;       //�����е�λ��
		int num = 1;         //��num��
		while(maxgens>0) {
			//System.out.println("������������������"+num+"������������������");
			CalAll();//��ʼ��������

			pad();//��̭���в�Ļ���
			
			crossover();//���н���仯
			mutate();//�ٽ��б���
			maxgens--;
			double temp = tspPoints[0].fitness;
			for (int i=1;i<popSize;i++)
				if (tspPoints[i].fitness<temp) {
					temp = tspPoints[i].fitness;
				}
			//System.out.println("���ŵĽ�"+temp);//�ڶ��ٴ����Ž�
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
	 * @param a ��ʼʱ��
	 * @param b ����ʱ��
	 */
	public void CalTime(Calendar a, Calendar b) {
		long x = b.getTimeInMillis()-a.getTimeInMillis();
		long y = x/1000;
		x = x-1000*y;
		//System.out.println("�㷨ִ��ʱ�䣺"+y+"."+x+"��");
	}
		
	/**
	 * �������
	 */
	public ArrayList startTsp() {
		Calendar a = Calendar.getInstance();    //��ʼʱ��
		//Tsp tsp = new Tsp();
		ArrayList cList = this.run();
		Calendar b = Calendar.getInstance();    //����ʱ��
		this.CalTime(a,b);
		return cList;//����tsp�Ľ��
	}	

}


