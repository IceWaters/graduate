package nest.mdc.landform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.JPanel;

/**
 * 枚举地形图中的地形
 * 
 * @author 涂方蕾
 * @version 1.0
 */
public enum Landform {
	MOUNTAIN, LAKE, FLAT, ROAD, HARD, ROAD2
}

/**
 * 地形图中的点元素
 * 
 * @author 涂方蕾
 * @version 1.0
 */
class Dot {

	private int x, y;// 点的位置
	private int weight, degree;
	private Dot parDot;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            - 点的x轴坐标位置
	 * @param y
	 *            - 点的y轴坐标位置
	 * @param weight
	 *            - 从初始点经由节点n到目标点的估价权值
	 * @param degree
	 *            - 从初识状态到该点的实际代价
	 * @param parDot
	 *            - 该点的父节点
	 */
	public Dot(int x, int y, int weight, int degree, Dot parDot) {
		this.x = x;
		this.y = y;
		this.parDot = parDot;
		this.weight = weight;
		this.degree = degree;
	}

	/**
	 * 得到点的x轴坐标
	 * 
	 * @return x - x轴坐标
	 */
	public int getX() {
		return x;
	}

	/**
	 * 得到点的y轴坐标
	 * 
	 * @return y - y轴坐标
	 */
	public int getY() {
		return y;
	}

	/**
	 * 得到该点的权值
	 * 
	 * @return weight - 从初始点经由节点n到目标点的估价权值
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * 得到该点的从初识状态到该点的实际代价
	 * 
	 * @return degree - 该点的从初识状态到该点的实际代价
	 */
	public int getDegree() {
		return degree;
	}

	/**
	 * 得到该点的父节点
	 * 
	 * @return parDot - 该点的父节点
	 */
	public Dot getParDot() {
		return parDot;
	}
}

/**
 * 设置地形图，并用A*算法找出两个节点之间的最短距离
 * 
 * @author 涂方蕾
 * @version 1.0
 */
class Area {
	private int ROW;
	private int COL;
	private final static int speedMountian = 6;
	private final static int speedRoad = 2;
	private Landform[][] landformArray; // 用枚举类型二维数组存储地形
	private Landform[] landforms = Landform.values(); // 将枚举变量存入数组中
	private ArrayList<Dot> closed = new ArrayList<Dot>();
	private PriorityBlockingQueue<Dot> open = new PriorityBlockingQueue<Dot>(10, new Comparable());

	/**
	 * Constructor
	 * 
	 * @param Row
	 *            - 该地形图的宽
	 * @param Col
	 *            - 该地形图的高
	 */
	public Area(int Row, int Col) {
		this.ROW = Row;
		this.COL = Col;
		landformArray = new Landform[ROW][COL];
		setLandform();
	}

	/**
	 * 设置地形
	 */
	public void setLandform() {
		ReadLandform rc = new ReadLandform();
		landformArray = rc.getLandform();
	}

	/**
	 * 获取存储地形数据的二维数组
	 * 
	 * @return landformArray - 存储地形数据的二维数组
	 */
	public Landform[][] getLandforms() {
		return landformArray;
	}

