package es.source.code.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.source.code.activity.FoodView;

import es.source.code.activity.MyApplication;
import es.source.code.activity.R;
import es.source.code.model.Dish;
import es.source.code.model.User;

public class listviewAdapter extends SimpleAdapter {

    private static Context mContext;
    private List<? extends Map<String, ?>> items;
    private User user;
    private List<Dish> dishList;
    private Dish dish=new Dish();

    public listviewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, User u)
    {
        super(context, data, resource, from, to);
        mContext=context;
        items=data;
        user=u;
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
            final int mPosition = position;
            // TODO Auto-generated method stub
            View v = super.getView(position, convertView, parent);
            // 获取索引
            final TextView text = (TextView) v.findViewById(R.id.textView2);

            // 定义一个文本框并关联其ID
            text.setTag(position);
            // 以position作为索引，这样就能具体定位到某个列表项文本框
            final Button btn = (Button) v.findViewById(R.id.button1);
            // 定义一个文本框并关联其ID
            btn.setTag(position);
            //获取菜
            dish = (Dish)items.get(mPosition).get("dish");
            //Toast.makeText(mContext,"result:"+ dish.isChoosed(), Toast.LENGTH_SHORT).show();
            //设置按钮的文字
            if(!dish.isChoosed())
                btn.setText("点菜");
            else
                btn.setText("退点");
            // 以position作为索引，这样就能具体定位到是哪个列表项按钮
            btn.setOnClickListener(new android.view.View.OnClickListener() {
                // 添加事件监听器
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    if(btn.getText().toString().equals("点菜")&&!dish.isChoosed())
                    {
                        dish.setChoosed(true);
                        dish.addCount();
                        user.addOrdered(dish);
                        btn.setText("退点");
                        //Toast.makeText(mContext, "菜名:"+dish.getName()+",价格:"+dish.getPrice(), Toast.LENGTH_SHORT).show();

                    }else if(btn.getText().toString().equals("退点")&&dish.isChoosed())
                    {
                        dish.subCount();
                        dish.setChoosed(false);
                        user.removeOrdered(dish);
                        btn.setText("点菜");
                    }
                    Toast.makeText(mContext, "点菜成功", Toast.LENGTH_SHORT).show();
                }
            });
            return v;
    }
}


