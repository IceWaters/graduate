package nest.mdc.algorithm;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;

/**
 * 对node点进行Tsp
 * @author 柯登科
 * version:1.0
 */
public class Tsp {
	public final static int TSPNUM = 15 ;     //进行tsp的节点个数
	
	Set nodeSetMin = null;
	int[] idStore;//保存进行tsp的Node的ID
	
	private int nodeNum;    //城市个数
	private int popSize = 50;                 //种群数量
	private int maxgens = 100000;              //迭代次数
	private double pxover = 0.8;              //交叉概率
	private double pmultation = 0.2;         //变异概率
	private double[][] distance ;  
	private int range = 500;                 //用于判断何时停止的数组区间
	private  NodePool nodePool;
	
	
	/*
	 * 基因类
	 */
	private class genotype {
		int[] node = new int[nodeNum];        //单个基因的城市序列，一个基因代表一种城市连接顺序
		long fitness;                         //该基因的适应度
		double selectP;                       //选择概率
		double exceptP;                       //期望概率
		int isSelected;                       //是否被选择
	}
	
	private genotype[] nodes = new genotype[popSize];//存放genotype类型的数组,每个nodes相当于一个个体
	
	/**
	 *  构造函数，初始化种群
	 *  定义了一个包含50条基因的种群，每条基因包含着随机的34个城市的排序
	 */
	public Tsp(NodePool nodePool) {
		this.nodePool = nodePool;
		nodeSetMin = nodePool.findNodeSetWithBattery(TSPNUM);    //得到进行tsp计算的节点集合，参数为集合大小
		nodeNum = nodeSetMin.size(); 
		distance = new double[nodeNum][nodeNum];
		idStore = new int[nodeNum];     //进行tsp计算的节点个数
		                      
		System.out.println("nodeNum="+nodeNum);
		int count = 0;
		
		Iterator iterator = nodeSetMin.iterator();
		
		while(iterator.hasNext()){
			Node node = (Node)(iterator.next());
			idStore[count++] = node.getNodeID();//保存各Node的ID

		}
		
		/*************调试用*******************/
//		System.out.print("初始路径");
//		for(int i =0;i < count;i++){
//			System.out.print(idStore[i]+" ");
//		}
		
		for (int i=0;i<popSize;i++) {
			nodes[i] = new genotype();
			
			int[] num = new int[count];
			for(int j=0;j<count;j++)//定义一个存放0到Conut数值依次排序的数组,每个值对应idStore中的一个ID
				num[j] = j ;
			
			//初始化citys[i]基因
			int temp = nodeNum;
			for(int j=0;j<nodeNum;j++) {				
				int r = (int)(Math.random()*temp);//产生0到temp间的随机整数,不包括temp
				nodes[i].node[j] = num[r];
				num[r] = num[temp-1];//num[r]已经赋予基因的j位置了，不能在赋予基因其他位置,而num[temp-1]位置的值还没有赋予，正好覆盖在num[r]
				temp--;//num[temp-1]已经保存在num[r]中了，不需要有Num[temp-1]了
			}
			
//			nodes[i].fitness = 0;//代表该条基因的适应度
//			nodes[i].selectP = 0;
//			nodes[i].exceptP = 0;
//			nodes[i].isSelected = 0;
		}
		initDistance();//初始化各城市间的距离
	}
	
	/**
	 * 计算每个种群每个基因个体的适应度，选择概率，期望概率和是否被选择
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
	 * 填充，将多选的填充到未选的个体当中
	 * 淘汰种群中差的基因
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
				nodes[bad].node[i] = nodes[best].node[i];//isSelected>1的基因赋予覆盖isSelected=0的基因，淘汰掉差基因
			}
				
			
			nodes[best].isSelected--;//isSelected越大代表在这个种群中基因越好，好基因覆盖差基因一次，将它的isSelected的值降低
			nodes[bad].isSelected++;//坏的基因被覆盖后，将其isSelected加1，说明其已经变优
			
							
			if (bad==(popSize-1))
				break;
		}

	}
	
	/**
	 * 交叉主体函数
	 * 选择20对基因交，即有40个基因参与交叉，交叉概率为0.8
	 */
	public void crossover() {
		int x;
		int y;
		int pop = (int)(popSize*pxover/2);
		while(pop>0) {
			x = (int)(Math.random()*popSize);
			y = (int)(Math.random()*popSize);
			executeCrossover(x,y);      //x,y两个体执行交叉
			pop--;
		}
	}
	
	/**
	 * 执行交叉函数
	 * @param个体x
	 * @param个体y
	 * 对个体x和个体y执行佳点集的交叉，从而产生下一代城市序列
	 * note:先将x,y基因中dimension个不同的部分置-1，再用gp()将dimension个不同的地方序列打乱赋值到-1的位置，产生新个体
	 */
	private void executeCrossover(int x,int y) {
		int dimension = 0;
		//计算出x,y个体中序列不同的个数
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
	 * 根据x不同，生成0到dimension的乱序的序列存放在temp[]中
	 * @param individual - 个体
	 * @param dimension - 维数
	 * @return temp - 佳点集（用于交叉函数的交叉点） 在executeCrossover()函数中使用
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
		Arrays.sort(templ);    //排序
		for (int i=0;i<dimension;i++)
			for (int j=0;j<dimension;j++)
				if(temp[j]==templ[i])
					temp[j] = i;
		return temp;
	}
	