	/**
	 * 获取地形块(soul_x, int soul_y)到地形块(int des_x, int des_y)之间的最短距离。
	 * 
	 * @param soul_x
	 *            - 起点地形块的x轴坐标
	 * @param soul_y
	 *            - 起点地形块的y轴坐标
	 * @param des_x
	 *            - 终点地形块的x轴坐标
	 * @param des_y
	 *            - 终点地形块的y轴坐标
	 * @return Dot - 返回终点的Dot
	 */
	public Dot getDistance(int soul_x, int soul_y, int des_x, int des_y) {
		if (landformArray[soul_x][soul_y] == landforms[1] || landformArray[soul_x][soul_y] == landforms[2]
				|| landformArray[des_x][des_y] == landforms[1] || landformArray[des_x][des_y] == landforms[2])
			return null;
		Dot soulDot = new Dot(soul_x, soul_y, 0, 0, null);
		Dot dot;

		// System.out.println("soulDot : " + soulDot);
		open.add(soulDot);
		do {
			if (open.isEmpty())
				// 如果open表为空，则两点之间不可达，返回null
				return null;
			dot = open.poll();
			if (!hasVisited(dot.getX(), dot.getY())) {
				closed.add(dot);
				// 3*3的方格
				// //四个对角线
				// addDot(dot.getX() + 1, dot.getY() + 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() + 1, dot.getY() - 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() - 1, dot.getY() + 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() - 1, dot.getY() - 1, des_x, des_y, 14,
				// dot);
				// //上，下，左，右
				// addDot(dot.getX() + 1, dot.getY(), des_x, des_y, 10, dot);
				// addDot(dot.getX() - 1, dot.getY(), des_x, des_y, 10, dot);
				// addDot(dot.getX(), dot.getY() + 1, des_x, des_y, 10, dot);
				// addDot(dot.getX(), dot.getY() - 1, des_x, des_y, 10, dot);

				// 5*5的方格
				addDot(dot.getX() - 2, dot.getY() - 1, des_x, des_y, 22, dot);
				addDot(dot.getX() - 2, dot.getY() - 1, des_x, des_y, 22, dot);

				addDot(dot.getX() - 1, dot.getY() - 2, des_x, des_y, 22, dot);
				addDot(dot.getX() - 1, dot.getY() - 1, des_x, des_y, 14, dot);
				addDot(dot.getX() - 1, dot.getY(), des_x, des_y, 10, dot);
				addDot(dot.getX() - 1, dot.getY() + 1, des_x, des_y, 14, dot);
				addDot(dot.getX() - 1, dot.getY() + 2, des_x, des_y, 22, dot);

				addDot(dot.getX(), dot.getY() - 1, des_x, des_y, 10, dot);
				addDot(dot.getX(), dot.getY() + 1, des_x, des_y, 10, dot);

				addDot(dot.getX() + 1, dot.getY() - 2, des_x, des_y, 22, dot);
				addDot(dot.getX() + 1, dot.getY() - 1, des_x, des_y, 14, dot);
				addDot(dot.getX() + 1, dot.getY(), des_x, des_y, 10, dot);
				addDot(dot.getX() + 1, dot.getY() + 1, des_x, des_y, 14, dot);
				addDot(dot.getX() + 1, dot.getY() - 1, des_x, des_y, 22, dot);

				addDot(dot.getX() + 2, dot.getY() - 1, des_x, des_y, 22, dot);
				addDot(dot.getX() + 2, dot.getY() + 1, des_x, des_y, 22, dot);

				// 不注释下面代码，则与上面5*5代码相加为7*7方格
				addDot(dot.getX() - 3, dot.getY() - 2, des_x, des_y, 36, dot);
				addDot(dot.getX() - 3, dot.getY() - 1, des_x, des_y, 32, dot);
				addDot(dot.getX() - 3, dot.getY() + 1, des_x, des_y, 32, dot);
				addDot(dot.getX() - 3, dot.getY() + 2, des_x, des_y, 36, dot);

				addDot(dot.getX() - 2, dot.getY() - 3, des_x, des_y, 36, dot);
				addDot(dot.getX() - 1, dot.getY() - 3, des_x, des_y, 32, dot);
				addDot(dot.getX() + 1, dot.getY() - 3, des_x, des_y, 32, dot);
				addDot(dot.getX() + 2, dot.getY() - 3, des_x, des_y, 36, dot);

				addDot(dot.getX() - 2, dot.getY() + 3, des_x, des_y, 36, dot);
				addDot(dot.getX() - 1, dot.getY() + 3, des_x, des_y, 32, dot);
				addDot(dot.getX() + 1, dot.getY() + 3, des_x, des_y, 32, dot);
				addDot(dot.getX() + 2, dot.getY() + 3, des_x, des_y, 36, dot);

				addDot(dot.getX() + 3, dot.getY() - 2, des_x, des_y, 36, dot);
				addDot(dot.getX() + 3, dot.getY() - 1, des_x, des_y, 32, dot);
				addDot(dot.getX() + 3, dot.getY() + 1, des_x, des_y, 32, dot);
				addDot(dot.getX() + 3, dot.getY() + 2, des_x, des_y, 36, dot);
			}
		} while (dot.getX() != des_x || dot.getY() != des_y);
		// System.out.println("Dot : " + dot);
		open.clear();
		closed.clear();
		return dot;
	}

