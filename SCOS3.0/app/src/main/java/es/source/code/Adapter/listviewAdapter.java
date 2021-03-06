package es.source.code.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.source.code.activity.R;
import es.source.code.model.Dish;
import es.source.code.model.User;

public class listviewAdapter extends SimpleAdapter {

    private static Context mContext;
    private List<Map<String, Object>> items;
    private User user;
    private Dish dish;


    public listviewAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to, User u)
    {
        super(context, data, resource, from, to);
        this.mContext=context;
        this.items=data;
        this.user=u;
        // TODO Auto-generated constructor stub
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

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
            //设置按钮的文字



            //获取菜
            dish = (Dish)items.get(position).get("dish");
            //Toast.makeText(mContext,"result:"+ dish.isChoosed(), Toast.LENGTH_SHORT).show();
            if(!dish.isChoosed())
                    btn.setText("点菜");
            else
                    btn.setText("退点");

            final int mposition = position;
            //Toast.makeText(mContext, "position:"+position, Toast.LENGTH_SHORT).show();

            // 以position作为索引，这样就能具体定位到是哪个列表项按钮
            btn.setOnClickListener(new android.view.View.OnClickListener() {
                // 添加事件监听器
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Dish se_dish = (Dish)items.get(mposition).get("dish");
                    if(btn.getText().toString().equals("点菜"))
                    {
                        se_dish.setChoosed(true);
                        se_dish.addCount();
                        user.addOrdered(se_dish);
                        Toast.makeText(mContext, "点菜成功", Toast.LENGTH_SHORT).show();
                        btn.setText("退点");

                    }else if(btn.getText().toString().equals("退点"))
                    {
                        se_dish.setChoosed(false);
                        se_dish.subCount();
                        user.removeOrdered(se_dish);
                        btn.setText("点菜");
                    }

                }
            });
            return v;
    }
}


