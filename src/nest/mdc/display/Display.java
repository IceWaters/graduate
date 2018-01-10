/**
 * ��ʾģ�飬���е���ʾ���ܶ�������ʵ��
 * ��ʾ�����ַ�ʽ��һ����ֱ�ӵ���display�еķ�����ʾ����һ���Ǵ���һ��DisplayBuf�Ķ���Ȼ������������ͳһ��ʾ
 * ����Ƿ���ʵ���������ʾ�������������Լ���ģ���д���һ��diplay()�ķ�����������һ��DisplayBuf�Ķ���
 * ������Լ�debug����ʾ�������ֱ�ӵ���Display�еķ���
 */
package nest.mdc.display;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JFrame;

import nest.mdc.landform.Landform;
import nest.mdc.network.Node;
import nest.mdc.network.NodePool;
import nest.mdc.network.Point;

/**
 * ��һ����������ʾ���
 * 
 * @author xiaoq
 * @version 1.0
 */
public class Display extends JFrame {

	/**    
	 * 
	 */
	public Display(NodePool nodePool) {
		// TODO Auto-generated constructor stub
		// this.add(this.showNode(nodePool));
		this.setTitle("MDC����"); // ���ô�������
		this.setSize(950, 950); // �����������
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // �����˳�����
		drawPoint(nodePool, 4, Color.BLACK);
		this.setVisible(true); // �������ɼ�
	}

	/**
	 * ����
	 * 
	 * @param soulNode
	 *            - �߶εĶ˵�1
	 * @param desNode
	 *            - �߶εĶ˵�2
	 * @param i
	 *            - �߶εĴ�ϸ
	 * @param color
	 *            - �߶ε���ɫ
	 */
	public void drawLine(Node soulNode, Node desNode, int i, Color color) {
		this.add(new ShowLine(soulNode, desNode, i, color));
		this.setVisible(true);
	}

	/**
	 * ����
	 * 
	 * @param soulPoint
	 *            - �߶εĶ˵�1
	 * @param desPoint
	 *            - �߶εĶ˵�2
	 * @param width
	 *            - �߶εĴ�ϸ
	 * @param color
	 *            - �߶ε���ɫ
	 */
	public void drawLine(Point soulPoint, Point desPoint, Float width, Color color) {
		this.add(new ShowLine(soulPoint, desPoint, width, color));
		this.setVisible(true);
	}

	/**
	 * ��·��
	 * 
	 * @param path
	 *            - ��Ҫ��ʾ��·������·�������Ľڵ�ID��ʾ��
	 * @param nodePool
	 *            - �ڵ��
	 */
	public void drawLine(int[] path, NodePool nodePool, float width, Color color) {
		this.add(new ShowLine(path, nodePool, width, color));
		this.setVisible(true);
	}

	/**
	 * ��·��
	 * 
	 * @param path
	 *            - ��Ҫ��ʾ��·������·�������Ľڵ��ʾ��
	 * @param nodePool
	 *            - �ڵ��
	 */
	public void drawLine(ArrayList<Node> path, float width, Color color) {
		// TODO Auto-generated method stub
		this.add(new ShowLine(path, width, color));
		this.setVisible(true);
	}

	/**
	 * ��ָ��λ��д��
	 * 
	 * @param str
	 *            - �ַ���
	 * @param x
	 *            - x������
	 * @param y
	 *            - y������
	 */
	public void drawString(String str, int x, int y) {

		this.add(new DisplayString(str, x, y));
		this.setVisible(true);
	}

	/**
	 * ����
	 * 
	 * @param nodeSet
	 *            - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width
	 *            - �����ʾ
	 * @param color
	 *            - �����ʾ��ɫ
	 */
	public void drawPoint(Set<Node> nodeSet, float width, Color color) {
		this.add(new ShowPoint(nodeSet, width, color));
		this.setVisible(true);
	}

	/**
	 * ����
	 * 
	 * @param point
	 *            - ��Ҫ��ʾ�Ľڵ�
	 * @param width
	 *            - �����ʾ
	 * @param color
	 *            - �����ʾ��ɫ
	 */
	public void drawPoint(Point point, float width, Color color) {
		this.add(new ShowPoint(point, width, color));
		this.setVisible(true);
	}

	/**
	 * ����
	 * 
	 * @param pointSet
	 *            - ��Ҫ��ʾ�Ľڵ㼯��
	 * @param width
	 *            - �����ʾ
	 * @param color
	 *            - �����ʾ��ɫ
	 */
	public void drawPoint(float width, Color color, Set<Point> pointSet) {
		this.add(new ShowPoint(width, color, pointSet));
		this.setVisible(true);
	}

	/**
	 * ����
	 * 
	 * @param nodePool
	 *            - �ڵ��
	 * @param width
	 *            - �����ʾ
	 * @param color
	 *            - �����ʾ��ɫ
	 */
	public void drawPoint(NodePool nodePool, float width, Color color) {
		ShowPoint showPoint = new ShowPoint(nodePool, width, color);
		this.add(showPoint);
		this.setVisible(true);
	}

	public void drawLandform(int rows, int cols, Landform[][] landformArray) {
		this.add(new ShowLandform(rows, cols, landformArray));
		this.setVisible(true);
	}

	public void draw(DisplayBuf buf) {
		ShowLine showLine = new ShowLine(buf.getDisplayLineSet());
		this.add(showLine);
		this.setVisible(true);
		this.add(new ShowPoint(buf.getDisplayPointSet()));
		this.setVisible(true);
		showLine.setVisible(true);
	}

	public void clearPicture() {
		this.add(new ClearPicture());
		this.setVisible(true);
	}
}