	/**
	 * 向open表中添加元素
	 * 
	 * @param x
	 *            - 点的x轴坐标
	 * @param y
	 *            - 点的y轴坐标
	 * @param des_x
	 *            - 目的状态的x轴坐标
	 * @param des_y
	 *            - 目的状态的y轴坐标
	 * @param dir
	 *            - 父节点到该点的代价，该代价与方向有关
	 * @param parDot
	 *            - 父节点
	 */
	private void addDot(int x, int y, int des_x, int des_y, int dir, Dot parDot) {
		if (x >= 0 && x < ROW && y >= 0 && y < COL) {
			if (landformArray[x][y] == landforms[1] || landformArray[x][y] == landforms[2])
				return;

			// System.out.println("x : " + x +" y : " + y + " weight : " +
			// parDot.getWeight());
			int degree = 0;
			if (landformArray[x][y] == landforms[0])
				degree = parDot.getDegree() + dir * speedMountian;
			if (landformArray[x][y] == landforms[3])
				degree = parDot.getDegree() + dir * speedRoad;
			int weight = degree + 10 * (Math.abs(x - des_x) + Math.abs(y - des_y));
			Dot dot = new Dot(x, y, weight, degree, parDot);
			open.add(dot);
			// inOpen(dot);
		}
	}

	/**
	 * 查看closed表中是否有这个元素
	 * 
	 * @param x
	 *            - 点的x轴坐标
	 * @param y
	 *            - 点的y轴坐标
	 * @return boolean - 若closed表含有该点，返回true；否则，返回false
	 */
	private boolean hasVisited(int x, int y) {
		for (Dot dot : closed) {
			if (x == dot.getX() && y == dot.getY())
				return true;
		}
		return false;
	}

	/**
	 * 查看open表中是否有这个元素
	 * 
	 * @param dot
	 *            - 查看的点元素
	 * @return boolean - 如果open表中含有该点，返回true；否则，返回false
	 */
	private boolean inOpen(Dot dot) {
		int x = dot.getX();
		int y = dot.getY();

		for (Dot dotTemp : open) {
			if (x == dotTemp.getX() && y == dotTemp.getY()) {
				if (dot.getWeight() < dotTemp.getWeight()) {
					open.remove(dotTemp);
					open.add(dot);
				}
				return true;
			}
		}
		open.add(dot);
		return false;
	}
}

/**
 * 画出两个点之间的路径
 * 
 * @author 涂方蕾
 * @version 1.0
 */
class DrawPath extends JPanel {
	private Dot dot;
	private int soul_x, soul_y;

	/**
	 * Constructor
	 * 
	 * @param dot
	 *            - 终点
	 * @param soul_x
	 *            - 起点的x轴坐标
	 * @param soul_y
	 *            - 起点的y轴坐标
	 */
	public DrawPath(Dot dot, int soul_x, int soul_y) {
		this.dot = dot;
		this.soul_x = soul_x;
		this.soul_y = soul_y;
		// System.out.println(dot);
	}

	/**
	 * 画出两点之间的路径
	 */
	public void paintComponent(Graphics g) {
		/* 注释掉这句即可在原图基础上画图 */
		// super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;// 将Grqaphics强制转化为Graphics2D类型
		if (dot != null) {
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.BLACK);// 设置画笔颜色
			while (soul_x != dot.getX() || soul_y != dot.getY()) {
				g2.drawLine(dot.getX(), dot.getY(), dot.getParDot().getX(), dot.getParDot().getY());
				dot = dot.getParDot();
				// System.out.println(dot);
			}
		}
	}
}

/**
 * 在计算过程中，给正在计算的点着红色，已经计算过的点着绿色
 * 
 * @author 涂方蕾
 * @version 1.0
 */
class DrawPoint extends JPanel {
	private int x, y, flag;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            - 点的x轴坐标
	 * @param y
	 *            - 点的y轴坐标
	 * @param flag
	 *            - 如果为0，表示该点的路径已经计算过；否则，正在计算中
	 */
	public DrawPoint(int x, int y, int flag) {
		this.x = x;
		this.y = y;
		this.flag = flag;
	}

	/**
	 * 给点上色
	 */
	public void paintComponent(Graphics g) {
		/* 注释掉这句即可在原图基础上画图 */
		// super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;// 将Grqaphics强制转化为Graphics2D类型
		g2.setStroke(new BasicStroke(4));
		if (flag == 0)
			g2.setColor(Color.green);// 设置画笔颜色
		else {
			g2.setColor(Color.red);// 设置画笔颜色
		}
		g2.drawLine(x, y, x, y);
	}
}

/**
 * 给出优先队列的比较规则
 * 
 * @author 涂方蕾
 * @version 1.0
 */
class Comparable implements Comparator<Object> {

	/**
	 * 给出优先队列的比较规则
	 */
	public int compare(Object arg0, Object arg1) {
		if (((Dot) arg0).getWeight() < ((Dot) arg1).getWeight())
			return -1;
		else if (((Dot) arg0).getWeight() == ((Dot) arg1).getWeight()) {
			return 0;
		}
		return 1;
	}
}
