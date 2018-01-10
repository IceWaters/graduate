/**
 * 显示模块，所有的显示功能都在这里实现
 * 显示有两种方式，一种是直接调用display中的方法显示，另一种是创建一个DisplayBuf的对象，然后在主函数中统一显示
 * 如果是仿真实验需求的显示，建议你们在自己的模块中创建一个diplay()的方法，并返回一个DisplayBuf的对象
 * 如果是自己debug的显示，则可以直接调用Display中的方法
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
 * 在一个窗口中显示结果
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
		this.setTitle("MDC网络"); // 设置窗体名字
		this.setSize(950, 950); // 设置面板区域
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置退出操作
		drawPoint(nodePool, 4, Color.BLACK);
		this.setVisible(true); // 设置面板可见
	}

	/**
	 * 划线
	 * 
	 * @param soulNode
	 *            - 线段的端点1
	 * @param desNode
	 *            - 线段的端点2
	 * @param i
	 *            - 线段的粗细
	 * @param color
	 *            - 线段的颜色
	 */
	public void drawLine(Node soulNode, Node desNode, int i, Color color) {
		this.add(new ShowLine(soulNode, desNode, i, color));
		this.setVisible(true);
	}

	/**
	 * 划线
	 * 
	 * @param soulPoint
	 *            - 线段的端点1
	 * @param desPoint
	 *            - 线段的端点2
	 * @param width
	 *            - 线段的粗细
	 * @param color
	 *            - 线段的颜色
	 */
	public void drawLine(Point soulPoint, Point desPoint, Float width, Color color) {
		this.add(new ShowLine(soulPoint, desPoint, width, color));
		this.setVisible(true);
	}

	/**
	 * 画路径
	 * 
	 * @param path
	 *            - 需要显示的路径（由路径经过的节点ID表示）
	 * @param nodePool
	 *            - 节点池
	 */
	public void drawLine(int[] path, NodePool nodePool, float width, Color color) {
		this.add(new ShowLine(path, nodePool, width, color));
		this.setVisible(true);
	}

	/**
	 * 画路径
	 * 
	 * @param path
	 *            - 需要显示的路径（由路径经过的节点表示）
	 * @param nodePool
	 *            - 节点池
	 */
	public void drawLine(ArrayList<Node> path, float width, Color color) {
		// TODO Auto-generated method stub
		this.add(new ShowLine(path, width, color));
		this.setVisible(true);
	}

	/**
	 * 在指定位置写字
	 * 
	 * @param str
	 *            - 字符串
	 * @param x
	 *            - x轴坐标
	 * @param y
	 *            - y轴坐标
	 */
	public void drawString(String str, int x, int y) {

		this.add(new DisplayString(str, x, y));
		this.setVisible(true);
	}

	/**
	 * 画点
	 * 
	 * @param nodeSet
	 *            - 需要显示的节点集合
	 * @param width
	 *            - 点的显示
	 * @param color
	 *            - 点的显示颜色
	 */
	public void drawPoint(Set<Node> nodeSet, float width, Color color) {
		this.add(new ShowPoint(nodeSet, width, color));
		this.setVisible(true);
	}

	/**
	 * 画点
	 * 
	 * @param point
	 *            - 需要显示的节点
	 * @param width
	 *            - 点的显示
	 * @param color
	 *            - 点的显示颜色
	 */
	public void drawPoint(Point point, float width, Color color) {
		this.add(new ShowPoint(point, width, color));
		this.setVisible(true);
	}

	/**
	 * 画点
	 * 
	 * @param pointSet
	 *            - 需要显示的节点集合
	 * @param width
	 *            - 点的显示
	 * @param color
	 *            - 点的显示颜色
	 */
	public void drawPoint(float width, Color color, Set<Point> pointSet) {
		this.add(new ShowPoint(width, color, pointSet));
		this.setVisible(true);
	}

	/**
	 * 画点
	 * 
	 * @param nodePool
	 *            - 节点池
	 * @param width
	 *            - 点的显示
	 * @param color
	 *            - 点的显示颜色
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