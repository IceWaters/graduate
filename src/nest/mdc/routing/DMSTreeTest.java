package nest.mdc.routing;

import java.util.Set;

import nest.mdc.network.Node;

/**
 * ������С����ͼ����Ȩֵ
 * @author Ϳ����
 * @version 1.0
 */
public class DMSTreeTest {
	
	private double[] In;
	private int[] pre,hash1,vis;
	private edge[] e;   //�洢��
	private final double INF = 9999;	//�����룬��ʾ����ֱ��
	private final int nodeNum;	//�ڵ���
	
	/**
	 * Constructor
	 * @param nodeSet - �ڵ㼯��
	 */
	DMSTreeTest(Set<Node> nodeSet){
		nodeNum = nodeSet.size();
		In = new double[nodeNum + 10];
		pre = new int[nodeNum + 10];
		hash1 = new int[nodeNum + 10];
		vis = new int[nodeNum + 10];
		e = new edge[nodeNum*nodeNum];
		intialMap(nodeSet);
		double weight = Directed_MST(0, nodeNum, nodeNum * nodeNum);
		System.out.println("The whole weight is : " + weight);
	}
	
	/**
	 * ��ʼ��ͼ�Σ����ڵ����ڵ�ıߴ洢��edge[]��
	 * @param nodeSet - �ڵ㼯��
	 */
	private void intialMap(Set<Node> nodeSet){	
		int count = 0;		
		for(Node node : nodeSet){
			int desNodeID = node.getNodeID();
			Set<Node> neighbors = node.neighbors.get(1).getNeig();//���εõ��ڵ���ھӽڵ�
			for(Node soulNode : neighbors){
				int soulNodeID = soulNode.getNodeID(); //�õ�Դ�ڵ�i��ID
				double weight = getWeight(soulNode, node);	
				e[count++] =new edge(soulNodeID, desNodeID, weight);
			}
			for(Node node2 : nodeSet){
				if(!neighbors.contains(node2))
					e[count++] =new edge(node2.getNodeID(), desNodeID, INF);
			}
		}
	//	System.out.println("The count is : " + count);
	}
	
	/**
	 * ��ȡ�ߵ�Ȩ�أ�Ȩ��W = desNode.getRemainingBattery * soulNode.getDistance(desNode)
	 * @param souNode - ��ʶ�ڵ�
	 * @param desNode - Ŀ�Ľڵ�
	 * @return weight - �����ڵ�ıߵ�Ȩ��
	 */
	private double getWeight(Node souNode, Node desNode){
		double weight = desNode.getRemainingBattery() * souNode.getDistance(desNode);
		return weight;
	}
	
	/**
	 * ������С����ͼ����������Ȩֵ
	 * @param root - ���ڵ�
	 * @param n - �ڵ���Ŀ
	 * @param m - �ߵ������Ŀ
	 * @return ret - ��С����������Ȩֵ
	 */
	private double Directed_MST(int root,int n,int m)
	{
	    double ret=0;
	    while(true)
	    {
	    //	System.out.println("The count is : " + count++);
	        for(int i=0;i<n;i++)
	            In[i] = INF;
	        for(int i=0;i<m;i++)//����С���
	        {
	            int u=e[i].u;
	            int v=e[i].v;
	            if(e[i].cost<In[v] && u!=v){
	                pre[v]=u;
	                In[v]=e[i].cost;
	            }
	        }
	        for(int i=0;i<n;i++)
	        {
	            if(i==root)
	                continue;
	            if(In[i]==INF)
	                return -1;
	        }
	        int cntnode=0;
	        for(int i = 0; i < hash1.length; i++){
	        	hash1[i] = -1;
	        }
	        for(int i = 0; i < vis.length; i++){
	        	vis[i] = -1;
	        }	       
	        In[root]=0;
	        for(int i=0;i<n;i++)//�һ�
	        {
	            ret+=In[i];
	            int v=i;
	            while(vis[v]!=i && hash1[v]==-1 && v!=root)
	            {
	                vis[v]=i;
	                v=pre[v];
	            }
	            if(v!=root && hash1[v]==-1)
	            {
	                for(int u=pre[v];u!=v;u=pre[u])
	                    hash1[u]=cntnode;
	                hash1[v]=cntnode++;
	            }
	        }
	        if(cntnode==0)
	            break;
	        for(int i=0;i<n;i++)
	            if(hash1[i]==-1)
	                hash1[i]=cntnode++;
	        for(int i=0;i<m;i++)//�ر��
	        {
	            int v=e[i].v;
	            e[i].u=hash1[e[i].u];
	            e[i].v=hash1[e[i].v];
	            if(e[i].u!=e[i].v)
	                e[i].cost-=In[v];
	        }
	        n=cntnode;
	        root=hash1[root];
	    }
	    return ret;
	}
	
}

/**
 * ��С����ͼ�еı�
 * @author Ϳ����
 * @version 1.0
 */
class edge{
	public int u, v;
	public double cost;
	
	/**
	 *Constructor
	 * @param start - ���
	 * @param end - �յ�
	 * @param weight - Ȩֵ
	 */
	edge(int start, int end, double weight){
		this.u = start;
		this.v = end;
		this.cost = weight;
	}		
}

