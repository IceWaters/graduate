package nest.mdc.uav;
import java.util.*;

import nest.mdc.network.Node;
import nest.mdc.network.Point;
/**
 * Created by qiu on 17-6-7.
 */
public class RouteWithoutDepot implements Route{
    ArrayList<Node> route;
    double cost = 0;
    double label=0;
    RouteWithoutDepot(){
        super();
        route = new ArrayList<Node>();
    }
    public void setLabel(double cost){
        this.label=cost;
    }
    public double getLabel(){
        return this.label;
    }
    public void addPoint(Point node){
        route.add((Node) node);
    }
    public void addPoints(List<Node> nodes){route.addAll(nodes);}
    public void deletePoint(Point node){
        if (route.contains((Node) node)) {
            route.remove((Node) node);
        }
    }

    public ArrayList<Node> getRoute() {
        return route;
    }

    public double getDistance(){
        double distance = 0;
        if(route.size()==0){
            return 0;
        }else{
            for (int i = 0; i < route.size() - 1; i++) {
                distance = distance + route.get(i).getDistance(route.get(i + 1));
            }
            return distance + route.get(route.size() - 1).getDistance(route.get(0));
        }
    }

    public int compareTo(Object o) {
        RouteWithoutDepot other = (RouteWithoutDepot) o;
        if (getDistance() > other.getDistance()) {
            return 1;
        }
        if (getDistance() < other.getDistance()) {
            return -1;
        }
        return 0;
    }

    private void setCost() {
        if (route.size() == 1) {
            this.cost = Double.MAX_VALUE - 1;
        } else if (route.size() == 0) {
            this.cost = Double.MAX_VALUE - 1;
        } else {
            this.cost = getDistance() / this.route.size();
        }
    }

    public RouteWithoutDepot CopyRoute(){
        RouteWithoutDepot result=new RouteWithoutDepot();
        for(Node n:route){
            result.addPoint(new Node(n.getXCoordinate(),n.getYCoordinate(), n.getNodeID()));
        }
        return result;
    }

    public double getCost() {
        setCost();//计算路径的代价
        return cost;
    }
    public String toString(){
        String s="[";
        for(Node x:route){
            s=s+x+" ";
        }
        s=s+"]";
        return s;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RouteWithoutDepot other = (RouteWithoutDepot) obj;
        int size1=this.route.size();
        int size2=other.route.size();
        if(size1!=size2){
            return  false;
        }else {
            for (Node n : this.route) {
                for (Node m : other.route) {
                    if (n.equals(m)) {
                        size1--;
                    }
                }
            }
            if (size1 == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

}
