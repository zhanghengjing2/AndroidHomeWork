package es.source.code.model;


import java.io.Serializable;
import java.util.List;

//菜品的集合
public class DishList implements Serializable {
    private List<Dish> coldFoodList;
    private List<Dish> hotFoodList;
    private List<Dish> seaFoodList;
    private List<Dish> drinkingList;

    public List<Dish> getColdFoodList() {
        return coldFoodList;
    }

    public void setColdFoodList(List<Dish> coldFoodList) {
        this.coldFoodList = coldFoodList;
    }

    public List<Dish> getHotFoodList() {
        return hotFoodList;
    }

    public void setHotFoodList(List<Dish> hotFoodList) {
        this.hotFoodList = hotFoodList;
    }

    public List<Dish> getSeaFoodList() {
        return seaFoodList;
    }

    public void setSeaFoodList(List<Dish> seaFoodList) {
        this.seaFoodList = seaFoodList;
    }

    public List<Dish> getDrinkingList() {
        return drinkingList;
    }

    public void setDrinkingList(List<Dish> drinkingList) {
        this.drinkingList = drinkingList;
    }
}
