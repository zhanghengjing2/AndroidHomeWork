package es.source.code.eventbus;

import es.source.code.model.DishList;

public class EventInfo {
    private int what;//相当于message的what
    private DishList dlist;//存放数据

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public DishList getDlist() {
        return dlist;
    }

    public void setDlist(DishList dlist) {
        this.dlist = dlist;
    }
}
