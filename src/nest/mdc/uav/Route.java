package nest.mdc.uav;

import nest.mdc.network.Point;

/**
 * Created by qiu on 17-6-6.
 */
public interface Route extends Comparable {
    public double getDistance();//���㳤��
    public void addPoint(Point point);//���ӽڵ�
    public void deletePoint(Point point);//ɾ���ڵ�
    int compareTo(Object o);//route����Ƚ�
}
