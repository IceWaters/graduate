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
 * ���߶�
 * @author xiaoq
 * @version 1.0
 */
public class ShowLine extends JPanel {
	private Set<DisplayLine> lineSet;

	/**
	 * Constructor
	 * @param soulNode - �߶εĶ˵�1
	 * @param desNode - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public ShowLine(Node soulNode, Node desNode, float width, Color color) {
		lineSet = new HashSet<>();
		lineSet.add(new DisplayLine(soulNode, desNode, width, color));
	}
	
	/**
	 * Constructor
	 * @param soulPoint - �߶εĶ˵�1
	 * @param desPoint - �߶εĶ˵�2
	 * @param width - �߶εĴ�ϸ
	 * @param color - �߶ε���ɫ
	 */
	public ShowLine(Point soulPoint, Point desPoint, float width, Color color) {
		lineSet = new HashSet<>();
		lineSet.add(new DisplayLine(soulPoint, desPoint, width, color));
	}
	
	/**
	 * Constructor
	 * @param path - ��Ҫ��ʾ��·������·�������Ľڵ�ID��ʾ��
	 * @param nodePool - �ڵ��
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
	 * @param path - ��Ҫ��ʾ��·������·�������Ľڵ��ʾ��
	 * @param nodePool - �ڵ��
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
	 * @param lineSet - ����ʾ���ߵļ���
	 */
	public ShowLine(Set<DisplayLine> lineSet) {
		this.lineSet = new HashSet<>();
		this.lineSet.addAll(lineSet);
	}

	public void paintComponent(Graphics g){
		/*ע�͵���伴����ԭͼ�����ϻ�ͼ*/
		//super.paintComponent(g);   
		Graphics2D g2 = (Graphics2D)g;	//��Grqaphicsǿ��ת��ΪGraphics2D����
		//g2.fillRect(0, 0, 500, 500);	//���ñ�����С
		for(DisplayLine line : lineSet){
			g2.setColor(line.color);
			Line2D.Double line1 =  new Line2D.Double(line.soulPoint.getXCoordinate(),
					line.soulPoint.getYCoordinate(), line.desPoint.getXCoordinate(), line.desPoint.getYCoordinate());
			g2.setStroke(new BasicStroke(line.width));	//���û��ʴ�С
			g2.draw(line1);
		}
	}
}
