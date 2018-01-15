package nest.mdc.uav;
import java.util.*;
public class MySet<E> extends LinkedHashSet<E>{
    public MySet(){
        super();
    }
    public MySet(Collection<? extends E> c){
        super(c);
    }
    public boolean add(E e){
        boolean flag=false;
        for(Object item:super.toArray()){
            if(((E)item).equals(e)){
                flag=true;
                break;
            }
        }
        if(!flag){
            super.add(e);
        }
        return true;
    }
    @Override
    public boolean addAll(Collection<? extends E> lists){
        for(E AddItem:lists){
            boolean flag=false;
            for(Object HadItem:super.toArray()){
                if(((E)HadItem).equals(AddItem)){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                super.add(AddItem);
            }
        }
        return true;
    }
}