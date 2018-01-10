package nest.mdc.display;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class DisplayString extends JPanel {

	public String str;
	public int x;
	public int y;

	public DisplayString(String str, int x, int y) {
		this.str = str;
		this.x = x;
		this.y = y;
	}

	/* 重写父类的方法，定义自己的画法 */
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g; // 将Grqaphics强制转化为Graphics2D类型
		// g2.setColor(Color.WHITE); // 设置背景颜色
		// g2.fillRect(x, y - 9, 7, 9);
		// g2.setColor(Color.black);
		g2.drawString(str, x, y);
		// g2.fillRect(0, 0, 500, 500); //设置背景大小

	}
}
