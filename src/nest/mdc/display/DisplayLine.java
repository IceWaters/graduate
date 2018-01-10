/**
 * 
 */
package nest.mdc.display;

import java.awt.Color;

import nest.mdc.network.Node;
import nest.mdc.network.Point;

/**
 * display lines
 * @author xiaoq
 * @version 1.0
 */
class DisplayLine{
	public Color color;
	public float width;
	public Point soulPoint;
	public Point desPoint;
	
	/**
	 * Constructor
	 * @param soulNode - 线段的端点1
	 * @param desNode - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public DisplayLine(Node soulNode, Node desNode, float width,Color color){
		soulPoint = new Point(soulNode);
		desPoint = new Point(desNode);
		this.color = color;
		this.width = width;
	}
	
	/**
	 * Constructor
	 * @param soulPoint - 线段的端点1
	 * @param desPoint - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public DisplayLine(Point soulPoint, Point desPoint, float width,Color color) {
		soulPoint = new Point(soulPoint.getXCoordinate(), soulPoint.getYCoordinate());
		desPoint = new Point(desPoint.getXCoordinate(), desPoint.getYCoordinate());
		this.color = color;
		this.width = width;
	}
}
