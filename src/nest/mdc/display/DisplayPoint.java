/**
 * 
 */
package nest.mdc.display;

import java.awt.Color;

import nest.mdc.network.Node;
import nest.mdc.network.Point;

/**
 * display point
 * @author xiaoq
 * @version 1.0
 */
class DisplayPoint{
	public Point point;
	public float width;
	public Color color;
	
	/**
	 * Constructor
	 * @param point - 点
	 * @param width - 点的显示大小
	 * @param color - 点的显示颜色
	 */
	public DisplayPoint(Point point, float width,Color color){
		// TODO Auto-generated constructor stub
		this.color = color;
		this.point = new Point(point.getXCoordinate(), point.getYCoordinate());
		this.width = width;
	}
	
	/**
	 * Constructor
	 * @param node - 点
	 * @param width - 点的显示大小
	 * @param color - 点的显示颜色
	 */
	public DisplayPoint(Node node, float width,Color color){
		// TODO Auto-generated constructor stub
		this.color = color;
		this.point = new Point(node);
		this.width = width;
	}
}
