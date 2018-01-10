/**
 * 
 */
package nest.mdc.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;

import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

/**
 * 画线段
 * @author xiaoq
 * @version 1.0
 */
public class ShowLine extends JPanel {
	private Set<DisplayLine> lineSet;

	/**
	 * Constructor
	 * @param soulNode - 线段的端点1
	 * @param desNode - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public ShowLine(Node soulNode, Node desNode, float width, Color color) {
		lineSet = new HashSet<>();
		lineSet.add(new DisplayLine(soulNode, desNode, width, color));
	}
	
	/**
	 * Constructor
	 * @param soulPoint - 线段的端点1
	 * @param desPoint - 线段的端点2
	 * @param width - 线段的粗细
	 * @param color - 线段的颜色
	 */
	public ShowLine(Point soulPoint, Point desPoint, float width, Color color) {
		lineSet = new HashSet<>();
		lineSet.add(new DisplayLine(soulPoint, desPoint, width, color));
	}
	
	/**
	 * Constructor
	 * @param path - 需要显示的路径（由路径经过的节点ID表示）
	 * @param nodePool - 节点池
	 */
	public ShowLine(int[] path, NodePool nodePool, float width, Color color){
		lineSet = new HashSet<>();
		DisplayLine line;
		for(int i = 0;i < path.length - 1;i++){
			line = new DisplayLine(nodePool.getNodeWithID(path[i]), nodePool.getNodeWithID(path[i + 1]), width, color);
			lineSet.add(line);
		}
	}
	
	/**
	 * Constructor
	 * @param path - 需要显示的路径（由路径经过的节点表示）
	 * @param nodePool - 节点池
	 */
	public ShowLine(ArrayList<Node> path, float width, Color color) {
		// TODO Auto-generated constructor stub
		lineSet = new HashSet<>();
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
	 * Constructor
	 * @param lineSet - 待显示的线的集合
	 */
	public ShowLine(Set<DisplayLine> lineSet) {
		this.lineSet = new HashSet<>();
		this.lineSet.addAll(lineSet);
	}

	public void paintComponent(Graphics g){
		/*注释掉这句即可在原图基础上画图*/
		//super.paintComponent(g);   
		Graphics2D g2 = (Graphics2D)g;	//将Grqaphics强制转化为Graphics2D类型
		//g2.fillRect(0, 0, 500, 500);	//设置背景大小
		for(DisplayLine line : lineSet){
			g2.setColor(line.color);
			Line2D.Double line1 =  new Line2D.Double(line.soulPoint.getXCoordinate(),
					line.soulPoint.getYCoordinate(), line.desPoint.getXCoordinate(), line.desPoint.getYCoordinate());
			g2.setStroke(new BasicStroke(line.width));	//设置画笔大小
			g2.draw(line1);
		}
	}
}
