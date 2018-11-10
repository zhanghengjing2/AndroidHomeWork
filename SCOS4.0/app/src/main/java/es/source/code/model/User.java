package es.source.code.model;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String userName, password;
    private Boolean oldUser;
    private int count=0;//用来记录该用户点了多少个菜
    private int consumption=0;//用来记录该用户消费了多少钱
    private List<Dish> ordered = new ArrayList<>();//已点的菜品



    //返回菜单
    public List<Dish> getOrdered() { return ordered; }
    //点一道菜
    public void addOrdered(Dish dish) { this.ordered.add(dish);this.count++;this.consumption+=dish.getPrice(); }
    //去除一道菜
    public void removeOrdered(Dish dish) { this.ordered.remove(dish);this.count--;this.consumption-=dish.getPrice(); }
    //清空菜单
    public void ClearOrdered() { this.ordered.clear();this.count=0;this.consumption=0;}
    //返回菜品总数
    public int getCount() { return count; }
    //返回消费总金额
    public int getConsumption() { return consumption; }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getOldUser() {
        return oldUser;
    }

    public void setOldUser(Boolean oldUser) {
        this.oldUser = oldUser;
    }
}
