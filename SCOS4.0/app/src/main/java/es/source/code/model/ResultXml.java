package es.source.code.model;

import java.util.List;


public class ResultXml extends ResultBase{

    // Food列表
    private List<Dish> dataList;

    public ResultXml() {

    }

    public ResultXml(int RESULTCODE, String msg, List<Dish> dataList) {
        super(RESULTCODE,msg);
        this.dataList = dataList;
    }

    public List<Dish> getDataList() {
        return dataList;
    }

    public void setDataList(List<Dish> dataList) {
        this.dataList = dataList;
    }
}
