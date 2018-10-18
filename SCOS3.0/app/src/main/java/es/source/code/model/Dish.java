package es.source.code.model;

import android.media.Image;

import java.io.Serializable;

public class Dish implements Serializable {
    private String name;//菜名
    private int price;//价格
    private int catgory;//菜的类别.0冷菜，1热菜，2海鲜，3酒水
    private int Imageurl;
    private int dishcount;//菜品库存
    private boolean order; // 是否点单

    public boolean isOrder() { return order; }

    public void setOrder(boolean order) { this.order = order; }

    public int getDishcount() {
        return dishcount;
    }

    public void setDishcount(int dishcount) {
        this.dishcount = dishcount;
    }

    private boolean isChoosed;

    public boolean isChoosed() { return isChoosed; }

    public void setChoosed(boolean choosed) { isChoosed = choosed; }

    public int getCount() {
        return count;
    }

    public void addCount() {
        this.count ++;
    }
    public void subCount() {
        this.count --;
    }

    private int count=0;//记录该菜被点击次数

    public int getCatgory() {
        return catgory;
    }

    public void setCatgory(int catgory) {
        this.catgory = catgory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    //设置图片url
    public int getImageurl() {
        return Imageurl;
    }

    public void setImageurl(int imageurl) {
        Imageurl = imageurl;
    }





}
