package nest.mdc.routing;

import java.util.Set;

import nest.mdc.network.Node;

/**
 * 计算最小树形图的总权值
 * @author 涂方蕾
 * @version 1.0
 */
public class DMSTreeTest {
	
	private double[] In;
	private int[] pre,hash1,vis;
	private edge[] e;   //存储边
	private final double INF = 9999;	//最大距离，表示不可直达
	private final int nodeNum;	//节点数
	
	/**
	 * Constructor
	 * @param nodeSet - 节点集合
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
	 * 初始化图形，将节点与邻点的边存储在edge[]中
	 * @param nodeSet - 节点集合
	 */
	private void intialMap(Set<Node> nodeSet){	
		int count = 0;		
		for(Node node : nodeSet){
			int desNodeID = node.getNodeID();
			Set<Node> neighbors = node.neighbors.get(1).getNeig();//依次得到节点的邻居节点
			for(Node soulNode : neighbors){
				int soulNodeID = soulNode.getNodeID(); //得到源节点i的ID
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
	 * 获取边的权重，权重W = desNode.getRemainingBattery * soulNode.getDistance(desNode)
	 * @param souNode - 初识节点
	 * @param desNode - 目的节点
	 * @return weight - 两个节点的边的权重
	 */
	private double getWeight(Node souNode, Node desNode){
		double weight = desNode.getRemainingBattery() * souNode.getDistance(desNode);
		return weight;
	}
	
	/**
	 * 生成最小树形图，并计算总权值
	 * @param root - 根节点
	 * @param n - 节点数目
	 * @param m - 边的最大数目
	 * @return ret - 最小生成树的总权值
	 */
	private double Directed_MST(int root,int n,int m)
	{
	    double ret=0;
	    while(true)
	    {
	    //	System.out.println("The count is : " + count++);
	        for(int i=0;i<n;i++)
	            In[i] = INF;
	        for(int i=0;i<m;i++)//找最小入边
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
	        for(int i=0;i<n;i++)//找环
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
	        for(int i=0;i<m;i++)//重标记
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
 * 最小树形图中的边
 * @author 涂方蕾
 * @version 1.0
 */
class edge{
	public int u, v;
	public double cost;
	
	/**
	 *Constructor
	 * @param start - 起点
	 * @param end - 终点
	 * @param weight - 权值
	 */
	edge(int start, int end, double weight){
		this.u = start;
		this.v = end;
		this.cost = weight;
	}		
}

