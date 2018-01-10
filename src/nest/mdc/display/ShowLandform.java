package nest.mdc.display;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import nest.mdc.landform.Landform;


/**
 * ��������ͼ
 * @author Ϳ����
 * @version 1.0
 */
 public class ShowLandform extends JPanel{
	
	public static final int Length = 1;
	
	private int rows;
	private int cols;
	private Landform landform = null;
	private Landform[][] landformArray;
	private Color mountianColor = new Color(205, 138, 73);
	private Color lakeColor = new Color(99,124,248);
	private Color flatColor= new Color(31,46,121);
	private Color roadColor = new Color(142,145,161);
	private Color roadColor2 = new Color(242,202,150);
	private Color roadColor3 = new Color(0, 255, 0);
	
	/**
	 * Constructor
	 * @param rows - ����ͼ�Ŀ��
	 * @param cols - ����ͼ�ĸ߶�
	 * @param landformArray - �洢����ͼ�Ķ�ά����
	 */
	public ShowLandform(int rows, int cols, Landform[][] landformArray) {
		this.rows = rows;
		this.cols = cols;
		this.landformArray = landformArray;
   //     System.out.println(rows + "  " + cols);
	}
	
//	/**
//	 * ���ص���
//	 * @return area - ����ͼ��Ϣ
//	 */
//	public Area getArea(){
//		return area;
//	}
	
	@Override
	public void paintComponent(Graphics g){
	//	super.paintComponent(g);
		//Graphics2D g2 = (Graphics2D)g;//��Grqaphicsǿ��ת��ΪGraphics2D����
		//g2.setStroke(new BasicStroke(1));//���û��ʴ�С
	//	System.out.println(rows + "  " + cols);
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < cols; col++){
				landform = landformArray[row][col];
		//		System.out.println(row + "  " + col);
				switch (landform) {
					case MOUNTAIN: g.setColor(mountianColor);       break;
					case LAKE: g.setColor(lakeColor);      break;
					case FLAT: g.setColor(flatColor);    break;
					case ROAD: g.setColor(roadColor2);		break;
					case ROAD2: g.setColor(roadColor3);    break;
					case HARD: g.setColor(Color.white); 	break;
					default:
						break;
				}
				g.fillRect(row*Length, col*Length, Length, Length);
			}
		}
	}		
}
