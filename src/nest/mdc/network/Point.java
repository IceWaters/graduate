/**
 * 
 */
package nest.mdc.network;

import nest.mdc.field.Field;

/**
 * define a 2D point
 * @author xiaoq
 * @version 1.0
 */
public class Point {

	private double X = -1;
	private double Y = -1;
	private double angle;

	
	/*构造函数*/
	public Point (final double x, final double y){
		if(!setCoordinate(x, y)) {
			System.out.println("The coordinate is illegal!");
		}

	}
	
	public Point (Node node){
		if(!setCoordinate(node.getXCoordinate(), node.getYCoordinate())){
			System.out.println("The coordinate is illegal!");
		}
	}


	
	

	/*对点的坐标进行设定*/
	public boolean setCoordinate(final double x, final double y) {
		if(x > Field.iMaxX || x < Field.iMinX || y > Field.iMaxY || y < Field.iMinY) {
			System.out.println(x + " : " + y);
			return false;
		}
		else {
			X = x;
			Y = y;
			return true;
		}
	}
	
	public double getXCoordinate(){
		return X;
	}
	
	public double getYCoordinate(){
		return Y;
	}
	

	
	/*获取当前点到某点的距离*/
	public double getDistance (Point a)
	{
	    return Math.sqrt(Math.pow((this.X-a.X),2)+Math.pow((this.Y-a.Y),2));
	}
	
	public double getAngle(){
	    return this.angle;
	}
	
	public void setAngle(Point start){
        double ydist=Math.abs(this.Y-start.getYCoordinate());
        double xdist=Math.abs(this.X-start.getXCoordinate());
        if(xdist==0){
            this.angle=0;
        }else {
            this.angle = Math.atan(ydist / xdist);
        }
    }

}