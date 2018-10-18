package es.source.code.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.Utils.Constant;
import es.source.code.mail.MailSender;

public class SCOSHelper extends AppCompatActivity {

    private GridView mAppGridView = null;    // 应用图标
    private int[] mAppIcons = {
            R.drawable.ic_profile_blue, R.drawable.ic_system_blue,
            R.drawable.ic_call_blue, R.drawable.ic_message_blue, R.drawable.ic_mail_blue};    // 应用名
    private String[] mAppNames = {"用户使用协议", "关于系统", "电话人工帮助", "短信帮助", "邮件帮助"};


    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
// handler对象，用来接收消息~
     //@SuppressLint("HandlerLeak")
     private  Handler handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {  //这个是发送过来的消息
             switch (msg.what)
             {
                 case Constant.EMAIL_SEND_SUCCESS:
                     Toast.makeText(SCOSHelper.this, "求助邮件已发送成功", Toast.LENGTH_SHORT).show();
             }

         };
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoshelper);

        //初始化组件
        InitView();

        // 添加列表项被单击的监听器
        mAppGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch ((String) listItems.get(position).get("name")) {
                    case "用户使用协议":

                        break;
                    case "关于系统":

                        break;
                    case "电话人工帮助":
                        callPhone(String.valueOf(5554));
                        break;
                    case "短信帮助":
                        sendMessage("5554","test scos helper");
                        break;
                    case "邮件帮助":
                        new Thread(new MailSender("请看这个文本邮件",handler)).start();
                        break;
                }

            }
        });
    }

    private void InitView() {

        // 获取界面组件
        mAppGridView = (GridView) findViewById(R.id.help_gridview);
        // 初始化数据，创建一个List对象，List对象的元素是Map
        for (int i = 0; i < mAppIcons.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("icon", mAppIcons[i]);
            listItem.put("name", mAppNames[i]);
            listItems.add(listItem);
        }
        // 创建一个SimpleAdapter
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.help_item, new String[]{"icon", "name"}, new int[]{R.id.help_item_icon, R.id.help_name});

        // 为GridView设置Adapter
        mAppGridView.setAdapter(simpleAdapter);
    }


    private void callPhone(String phoneNum) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private void sendMessage(String number,String content)
    {
        SmsManager manager = SmsManager.getDefault();
        //短信拆分
        ArrayList<String> arrayList = manager.divideMessage(content);
        for (String text : arrayList) {
            //第四个：是否发送 第五个：是否接受（需要移动网络产生电信号）
            manager.sendTextMessage(number, null, text, null, null);
        }
        Toast.makeText(SCOSHelper.this, "求助短信发送成功", Toast.LENGTH_SHORT).show();
    }






}
