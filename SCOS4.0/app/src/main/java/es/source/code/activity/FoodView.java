package es.source.code.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.Adapter.listviewAdapter;
import es.source.code.model.Dish;
import es.source.code.model.DishList;
import es.source.code.model.ServerData;
import es.source.code.model.User;
import es.source.code.observer.SimpleObserver;
import es.source.code.service.ServerObserverService;
import es.source.code.service.ServerObserverServiceEventBus;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FoodView extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Menu menu;//ActionBar

    private List<Dish> dishList = new ArrayList<>();//菜品列表
    private DishList dl = new DishList();

    private User user;
    private static MyApplication mApplication;
    private List<ServerData> datalist;

    private DishList foodCollection;

    private  listviewAdapter adapter1=null,adapter2=null,adapter3=null,adapter4=null;
    private List<Map<String, Object>> items1,items2,items3,items4;

    //Messenger管理
    Messenger cMessenger;
    Messenger sMessenger;

    SMessageHandle sMessageHandler = new SMessageHandle(FoodView.this);
    //来自服务端消息的处理
    private class SMessageHandle extends Handler{
        WeakReference<FoodView> foodViewWeakReference;
        SMessageHandle(FoodView foodView) {
            foodViewWeakReference = new WeakReference<>(foodView);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FoodView foodView = foodViewWeakReference.get();
            switch (msg.what)
            {
                case 10:
                    Bundle bundle = new Bundle();
                    DishList foodCollection1 = (DishList) msg.getData().getSerializable("fullDishlist");
                    Log.i("ClientData", "返回数据成功");
                    foodView.ReceiveData(foodCollection1);
                    break;
            }
        }
    }

    //建立连接
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("Connection", "连接建立");
            sMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Connection", "连接断开");

        }
    };
    public FoodView() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu=menu;
        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                 // Handle action bar item clicks here. The action bar will
                 // automatically handle clicks on the Home/Up button, so long
                 // as you specify a parent activity in AndroidManifest.xml.
                 Intent intent = new Intent();
                 switch (item.getItemId()) {
                     case R.id.dish_order:
                             user = (User)getIntent().getSerializableExtra("user");
                             intent.putExtra("user",user);
                             intent.putExtra("showPage","未下单菜");
                             intent.setClass(FoodView.this, FoodOrderView.class);
                             startActivity(intent);
                             //Toast.makeText(this, "已点菜品", Toast.LENGTH_SHORT).show();
                             break;
                     case R.id.dish_show:
                             user = (User)getIntent().getSerializableExtra("user");
                             intent.putExtra("user",user);
                             intent.putExtra("showPage","已下单菜");
                             intent.setClass(FoodView.this, FoodOrderView.class);
                             startActivity(intent);
                             //Toast.makeText(this, "查看订单", Toast.LENGTH_SHORT).show();
                             break;
                     case R.id.dish_help:
                         Toast.makeText(this, "呼叫服务", Toast.LENGTH_SHORT).show();
                         break;

                     case R.id.dish_update:
                         handleRefreshClick();
                         break;
                     default:
                             break;
                     }
                 return super.onOptionsItemSelected(item);
             }


    @Override
    protected void onStart() {
        super.onStart();
        initBinder();//与Service建立连接

    }


    //与Service建立连接
    private void initBinder() {
        cMessenger = new Messenger(sMessageHandler);
        Intent service = new Intent(getApplicationContext(),ServerObserverService.class);
        service.putExtra("FullFishList",dl);
        bindService(service,connection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);//断开连接
    }

    //处理自动刷新按钮
    private void  handleRefreshClick() {
        MenuItem item = menu.getItem(3);//获取第三个ActionBar
        String title = item.getTitle().toString();//获取按钮文本
        int what;

        if(title.equals("启动实时更新"))
        {
            //改变标题名
            title="停止实时更新";
            //what值设为1
            what=1;
        }else {
            //改变标题名
            title="启动实时更新";
            //what值设为0
            what=0;
        }
        try {
            //发送消息
            Message message = Message.obtain();
            message.what=what;
            message.replyTo=cMessenger;
            sMessenger.send(message);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        //更改按钮名称
        item.setTitle(title);

    }


    protected  int FindCount(String name,List<ServerData> datalist)
    {
        int count=0;

        for(int i=0;i<datalist.size();i++)
        {
            ServerData s = datalist.get(i);
            if(s.getName().equals(name))
            {
                count = s.getNumber();
            }
        }

        return  count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);

        initView();
        Intent intent=getIntent();
        // 实例化一个Bundle
        Bundle bundle=intent.getExtras();
        //获取里面的Persion里面的数据
        foodCollection= (DishList) bundle.getSerializable("fullDishlist");
        final DishList collection = foodCollection;
        //ReceiveData(collection);

        //启动Service
        //Intent intentOne = new Intent(this, ServerObserverServiceEventBus.class);
        //startService(intentOne);
    }

    @SuppressLint("CheckResult")
    private void ReceiveData(final DishList foodCollection) {
        Observable.create(new ObservableOnSubscribe<DishList>() {

            @Override
            public void subscribe(ObservableEmitter<DishList> e) throws Exception {
                e.onNext(foodCollection);
            }
        }).map(new Function<DishList, DishList>() {

            @Override
            public DishList apply(DishList dishList) throws Exception {
                setFoodStatus(foodCollection.getColdFoodList());
                setFoodStatus(foodCollection.getHotFoodList());
                setFoodStatus(foodCollection.getSeaFoodList());
                setFoodStatus(foodCollection.getDrinkingList());
                return foodCollection;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleObserver<DishList>() {
            @Override
            public void onEvent(DishList foodCollection) {
                refreshFragmentData(foodCollection);
            }
        });
    }

    private void refreshFragmentData(DishList foodCollection) {
        Log.i("update","开始更新");
        List<Dish> cold = foodCollection.getColdFoodList();
        for(int i=0;i<cold.size();i++)
        {
            Dish d= cold.get(i);
            HashMap<String,Integer> hp = (HashMap<String, Integer>) adapter1.getItem(i);
            hp.put("dish_count",d.getDishcount());
            adapter1.notifyDataSetChanged();
        }
        List<Dish> hot = foodCollection.getHotFoodList();
        for(int i=0;i<hot.size();i++)
        {
            Dish d= hot.get(i);
            HashMap<String,Integer> hp = (HashMap<String, Integer>) adapter2.getItem(i);
            hp.put("dish_count",d.getDishcount());
            adapter2.notifyDataSetChanged();
        }
        List<Dish> sea = foodCollection.getSeaFoodList();
        for(int i=0;i<sea.size();i++)
        {
            Dish d= sea.get(i);
            HashMap<String,Integer> hp = (HashMap<String, Integer>) adapter3.getItem(i);
            hp.put("dish_count",d.getDishcount());
            adapter3.notifyDataSetChanged();
        }
        List<Dish> drink = foodCollection.getDrinkingList();
        for(int i=0;i<drink.size();i++)
        {
            Dish d= drink.get(i);
            HashMap<String,Integer> hp = (HashMap<String, Integer>) adapter4.getItem(i);
            hp.put("dish_count",d.getDishcount());
            adapter4.notifyDataSetChanged();
        }
    }

    private void initView() {
        //获取用户
        mApplication=(MyApplication)getApplication();
        user=mApplication.getLoginUser();
        //填充菜品
        //LoadDishes(dishList);
        List<Dish> colddish=MyApplication.getInstance().getFullListFromJson().getColdFoodList();
        List<Dish> hotdish=MyApplication.getInstance().getFullListFromJson().getHotFoodList();
        List<Dish> seadish=MyApplication.getInstance().getFullListFromJson().getSeaFoodList();
        List<Dish> drinkdish=MyApplication.getInstance().getFullListFromJson().getDrinkingList();

        //初始化部件
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //使用Tablayout的newTab()创建tab,设置tab项显示的文字
        final String[] mTabNames=new String[]{"冷菜","热菜","海鲜","酒水"};



        //定义一个视图集合（用来装左右滑动的页面视图）
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
        //为viewPager添加监听事件


        ListView listView1 = (ListView) view1.findViewById(R.id.listview);
        ListView listView2 = (ListView) view2.findViewById(R.id.listview);
        ListView listView3 = (ListView) view3.findViewById(R.id.listview);
        ListView listView4 = (ListView) view4.findViewById(R.id.listview);

        List<Map<String, Object>> items1 = InitData(colddish,0);
        List<Map<String, Object>> items2 = InitData(hotdish,1);
        List<Map<String, Object>> items3 = InitData(seadish,2);
        List<Map<String, Object>> items4 = InitData(drinkdish,3);


        adapter1 = new listviewAdapter(FoodView.this, items1,R.layout.items, new String[]{"name","price","dish_count"}, new int[]{R.id.dish_name,R.id.dish_price,R.id.dish_number},user);
        listView1.setAdapter(adapter1);

        adapter2 = new listviewAdapter(FoodView.this, items2,R.layout.items, new String[]{"name","price","dish_count"}, new int[]{R.id.dish_name,R.id.dish_price,R.id.dish_number},user);
        adapter3 = new listviewAdapter(FoodView.this, items3,R.layout.items, new String[]{"name","price","dish_count"}, new int[]{R.id.dish_name,R.id.dish_price,R.id.dish_number},user);
        adapter4 = new listviewAdapter(FoodView.this, items4,R.layout.items, new String[]{"name","price","dish_count"}, new int[]{R.id.dish_name,R.id.dish_price,R.id.dish_number},user);




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

    /*
    //填充菜品
    private void LoadDishes(List<Dish> dishList) {
        int num=20;
        String[] name={"话梅花生","鸡汤豆腐","三叶香拌豆劲","糖醋鱼","红烧肉","糖醋排骨","蛏子","皮皮虾","大闸蟹","啤酒","雪碧","可乐"};
        ArrayList<Image> list = new ArrayList<>();
        int[] imgs = { R.drawable.u1, R.drawable.u2, R.drawable.u3,R.drawable.u4,R.drawable.u5,R.drawable.u6,R.drawable.u7,R.drawable.u8,R.drawable.u9,R.drawable.u10,R.drawable.u11,R.drawable.u12};

        int[] price={10,12,8,35,46,45,60,65,80,6,10,9};
        List<Dish> cold  = new ArrayList<>();
        List<Dish> hot  = new ArrayList<>();
        List<Dish> sea  = new ArrayList<>();
        List<Dish> drink  = new ArrayList<>();

        for(int i=0;i<3;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(0);
            d.setImageurl(String.valueOf(imgs[i]));
            d.setChoosed(false);
            dishList.add(d);
            cold.add(d);
        }
        for(int i=3;i<6;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(1);
            d.setImageurl(String.valueOf(imgs[i]));
            d.setChoosed(false);
            dishList.add(d);
            hot.add(d);
        }
        for(int i=6;i<9;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(2);
            d.setImageurl(String.valueOf(imgs[i]));
            d.setChoosed(false);
            dishList.add(d);
            sea.add(d);
        }
        for(int i=9;i<12;i++)
        {
            Dish d = new Dish();
            d.setName(name[i]);
            d.setPrice(price[i]);
            d.setCatgory(3);
            d.setImageurl(String.valueOf(imgs[i]));
            d.setChoosed(false);
            dishList.add(d);
            drink.add(d);
        }
        dl.setColdFoodList(cold);
        dl.setHotFoodList(hot);
        dl.setSeaFoodList(sea);
        dl.setDrinkingList(drink);

        mApplication.setDishList(dishList);
        mApplication.setFullDishlist(dl);

    }*/

    private List<Map<String, Object>> InitData(List<Dish> dishList, int catgory) {
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for(int i = 0; i<dishList.size(); i++){
            Dish d = dishList.get(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("dish", d);
                map.put("name", d.getName());
                map.put("price", d.getPrice());
                map.put("catgory",d.getCatgory());
                map.put("dish_count",d.getDishcount());
                //Log.i("JSONDATA","name"+d.getName()+",url:"+d.getImageurl()+",price"+d.getPrice());
                items.add(map);
        }
        return items;
    }

    //description: 设置食物点击状态
    private void setFoodStatus(List<Dish> originFoodList){
        for (Dish orderFood :dishList){
            for(Dish originFood : originFoodList){
                if(originFood.getName().equals(orderFood.getName())){
                    originFood.setOrder(true);
                    break;
                }
            }
        }
    }
}
