package es.source.code.activity;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import es.source.code.model.Dish;
import es.source.code.model.User;

public class MyApplication extends Application {

    //当前登录用户
    private User loginUser = new User();

    //当前用户的菜单框
    private List<Dish> dishList = new ArrayList<>();

    //设置用户
    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    //得到用户
    public User getLoginUser() {
        return loginUser;
    }

    //得到用户菜单框
    public List<Dish> getDishList() {
        return dishList;
    }

    //设置用户菜单框
    public void setDishList(List<Dish> dishList) {
        this.dishList = dishList;
    }

    //用户登出
    public void userLogout(){
        loginUser = new User();
    }
}
