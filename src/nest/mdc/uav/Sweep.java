package nest.mdc.uav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import nest.mdc.network.Node;
public class Sweep {
    private Random rand;
    public final static int UAVCapacity = 600; 
    public Sweep(Random rand){
        this.rand=rand;
    }
    public ArrayList<RouteWithoutDepot> initialize(ArrayList<Node> customers) {
        ArrayList<Node> customersCopy=new ArrayList<Node>();
        ArrayList<RouteWithoutDepot> routes=new ArrayList<RouteWithoutDepot>();
        int finishedSize=0;
        int customerSize=customers.size();
        for(Node x:customers){
            customersCopy.add((Node)x.clone());
        }
        Node start=customersCopy.get(rand.nextInt(customerSize));
        for(Node node:customersCopy){
            node.setAngle(start);
        }
        Collections.sort(customersCopy, new Comparator<Node>() {
            public int compare(Node o1, Node o2) {
                if(o1.getAngle()>o2.getAngle()){
                    return 1;
                }
                if(o1.getAngle()<o2.getAngle()){
                    return -1;
                }
                return 0;
            }
        });
        while(finishedSize!=customerSize){
            RouteWithoutDepot newRoute=new RouteWithoutDepot();
            while(newRoute.getDistance()<UAVCapacity){
                try {
                    Node TryAdd = customersCopy.get(0);
                    newRoute.addPoint(TryAdd);
                    finishedSize++;
                    customersCopy.remove(TryAdd);
                } catch (Exception e) {
                    break;
                }
            }
            if(newRoute.getDistance()>=UAVCapacity){
                Node PutBack=newRoute.getRoute().get(newRoute.getRoute().size()-1);
                newRoute.deletePoint(PutBack);
                finishedSize--;
                customersCopy.add(0,PutBack);
            }
            routes.add(newRoute);
        }
        return routes;
    }
}