/**
 * 显示信息缓存，用于承载需要显示的信息
 */
package nest.mdc.display;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

/**
 * 显示缓存，保存需要显示的信息
 * @author xiaoq
 *
 */
public class DisplayBuf {
	private Set<DisplayPoint> pointSet = new HashSet<>();
	private Set<DisplayLine> lineSet = new HashSet<>();

	/**
	 * Constructor
	 */
	public DisplayBuf() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 划线
	 * @param soulNode - 线段的端点1
	 * @param desNode - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public void drawLine(Node soulNode, Node desNode, Float width, Color color) {
		lineSet.add(new DisplayLine(soulNode, desNode, width, color));
	}
	
	/**
	 * 划线
	 * @param soulPoint - 线段的端点1
	 * @param desPoint - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public void drawLine(Point soulPoint, Point desPoint, Float width, Color color) {
		lineSet.add(new DisplayLine(soulPoint, desPoint, width, color));
	}
	
	/**
	 * 画路径
	 * @param path - 需要显示的路径（由路径经过的节点ID表示）
	 * @param nodePool - 节点池
	 */
	public void drawPath(int[] path, NodePool nodePool, float width, Color color){
		for(int i = 1; i < path.length; i++){
			Node soulNode = nodePool.getNodeWithID(path[i - 1]);
			Node desNode = nodePool.getNodeWithID(path[i]);
			lineSet.add(new DisplayLine(soulNode, desNode, width, color));
		}
		System.out.println("路径跳数为：" + lineSet.size());
	}
	
	/**
	 * 画路径
	 * @param path - 需要显示的路径（由路径经过的节点表示）
	 * @param nodePool - 节点池
	 */
	public void drawPath(ArrayList<Node> path, float width, Color color){
		Iterator<Node> it = path.iterator();
		Iterator<Node> it_pre = path.iterator();
		if (it.hasNext()) {
			it.next();
			while(it.hasNext() && it_pre.hasNext()){
				Node desNode = (Node)it.next();
				Node soulNode = (Node)it_pre.next();
				lineSet.add(new DisplayLine(soulNode, desNode, width, color));
			}
		}
	}
	
	/**
	 * 画点
	 * @param nodeSet - 需要显示的节点集合
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	public void drawPoint(Set<Node> nodeSet, float width, Color color){
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * 画点
	 * @param point - 需要显示的节点
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	public void drawPoint(Point point, float width, Color color){
		pointSet.add(new DisplayPoint(point, width, color));
	}
	
	/**
	 * 画点
	 * @param pointSet - 需要显示的节点集合
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	public void drawPoint(float width, Color color, Set<Point> pointSet){
		for(Point point : pointSet){
			this.pointSet.add(new DisplayPoint(point, width, color));
		}
	}
	
	/**
	 * 画点
	 * @param nodePool - 节点池
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	public void drawPoint(NodePool nodePool, float width, Color color){
		Set<Node> nodeSet = nodePool.getNodeSet();
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * 获取显示信息
	 * @return pointSet - 待显示点的集合
	 */
	public Set<DisplayPoint> getDisplayPointSet() {
		return pointSet;
	}
	
	/**
	 * 获取显示信息
	 * @return lineSet - 待显示线的集合
	 */
	public Set<DisplayLine> getDisplayLineSet() {
		return lineSet;
	}

	
}
