package es.source.code.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.Adapter.listviewAdapter;
import es.source.code.model.Dish;
import es.source.code.model.User;

public class FoodView extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Dish> dishList = new ArrayList<>();//菜品列表
    private User user;
    private static MyApplication mApplication;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                 // Handle action bar item clicks here. The action bar will
                 // automatically handle clicks on the Home/Up button, so long
                 // as you specify a parent activity in AndroidManifest.xml.
                 switch (item.getItemId()) {
                     case R.id.dish_order:
                             Toast.makeText(this, "已点菜品", Toast.LENGTH_SHORT).show();
                             break;
                     case R.id.dish_show:
                             Toast.makeText(this, "查看订单", Toast.LENGTH_SHORT).show();
                             break;
                     case R.id.dish_help:
                         Toast.makeText(this, "呼叫服务", Toast.LENGTH_SHORT).show();
                         break;
                     default:
                             break;
                     }
                 return super.onOptionsItemSelected(item);
             }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);

        //获取用户
        mApplication=(MyApplication)getApplication();
        user=mApplication.getLoginUser();
        //填充菜品
        LoadDishes(dishList);
        //初始化部件
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //使用Tablayout的newTab()创建tab,设置tab项显示的文字
        final String[] mTabNames=new String[]{"冷菜","热菜","海鲜","酒水"};



        ////定义一个视图集合（用来装左右滑动的页面视图）
        final List<View> viewList = new ArrayList<View>();

        //定义四个视图，四个视图都加载同一个布局文件list_view.ml
        View view1 = getLayoutInflater().inflate(R.layout.list_view, null);
        View view2 = getLayoutInflater().inflate(R.layout.list_view, null);
        View view3 = getLayoutInflater().inflate(R.layout.list_view, null);
        View view4 = getLayoutInflater().inflate(R.layout.list_view, null);

        //将view加入viewList
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);

        //为viewpager设置适配器
        mViewPager.setAdapter(new PagerAdapter() {
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
                //返回tab选项的名字
                return mTabNames[position];
            }

        });


        ListView listView1 = (ListView) view1.findViewById(R.id.listview);
        ListView listView2 = (ListView) view2.findViewById(R.id.listview);
        ListView listView3 = (ListView) view3.findViewById(R.id.listview);
        ListView listView4 = (ListView) view4.findViewById(R.id.listview);

        List<Map<String, Object>> items1 = InitData(dishList,0);
        List<Map<String, Object>> items2 = InitData(dishList,1);
        List<Map<String, Object>> items3 = InitData(dishList,2);
        List<Map<String, Object>> items4 = InitData(dishList,3);

        final listviewAdapter adapter1 = new listviewAdapter(FoodView.this, items1,R.layout.items, new String[]{"name","price"}, new int[]{R.id.dish_name,R.id.dish_price,},user);
        final listviewAdapter adapter2 = new listviewAdapter(FoodView.this, items2,R.layout.items, new String[]{"name","price"}, new int[]{R.id.dish_name,R.id.dish_price},user);
        final listviewAdapter adapter3 = new listviewAdapter(FoodView.this, items3,R.layout.items, new String[]{"name","price"}, new int[]{R.id.dish_name,R.id.dish_price},user);
        final listviewAdapter adapter4 = new listviewAdapter(FoodView.this, items4,R.layout.items, new String[]{"name","price"}, new int[]{R.id.dish_name,R.id.dish_price},user);



        listView1.setAdapter(adapter1);
        listView2.setAdapter(adapter2);
        listView3.setAdapter(adapter3);
        listView4.setAdapter(adapter4);
        mTabLayout.setupWithViewPager(mViewPager);


        listView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                adapter1.getView(arg2, arg1, arg0);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (HashMap<String, Object>)arg0.getItemAtPosition(arg2);
                Intent intent = new Intent();
                intent.putExtra("selectedDish", (Dish)map.get("dish"));
                intent.setClass(FoodView.this,FoodDetailed.class);
                startActivity(intent);
                //Toast.makeText(FoodView.this, "您点击了第"+map.get("name").toString()+"个列表项，内容为："+map.get("price"), Toast.LENGTH_LONG).show();
            }
        });
        listView2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                adapter2.getView(arg2, arg1, arg0);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (HashMap<String, Object>)arg0.getItemAtPosition(arg2);
                Intent intent = new Intent();
                intent.putExtra("selectedDish", (Dish)map.get("dish"));
                intent.setClass(FoodView.this,FoodDetailed.class);
                startActivity(intent);
                //Toast.makeText(FoodView.this, "您点击了第"+map.get("name").toString()+"个列表项，内容为："+map.get("price"), Toast.LENGTH_LONG).show();
            }
        });
        listView3.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                adapter3.getView(arg2, arg1, arg0);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (HashMap<String, Object>)arg0.getItemAtPosition(arg2);
                Intent intent = new Intent();
                intent.putExtra("selectedDish", (Dish)map.get("dish"));
                intent.setClass(FoodView.this,FoodDetailed.class);
                startActivity(intent);
                //Toast.makeText(FoodView.this, "您点击了第"+map.get("name").toString()+"个列表项，内容为："+map.get("price"), Toast.LENGTH_LONG).show();
            }
        });
        listView4.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                adapter4.getView(arg2, arg1, arg0);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (HashMap<String, Object>)arg0.getItemAtPosition(arg2);
                Intent intent = new Intent();
                intent.putExtra("selectedDish", (Dish)map.get("dish"));
                intent.setClass(FoodView.this,FoodDetailed.class);
                startActivity(intent);
                //Toast.makeText(FoodView.this, "您点击了第"+map.get("name").toString()+"个列表项，内容为："+map.get("price"), Toast.LENGTH_LONG).show();
            }
        });


    }

    //填充菜品
    private void LoadDishes(List<Dish> dishList) {
        int num=20;
        String[] name={"话梅花生","鸡汤豆腐","三叶香拌豆劲","糖醋鱼","红烧肉","糖醋排骨","蛏子","皮皮虾","大闸蟹","啤酒","雪碧","可乐"};
        ArrayList<Image> list = new ArrayList<>();
        int[] imgs = { R.drawable.u1, R.drawable.u2, R.drawable.u3,R.drawable.u4,R.drawable.u5,R.drawable.u6,R.drawable.u7,R.drawable.u8,R.drawable.u9,R.drawable.u10,R.drawable.u11,R.drawable.u12};

        int[] price={10,12,8,35,46,45,60,65,80,6,10,9};
        for(int i=0;i<3;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(0);
            d.setImageurl(imgs[i]);
            d.setChoosed(false);
            dishList.add(d);


        }
        for(int i=3;i<6;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(1);
            d.setImageurl(imgs[i]);
            d.setChoosed(false);
            dishList.add(d);
        }
        for(int i=6;i<9;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(2);
            d.setImageurl(imgs[i]);
            d.setChoosed(false);
            dishList.add(d);
        }
        for(int i=9;i<12;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(3);
            d.setImageurl(imgs[i]);
            d.setChoosed(false);
            dishList.add(d);
        }
        mApplication.setDishList(dishList);
    }

    private List<Map<String, Object>> InitData(List<Dish> dishList, int catgory) {
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for(int i = 0; i<dishList.size(); i++){
            Dish d = dishList.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            if(d.getCatgory()==catgory) {
                map.put("dish", d);
                map.put("name", d.getName());
                map.put("price", d.getPrice());
                map.put("catgory",d.getCatgory());
                items.add(map);
            }
        }
        return items;
    }
}
