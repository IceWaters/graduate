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
 * ö�ٵ���ͼ�еĵ���
 * 
 * @author Ϳ����
 * @version 1.0
 */
public enum Landform {
	MOUNTAIN, LAKE, FLAT, ROAD, HARD, ROAD2
}

/**
 * ����ͼ�еĵ�Ԫ��
 * 
 * @author Ϳ����
 * @version 1.0
 */
class Dot {

	private int x, y;// ���λ��
	private int weight, degree;
	private Dot parDot;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            - ���x������λ��
	 * @param y
	 *            - ���y������λ��
	 * @param weight
	 *            - �ӳ�ʼ�㾭�ɽڵ�n��Ŀ���Ĺ���Ȩֵ
	 * @param degree
	 *            - �ӳ�ʶ״̬���õ��ʵ�ʴ���
	 * @param parDot
	 *            - �õ�ĸ��ڵ�
	 */
	public Dot(int x, int y, int weight, int degree, Dot parDot) {
		this.x = x;
		this.y = y;
		this.parDot = parDot;
		this.weight = weight;
		this.degree = degree;
	}

	/**
	 * �õ����x������
	 * 
	 * @return x - x������
	 */
	public int getX() {
		return x;
	}

	/**
	 * �õ����y������
	 * 
	 * @return y - y������
	 */
	public int getY() {
		return y;
	}

	/**
	 * �õ��õ��Ȩֵ
	 * 
	 * @return weight - �ӳ�ʼ�㾭�ɽڵ�n��Ŀ���Ĺ���Ȩֵ
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * �õ��õ�Ĵӳ�ʶ״̬���õ��ʵ�ʴ���
	 * 
	 * @return degree - �õ�Ĵӳ�ʶ״̬���õ��ʵ�ʴ���
	 */
	public int getDegree() {
		return degree;
	}

	/**
	 * �õ��õ�ĸ��ڵ�
	 * 
	 * @return parDot - �õ�ĸ��ڵ�
	 */
	public Dot getParDot() {
		return parDot;
	}
}

/**
 * ���õ���ͼ������A*�㷨�ҳ������ڵ�֮�����̾���
 * 
 * @author Ϳ����
 * @version 1.0
 */
class Area {
	private int ROW;
	private int COL;
	private final static int speedMountian = 6;
	private final static int speedRoad = 2;
	private Landform[][] landformArray; // ��ö�����Ͷ�ά����洢����
	private Landform[] landforms = Landform.values(); // ��ö�ٱ�������������
	private ArrayList<Dot> closed = new ArrayList<Dot>();
	private PriorityBlockingQueue<Dot> open = new PriorityBlockingQueue<Dot>(10, new Comparable());

	/**
	 * Constructor
	 * 
	 * @param Row
	 *            - �õ���ͼ�Ŀ�
	 * @param Col
	 *            - �õ���ͼ�ĸ�
	 */
	public Area(int Row, int Col) {
		this.ROW = Row;
		this.COL = Col;
		landformArray = new Landform[ROW][COL];
		setLandform();
	}

	/**
	 * ���õ���
	 */
	public void setLandform() {
		ReadLandform rc = new ReadLandform();
		landformArray = rc.getLandform();
	}

	/**
	 * ��ȡ�洢�������ݵĶ�ά����
	 * 
	 * @return landformArray - �洢�������ݵĶ�ά����
	 */
	public Landform[][] getLandforms() {
		return landformArray;
	}

