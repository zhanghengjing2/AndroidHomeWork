package es.source.code.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.source.code.activity.R;
import es.source.code.model.Dish;

public class detailedAdapter extends PagerAdapter {

    private List<Dish> foodList;
    private List<View> viewList;
    private int resId;
    private Context mContext;

    public detailedAdapter(List<Dish> foodList, int resId, Context mContext){
        this.foodList = foodList;
        this.resId = resId;
        this.mContext = mContext;
        initViewList();
    }

    //为每一个菜品添加一个视图
    private void initViewList() {
        viewList = new ArrayList<>();
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        for (Dish food : foodList) {
            viewList.add(inflater.inflate(resId, null));
        }
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View pageView = viewList.get(position);
        Dish food = foodList.get(position);

        TextView tvFoodName = (TextView) pageView.findViewById(R.id.show_dish_name);
        TextView tvFoodPrice = (TextView) pageView.findViewById(R.id.show_dish_price);
        ImageView ivFood = (ImageView) pageView.findViewById(R.id.detailed_imageview1);

        tvFoodName.setText(food.getName());

        String price = String.valueOf(food.getPrice())+"元";
        tvFoodPrice.setText(price);

        ivFood.setImageResource(food.getImageurl());

        container.addView(viewList.get(position));


        return viewList.get(position);
    }
}
