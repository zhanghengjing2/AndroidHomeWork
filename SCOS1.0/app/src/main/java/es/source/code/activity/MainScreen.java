package es.source.code.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.model.User;

public class MainScreen extends AppCompatActivity  {

    private GridView mAppGridView = null;    // 应用图标

    private int[] mAppIcons = {
            R.drawable.ic_order_blue2, R.drawable.ic_menu_blue,
            R.drawable.ic_login_blue, R.drawable.ic_help_blue };    // 应用名
    private String[] mAppNames = { "点菜", "查看订单", "登录/注册","系统帮助"};

    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    private User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen_layout);


        //初始化用户
        user = new User();

        //显示GridView部件
        ShowGridView();


        // 添加列表项被单击的监听器
        mAppGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                switch ((String)listItems.get(position).get("name"))
                {
                    case "点菜":
                        // 显示被单击的图片
                        //Toast.makeText(MainScreen.this, mAppNames[position],Toast.LENGTH_SHORT).show();
                        intent.setClass(MainScreen.this, FoodView.class);
                        startActivity(intent);
                        break;
                    case "查看订单":
                        // 显示被单击的图片
                        //Toast.makeText(MainScreen.this, mAppNames[position],Toast.LENGTH_SHORT).show();
                        intent.setClass(MainScreen.this, FoodOrderView.class);
                        startActivity(intent);
                        break;
                    case "登录/注册":
                        // 显示被单击的图片
                        //Toast.makeText(MainScreen.this, mAppNames[position],Toast.LENGTH_SHORT).show();
                        intent.setClass(MainScreen.this, LoginOrRegister.class);
                        startActivity(intent);
                        break;
                    case "系统帮助":
                        // 显示被单击的图片
                        Toast.makeText(MainScreen.this, mAppNames[position],Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });



    }

    private void ShowGridView() {
        // 获取界面组件
        mAppGridView = (GridView) findViewById(R.id.gridview);
        // 初始化数据，创建一个List对象，List对象的元素是Map



        for (int i = 2; i < mAppIcons.length; i++) {

            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("icon", mAppIcons[i]);
            listItem.put("name",mAppNames[i]);
            listItems.add(listItem);
        }
        try
        {
            String sID=getIntent().getStringExtra("id");
            if(!sID.isEmpty())
            {
                if(sID.equals("FromEntry")||sID.equals("LoginSuccess"))
                {
                    for (int i = 0; i < 2; i++) {

                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("icon", mAppIcons[i]);
                        listItem.put("name",mAppNames[i]);
                        listItems.add(i,listItem);
                    }
                    user = (User)getIntent().getSerializableExtra("login_userId");
                }
                if(sID.equals("RegisterSuccess"))
                {
                    for (int i = 0; i < 2; i++) {

                        Map<String, Object> listItem = new HashMap<String, Object>();
                        listItem.put("icon", mAppIcons[i]);
                        listItem.put("name",mAppNames[i]);
                        listItems.add(i,listItem);
                    }
                    user = (User)getIntent().getSerializableExtra("register_userId");
                    Intent intent = new Intent();
                    intent.putExtra("user",user);
                    intent.setClass(MainScreen.this,FoodView.class);
                    intent.setClass(MainScreen.this,FoodOrderView.class);
                    startActivity(intent);
                    Toast.makeText(MainScreen.this, "欢迎您成为SCOS新用户",Toast.LENGTH_SHORT).show();
                }
                else
                    user=null;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,R.layout.gridview_item,new String[]{"icon", "name"}, new int[]{R.id.icon_img, R.id.name_tv});

        // 为GridView设置Adapter
        mAppGridView.setAdapter(simpleAdapter);
    }


}