	/**
	 * ��ȡ���ο�(soul_x, int soul_y)�����ο�(int des_x, int des_y)֮�����̾��롣
	 * 
	 * @param soul_x
	 *            - �����ο��x������
	 * @param soul_y
	 *            - �����ο��y������
	 * @param des_x
	 *            - �յ���ο��x������
	 * @param des_y
	 *            - �յ���ο��y������
	 * @return Dot - �����յ��Dot
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
				// ���open��Ϊ�գ�������֮�䲻�ɴ����null
				return null;
			dot = open.poll();
			if (!hasVisited(dot.getX(), dot.getY())) {
				closed.add(dot);
				// 3*3�ķ���
				// //�ĸ��Խ���
				// addDot(dot.getX() + 1, dot.getY() + 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() + 1, dot.getY() - 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() - 1, dot.getY() + 1, des_x, des_y, 14,
				// dot);
				// addDot(dot.getX() - 1, dot.getY() - 1, des_x, des_y, 14,
				// dot);
				// //�ϣ��£�����
				// addDot(dot.getX() + 1, dot.getY(), des_x, des_y, 10, dot);
				// addDot(dot.getX() - 1, dot.getY(), des_x, des_y, 10, dot);
				// addDot(dot.getX(), dot.getY() + 1, des_x, des_y, 10, dot);
				// addDot(dot.getX(), dot.getY() - 1, des_x, des_y, 10, dot);

				// 5*5�ķ���
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

				// ��ע��������룬��������5*5�������Ϊ7*7����
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
	 * ��open�������Ԫ��
	 * 
	 * @param x
	 *            - ���x������
	 * @param y
	 *            - ���y������
	 * @param des_x
	 *            - Ŀ��״̬��x������
	 * @param des_y
	 *            - Ŀ��״̬��y������
	 * @param dir
	 *            - ���ڵ㵽�õ�Ĵ��ۣ��ô����뷽���й�
	 * @param parDot
	 *            - ���ڵ�
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
	 * �鿴closed�����Ƿ������Ԫ��
	 * 
	 * @param x
	 *            - ���x������
	 * @param y
	 *            - ���y������
	 * @return boolean - ��closed���иõ㣬����true�����򣬷���false
	 */
	private boolean hasVisited(int x, int y) {
		for (Dot dot : closed) {
			if (x == dot.getX() && y == dot.getY())
				return true;
		}
		return false;
	}

	/**
	 * �鿴open�����Ƿ������Ԫ��
	 * 
	 * @param dot
	 *            - �鿴�ĵ�Ԫ��
	 * @return boolean - ���open���к��иõ㣬����true�����򣬷���false
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
 * ����������֮���·��
 * 
 * @author Ϳ����
 * @version 1.0
 */
class DrawPath extends JPanel {
	private Dot dot;
	private int soul_x, soul_y;

	/**
	 * Constructor
	 * 
	 * @param dot
	 *            - �յ�
	 * @param soul_x
	 *            - ����x������
	 * @param soul_y
	 *            - ����y������
	 */
	public DrawPath(Dot dot, int soul_x, int soul_y) {
		this.dot = dot;
		this.soul_x = soul_x;
		this.soul_y = soul_y;
		// System.out.println(dot);
	}

	/**
	 * ��������֮���·��
	 */
	public void paintComponent(Graphics g) {
		/* ע�͵���伴����ԭͼ�����ϻ�ͼ */
		// super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;// ��Grqaphicsǿ��ת��ΪGraphics2D����
		if (dot != null) {
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.BLACK);// ���û�����ɫ
			while (soul_x != dot.getX() || soul_y != dot.getY()) {
				g2.drawLine(dot.getX(), dot.getY(), dot.getParDot().getX(), dot.getParDot().getY());
				dot = dot.getParDot();
				// System.out.println(dot);
			}
		}
	}
}

/**
 * �ڼ�������У������ڼ���ĵ��ź�ɫ���Ѿ�������ĵ�����ɫ
 * 
 * @author Ϳ����
 * @version 1.0
 */
class DrawPoint extends JPanel {
	private int x, y, flag;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            - ���x������
	 * @param y
	 *            - ���y������
	 * @param flag
	 *            - ���Ϊ0����ʾ�õ��·���Ѿ���������������ڼ�����
	 */
	public DrawPoint(int x, int y, int flag) {
		this.x = x;
		this.y = y;
		this.flag = flag;
	}

	/**
	 * ������ɫ
	 */
	public void paintComponent(Graphics g) {
		/* ע�͵���伴����ԭͼ�����ϻ�ͼ */
		// super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;// ��Grqaphicsǿ��ת��ΪGraphics2D����
		g2.setStroke(new BasicStroke(4));
		if (flag == 0)
			g2.setColor(Color.green);// ���û�����ɫ
		else {
			g2.setColor(Color.red);// ���û�����ɫ
		}
		g2.drawLine(x, y, x, y);
	}
}

/**
 * �������ȶ��еıȽϹ���
 * 
 * @author Ϳ����
 * @version 1.0
 */
class Comparable implements Comparator<Object> {

	/**
	 * �������ȶ��еıȽϹ���
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
