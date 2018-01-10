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
	 * @param point - ��
	 * @param width - �����ʾ��С
	 * @param color - �����ʾ��ɫ
	 */
	public DisplayPoint(Point point, float width,Color color){
		// TODO Auto-generated constructor stub
		this.color = color;
		this.point = new Point(point.getXCoordinate(), point.getYCoordinate());
		this.width = width;
	}
	
	/**
	 * Constructor
	 * @param node - ��
	 * @param width - �����ʾ��С
	 * @param color - �����ʾ��ɫ
	 */
	public DisplayPoint(Node node, float width,Color color){
		// TODO Auto-generated constructor stub
		this.color = color;
		this.point = new Point(node);
		this.width = width;
	}
}
