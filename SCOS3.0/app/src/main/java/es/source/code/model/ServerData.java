package es.source.code.model;

import android.os.Parcelable;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class ServerData extends BmobObject implements Serializable {
    private String name;
    private int number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
