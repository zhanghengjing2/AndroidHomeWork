package es.source.code.activity;


import android.app.Application;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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
    private TextView dish_count, dish_sumPrice;
    private MyApplication mApplication;
    private User user;
    private ProgressDialog pd ;
    private int money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_order_view);

        //初始化部件
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        dish_count = (TextView) findViewById(R.id.dish_count);
        dish_sumPrice = (TextView) findViewById(R.id.dish_countprice);

        //获取用户
        mApplication = (MyApplication) getApplication();
        user = mApplication.getLoginUser();

        //使用Tablayout的newTab()创建tab,设置tab项显示的文字
        final String[] mTabNames = new String[]{"未下单菜", "已下单菜"};

        //设置点菜总数
        dish_count.setText(String.valueOf(user.getOrdered().size()));
        //计算总金额
        money = CalculateMoney(user.getOrdered());
        dish_sumPrice.setText(String.valueOf(money));

        //定义一个视图集合（用来装左右滑动的页面视图）
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
                name = mTabNames[position].toString();
                return mTabNames[position];
            }

        });

        try {
            String name = getIntent().getStringExtra("showPage");
            //Toast.makeText(FoodOrderView.this, name, Toast.LENGTH_SHORT).show();
            if (name.equals("已下单菜")) {
                mViewPager.setCurrentItem(1);
            }
            if (name.equals("未下单菜")) {
                mViewPager.setCurrentItem(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ListView listView1 = (ListView) view1.findViewById(R.id.listview);
        ListView listView2 = (ListView) view2.findViewById(R.id.listview);


        //这里我们传入数据
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < user.getOrdered().size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            Dish d = user.getOrdered().get(i);
            map.put("name", d.getName());
            map.put("price", d.getPrice());
            map.put("count", d.getCount());
            items.add(map);
        }

        final orderdishAdapter adapter = new orderdishAdapter(FoodOrderView.this, items, R.layout.dish_items, new String[]{"name", "price", "count"}, new int[]{R.id.order_dish_name, R.id.order_dish_price, R.id.order_dish_count});


        //为ListView设置适配器
        listView1.setAdapter(adapter);
        listView2.setAdapter(adapter);


        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("未下单菜"))
                    btn_submit.setText("提交");
                else
                    btn_submit.setText("结账");
                //Toast.makeText(FoodOrderView.this, tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_submit.getText().equals("结账") && user != null) {
                    if (user.getOldUser()) {
                        Toast.makeText(FoodOrderView.this, "您好，老顾客，本次你可享受7折优惠", Toast.LENGTH_SHORT).show();
                        new MyAsyncTask().execute();
                    }
                }
            }
        });
    }

    private int CalculateMoney(List<Dish> ordered) {
        int m = 0;
        for (int i = 0; i < ordered.size(); i++) {
            m = m + ordered.get(i).getPrice();
        }
        return m;
    }

    class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            int initial = 0;//初始进度
            //设置循环条件
            for(int i=0;i<150;i++)
            {
                pd.setProgress(initial += 2);//设置每次完成5
                //Toast.makeText(FoodOrderView.this, "执行中", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            pd.dismiss();//进度完成时对话框消失
            btn_submit.setEnabled(false);
            Toast.makeText(FoodOrderView.this, "本次消费金额为:"+money+"元，积分为:"+money+"分.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(FoodOrderView.this, "执行前", Toast.LENGTH_SHORT).show();

            //此处初始化进度条对话框.
            pd = new ProgressDialog(FoodOrderView.this);
            pd.setTitle("请稍等");
            //设置对话进度条样式为水平
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //设置提示信息
            pd.setMessage("正在结账......");
            //设置对话进度条显示在屏幕顶部（方便截图）
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.setMax(300);
            pd.show();//调用show方法显示进度条对话框

        }
    }
}