package es.source.code.activity;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.source.code.Utils.CommonUtils;
import es.source.code.model.Dish;
import es.source.code.model.DishList;
import es.source.code.model.User;

public class MyApplication extends Application {

    private static MyApplication app;
    //当前登录用户
    private User loginUser = new User();

    //当前用户的菜单框
    private List<Dish> dishList = new ArrayList<>();

    //分类别获取所有菜品
    private DishList fullDishlist = new DishList();
    private DishList fullDishlistfromJson = new DishList();

    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }


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
        List<Dish> list = new ArrayList<>();
        DishList dlist = getFullListFromJson();
        for(Dish dish :dlist.getColdFoodList())
            list.add(dish);
        for(Dish dish :dlist.getHotFoodList())
            list.add(dish);
        for(Dish dish :dlist.getSeaFoodList())
            list.add(dish);
        for(Dish dish :dlist.getDrinkingList())
            list.add(dish);
        return list;
    }

    //从json得到数据
    public DishList getFullListFromJson()
    {
        Gson gson = new Gson();
        String result = CommonUtils.getJson("dish.json",getInstance());
        return gson.fromJson(result, DishList.class);

    }



    //用户登出
    public void userLogout(){
        loginUser = new User();
    }
}
