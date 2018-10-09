package es.source.code.activity;


import android.app.Application;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.Adapter.orderdishAdapter;
import es.source.code.model.Dish;
import es.source.code.model.User;


public class FoodOrderView extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Button btn_submit;
    private TextView dish_count;
    private MyApplication mApplication;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_order_view);

        //初始化部件
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        dish_count=(TextView)findViewById(R.id.dish_count);

        //获取用户
        mApplication=(MyApplication)getApplication();
        user=mApplication.getLoginUser();
        Toast.makeText(FoodOrderView.this, "用户名:"+user.getUserName()+"用户点菜数:"+user.getOrdered().size(), Toast.LENGTH_SHORT).show();
        //使用Tablayout的newTab()创建tab,设置tab项显示的文字
        final String[] mTabNames=new String[]{"未下单菜","已下单菜"};



        ////定义一个视图集合（用来装左右滑动的页面视图）
        final List<View> viewList = new ArrayList<View>();

        //定义两个视图，两个视图都加载同一个布局文件list_view.ml
        View view1 = getLayoutInflater().inflate(R.layout.list_view, null);
        View view2 = getLayoutInflater().inflate(R.layout.list_view, null);

        //将view加入viewList
        viewList.add(view1);
        viewList.add(view2);

        //为viewpager设置适配器
        mViewPager.setAdapter(new PagerAdapter() {
            private String name;
            @Override
            public int getCount() {
                //这个方法是返回总共有几个滑动的页面（）
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                //该方法判断是否由该对象生成界面。
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //这个方法返回一个对象，该对象表明PagerAapter选择哪个对象放在当前的ViewPager中。这里我们返回当前的页面
                mViewPager.addView(viewList.get(position));

                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                //这个方法从viewPager中移动当前的view。（划过的时候）
                mViewPager.removeView(viewList.get(position));
            }


            //重写getPageTitle()方法
            @Override
            public CharSequence getPageTitle(int position) {
                name=mTabNames[position].toString();
                return mTabNames[position];
            }

        });


        ListView listView1 = (ListView) view1.findViewById(R.id.listview);
        ListView listView2 = (ListView) view2.findViewById(R.id.listview);


        //这里我们传入数据
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();

        for(int i = 0; i<user.getOrdered().size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            Dish d = user.getOrdered().get(i);
            map.put("name", d.getName());
            map.put("price",d.getPrice());
            map.put("count",d.getCount());
            items.add(map);
        }

        final orderdishAdapter adapter = new orderdishAdapter (FoodOrderView.this, items,R.layout.dish_items, new String[]{"name","price","count"}, new int[]{R.id.order_dish_name,R.id.order_dish_price,R.id.order_dish_count});


        //为ListView设置适配器
        listView1.setAdapter(adapter);
        listView2.setAdapter(adapter);


        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tab.getText().equals("未下单菜"))
                    btn_submit.setText("提交");
                else
                    btn_submit.setText("结账");
                //Toast.makeText(FoodOrderView.this, tab.getText(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });



    }




}
