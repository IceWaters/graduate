package nest.mdc.landform;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import nest.mdc.field.Field;

/**
 * 读取地形图的RGB值，并将地形情况存入二维数组中
 * 
 * @author 涂方蕾
 * @version 1.0
 */
public class ReadLandform {

	public static Landform[][] landformArray = new Landform[Field.iMaxX][Field.iMaxY]; // 用枚举类型二维数组存储地形

	/**
	 * Constructor
	 */
	public ReadLandform() {
		try {
			getImagePixel("landform.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor
	 */
	public ReadLandform(String name) {
		try {
			getImagePixel(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取存储地形信息的二维数组
	 * 
	 * @return landformArray - 存储地形信息的二维数组
	 */
	public Landform[][] getLandform() {
		return landformArray;
	}

	/**
	 * 读取地形图片得到每一个像素值，并存入二维数组中
	 * 
	 * @param image
	 *            - 图像的文件名
	 * @throws Exception
	 */
	private void getImagePixel(String image) throws Exception {
		int[] rgb = new int[3];
		File file = new File(image);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		int minx = bi.getMinX();
		int miny = bi.getMinY();
		// System.out.println("width=" + width + ",height=" + height + ".");
		// System.out.println("minx=" + minx + ",miniy=" + miny + ".");
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				int num = rgb[0] + rgb[1] + rgb[2];
				switch (num) {
				// case 471: landformArray[i][j] = Landform.LAKE; break;
				// case 416: landformArray[i][j] = Landform.MOUNTAIN; break;
				// case 198: landformArray[i][j] = Landform.FLAT; break;
				// default : landformArray[i][j] = Landform.ROAD;break;
				case 255:
					landformArray[i][j] = Landform.ROAD2;
					break;
				case 383:
					landformArray[i][j] = Landform.ROAD;
					break;
				default:
					landformArray[i][j] = Landform.HARD;
					break;
				}
			}
		}
	}

}