	/**
	 * 变异
	 * 将种群中某一些个体基因的某一部分的值交换；
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
	 * 打印当前代数的所有城市序列，以及其相关的参数
	 */
	
	/**
	 * 初始化各城市之间的距离
	 */
	private void initDistance() {
		
		Node iNode = null;
		Node jNode = null;
		for (int i=0;i<nodeNum;i++) {
			for (int j=0;j<nodeNum;j++) {
				iNode = nodePool.getNodeWithID(idStore[i]);     //由节点ID得到该节点的引用
				jNode = nodePool.getNodeWithID(idStore[j]);

				double dx = iNode.getXCoordinate()-jNode.getXCoordinate();
				double dy = iNode.getYCoordinate()-jNode.getYCoordinate();
				distance[i][j] = Math.sqrt(dx*dx + dy*dy);//代表第i个城市到第j个城市的距离
			}
		}
	}
		
	/**
	 * 计算所有城市序列的适应度
	 * 该例子适应度用该条基因序列代表的城市排序的总路径表示
	 * 适应度越大，该基因越差
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
	 * 计算选择概率
	 * 适应度越大，选择概率越大
	 */
	private void CalSelectP() {
		long sum = 0;
		for (int i=0;i<popSize;i++)
			sum += nodes[i].fitness;
		for (int i=0;i<popSize;i++)
			nodes[i].selectP = (double)nodes[i].fitness/sum;
	}
		
	/**
	 * 计算期望概率
	 */
	private void CalexceptP() {
		for (int i=0;i<popSize;i++)
			nodes[i].exceptP = (double)nodes[i].selectP*popSize;
	}
		
	/**
	 * 计算该城市序列是否较优，较优则被选择，进入下一代
	 */
	private void CalIsSelected() {
		int needSelect = popSize;
		for (int i=0;i<popSize;i++)
			//概率小于1/popSize的基因被选中,即选出路径距离较小的那部分基因
			//nodes[i].exceptP<1即相当于nodes[i].selectP小于1/popSize,那么该基因在该种群中较优
			if (nodes[i].exceptP<1) {
				nodes[i].isSelected++;
				needSelect--;
			}
		
		
		//将较优的这批基因中再挑选出前needSelect个最优的基因，将它们的isSelected再加1
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
	 * @return - 判断一个数是否是素数的函数
	 */
	private boolean isSushu (int x) {
		if (x<2) return false;
		for (int i=2;i<=x/2;i++)
			if (x%i==0&&x!=2) return false;
		return true;
	}
		
	/**
	 * @param x - 数组
	 * @return x - 数组的值是否全部相等，相等则表示x.length代的最优结果相同，则算法结束
	 * 当一个种群中所有基因的适应度都一样的时候，代表已经最优
	 */
	private boolean isSame(long[] x) {
		for (int i=0;i<x.length-1;i++)
			if (x[i]!=x[i+1])
				return false;
		return true;
	}
		
	/**
	 * 打印任意代最优的路径序列
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
		System.out.println("最佳路径的序列：");

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
	 * 算法执行
	 */
	public int[] run() {
		long[] result = new long[range];    //result初始化为所有的数字都不相等
		for (int i=0;i<range;i++)
			result[i] = i;
		int index = 0;       //数组中的位置
		int num = 1;         //第num代
		while(maxgens>0) {
			System.out.println("————————第"+num+"代————————");
			CalAll();//初始化各基因

			pad();//淘汰其中差的基因
			
			crossover();//进行交叉变化
			mutate();//再进行变异
			maxgens--;
			long temp = nodes[0].fitness;
			for (int i=1;i<popSize;i++)
				if (nodes[i].fitness<temp) {
					temp = nodes[i].fitness;
				}
			System.out.println("最优的解"+temp);//第多少代最优解
			result[index] = temp;
			if (isSame(result))//当连续range代的最优解的适应度相同的时候，代表已经最优
				break;
			index++;
			if (index==range)
				index = 0;
			num++;
		}
		return printBestRoute();
	}
		
	/**
	 * @param a - 开始时间
	 * @param b - 结束时间
	 */
	public void CalTime(Calendar a, Calendar b) {
		long x = b.getTimeInMillis()-a.getTimeInMillis();
		long y = x/1000;
		x = x-1000*y;
		System.out.println("算法执行时间："+y+"."+x+"秒");
	}
		
	/**
	 * 程序入口
	 */
//	public void startTsp() {
//		Calendar a = Calendar.getInstance();    //开始时间
//		//Tsp tsp = new Tsp();
//		this.run();
//		Calendar b = Calendar.getInstance();    //结束时间
//		this.CalTime(a,b);
//	}	

}

