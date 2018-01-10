/**
 * 
 */
package nest.mdc.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

/**
 * ������ʾԲȦ�����
 * @author xiaoq
 * @version 1.0
 */
class ShowCirlce extends JPanel{
	private Set<DisplayPoint> pointSet;
	
	/**
	 * Constructor
	 * @param nodePool - �ڵ��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	ShowCirlce(NodePool nodePool, float width, Color color){
		pointSet = new HashSet<>();
		Set<Node> nodeSet = nodePool.getNodeSet();
		for(Node node : nodeSet){
			DisplayPoint point = new DisplayPoint(node, width, color);
			pointSet.add(point);
		}
	}
	
	/**
	 * Constructor
	 * @param node - ��Ҫ��ʾ�Ľڵ�
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	ShowCirlce(Node node, float width, Color color){
		pointSet = new HashSet<>();
		pointSet.add(new DisplayPoint(node, width, color));
	}
	
	/**
	 * Constructor
	 * @param nodeSet - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	ShowCirlce(Set<Node> nodeSet, float width, Color color){
		pointSet = new HashSet<>();
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * Constructor
	 * @param point - ��Ҫ��ʾ�Ľڵ�
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	ShowCirlce(Point point, float width, Color color){
		pointSet = new HashSet<>();
		pointSet.add(new DisplayPoint(point, width, color));
	}
	
	/**
	 * Constructor
	 * @param pointSet - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width - �����ʾ
	 * @param color - �����ʾ��ɫ
	 */
	ShowCirlce(float width, Color color, Set<Point> pointSet){
		pointSet = new HashSet<>();
		for(Point point : pointSet){
			this.pointSet.add(new DisplayPoint(point, width, color));
		}
	}
	
	/**
	 * Constructor
	 * @param lineSet - ����ʾ�ĵ�ļ���
	 */
	ShowCirlce(Set<DisplayPoint> pointSet){
		this.pointSet = new HashSet<>();
		this.pointSet.addAll(pointSet);
		//this.setOpaque(false);
	}
	
	/*��д����ķ����������Լ��Ļ���*/
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;	//��Grqaphicsǿ��ת��ΪGraphics2D����
		//g2.setColor(Color.WHITE);	//���ñ�����ɫ
		
		//g2.fillRect(0, 0, 500, 500);	//���ñ�����С
		
		for(DisplayPoint point : pointSet){
			g2.setColor(point.color);
			g2.setStroke(new BasicStroke(point.width));	//���û��ʴ�С
			Line2D.Double line1 = new Line2D.Double(point.point.getXCoordinate(),
					point.point.getYCoordinate(), point.point.getXCoordinate(), point.point.getYCoordinate());
			g2.draw(line1);
		}
	}
}
