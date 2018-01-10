/**
 * ��ʾ��Ϣ���棬���ڳ�����Ҫ��ʾ����Ϣ
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
 * ��ʾ���棬������Ҫ��ʾ����Ϣ
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
	 * ����
	 * @param soulNode - �߶εĶ˵�1
	 * @param desNode - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public void drawLine(Node soulNode, Node desNode, Float width, Color color) {
		lineSet.add(new DisplayLine(soulNode, desNode, width, color));
	}
	
	/**
	 * ����
	 * @param soulPoint - �߶εĶ˵�1
	 * @param desPoint - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public void drawLine(Point soulPoint, Point desPoint, Float width, Color color) {
		lineSet.add(new DisplayLine(soulPoint, desPoint, width, color));
	}
	
	/**
	 * ��·��
	 * @param path - ��Ҫ��ʾ��·������·�������Ľڵ�ID��ʾ��
	 * @param nodePool - �ڵ��
	 */
	public void drawPath(int[] path, NodePool nodePool, float width, Color color){
		for(int i = 1; i < path.length; i++){
			Node soulNode = nodePool.getNodeWithID(path[i - 1]);
			Node desNode = nodePool.getNodeWithID(path[i]);
			lineSet.add(new DisplayLine(soulNode, desNode, width, color));
		}
		System.out.println("·������Ϊ��" + lineSet.size());
	}
	
	/**
	 * ��·��
	 * @param path - ��Ҫ��ʾ��·������·�������Ľڵ��ʾ��
	 * @param nodePool - �ڵ��
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
	 * ����
	 * @param nodeSet - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	public void drawPoint(Set<Node> nodeSet, float width, Color color){
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * ����
	 * @param point - ��Ҫ��ʾ�Ľڵ�
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	public void drawPoint(Point point, float width, Color color){
		pointSet.add(new DisplayPoint(point, width, color));
	}
	
	/**
	 * ����
	 * @param pointSet - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	public void drawPoint(float width, Color color, Set<Point> pointSet){
		for(Point point : pointSet){
			this.pointSet.add(new DisplayPoint(point, width, color));
		}
	}
	
	/**
	 * ����
	 * @param nodePool - �ڵ��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	public void drawPoint(NodePool nodePool, float width, Color color){
		Set<Node> nodeSet = nodePool.getNodeSet();
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * ��ȡ��ʾ��Ϣ
	 * @return pointSet - ����ʾ��ļ���
	 */
	public Set<DisplayPoint> getDisplayPointSet() {
		return pointSet;
	}
	
	/**
	 * ��ȡ��ʾ��Ϣ
	 * @return lineSet - ����ʾ�ߵļ���
	 */
	public Set<DisplayLine> getDisplayLineSet() {
		return lineSet;
	}

	
}
