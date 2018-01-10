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
	 * @param soulNode - �߶εĶ˵�1
	 * @param desNode - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public DisplayLine(Node soulNode, Node desNode, float width,Color color){
		soulPoint = new Point(soulNode);
		desPoint = new Point(desNode);
		this.color = color;
		this.width = width;
	}
	
	/**
	 * Constructor
	 * @param soulPoint - �߶εĶ˵�1
	 * @param desPoint - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public DisplayLine(Point soulPoint, Point desPoint, float width,Color color) {
		soulPoint = new Point(soulPoint.getXCoordinate(), soulPoint.getYCoordinate());
		desPoint = new Point(desPoint.getXCoordinate(), desPoint.getYCoordinate());
		this.color = color;
		this.width = width;
	}
}
