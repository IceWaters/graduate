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
 * 用于显示圆圈的组件
 * @author xiaoq
 * @version 1.0
 */
class ShowCirlce extends JPanel{
	private Set<DisplayPoint> pointSet;
	
	/**
	 * Constructor
	 * @param nodePool - 节点池
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
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
	 * @param node - 需要显示的节点
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	ShowCirlce(Node node, float width, Color color){
		pointSet = new HashSet<>();
		pointSet.add(new DisplayPoint(node, width, color));
	}
	
	/**
	 * Constructor
	 * @param nodeSet - 需要显示的节点集合
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	ShowCirlce(Set<Node> nodeSet, float width, Color color){
		pointSet = new HashSet<>();
		for(Node node : nodeSet){
			pointSet.add(new DisplayPoint(node, width, color));
		}
	}
	
	/**
	 * Constructor
	 * @param point - 需要显示的节点
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	ShowCirlce(Point point, float width, Color color){
		pointSet = new HashSet<>();
		pointSet.add(new DisplayPoint(point, width, color));
	}
	
	/**
	 * Constructor
	 * @param pointSet - 需要显示的节点集合
	 * @param width - 点的显示
	 * @param color - 点的显示颜色
	 */
	ShowCirlce(float width, Color color, Set<Point> pointSet){
		pointSet = new HashSet<>();
		for(Point point : pointSet){
			this.pointSet.add(new DisplayPoint(point, width, color));
		}
	}
	
	/**
	 * Constructor
	 * @param lineSet - 待显示的点的集合
	 */
	ShowCirlce(Set<DisplayPoint> pointSet){
		this.pointSet = new HashSet<>();
		this.pointSet.addAll(pointSet);
		//this.setOpaque(false);
	}
	
	/*重写父类的方法，定义自己的画法*/
	public void paintComponent(Graphics g){
		//super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;	//将Grqaphics强制转化为Graphics2D类型
		//g2.setColor(Color.WHITE);	//设置背景颜色
		
		//g2.fillRect(0, 0, 500, 500);	//设置背景大小
		
		for(DisplayPoint point : pointSet){
			g2.setColor(point.color);
			g2.setStroke(new BasicStroke(point.width));	//设置画笔大小
			Line2D.Double line1 = new Line2D.Double(point.point.getXCoordinate(),
					point.point.getYCoordinate(), point.point.getXCoordinate(), point.point.getYCoordinate());
			g2.draw(line1);
		}
	}
}
