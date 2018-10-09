package es.source.code.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.Adapter.detailedAdapter;
import es.source.code.model.Dish;
import es.source.code.model.User;

public class FoodDetailed extends AppCompatActivity {

    private Button btn;//按钮
    private int selectedDish;//记录选择了第几个菜品
    private ViewPager vp;
    private MyApplication myApplication;
    private List<Dish> dishList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_detailed);

        //初始化部件
        InitCompoment();


        //获取菜品列表
        myApplication=(MyApplication)getApplication();
        dishList = myApplication.getDishList();

        //获取选择的是第几个菜品
        Dish d =(Dish)getIntent().getSerializableExtra("selectedDish");
        selectedDish=FindIndex(d,dishList);


        //初始化适配器
        detailedAdapter adapter = new detailedAdapter(dishList,R.layout.detail_layout,FoodDetailed.this);
        vp.setAdapter(adapter);

        //根据菜品位置，选择当前显示页
        vp.setCurrentItem(selectedDish);

        //设置按钮状态
        setButton(dishList.get(selectedDish));

        //为viewpager设置监听事件
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectedDish=position;
                //设置按钮状态
                setButton(dishList.get(selectedDish));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private int FindIndex(Dish d, List<Dish> dishList) {
        int index=-1;
        for(int i=0;i<dishList.size();i++)
        {
            if(dishList.get(i).getName().equals(d.getName()))
            {
                index=i;
                //Toast.makeText(FoodDetailed.this, "序号:"+index, Toast.LENGTH_LONG).show();
            }

        }
        return index;
    }

    private void InitCompoment() {
        btn =(Button)findViewById(R.id.detailed_button);
        vp = (ViewPager)findViewById(R.id.detailed_veiwpager);
    }

    public void setButton(Dish dish) {
        if(dish.isChoosed())
            btn.setText("退点");
        else
            btn.setText("点菜");
    }
}
