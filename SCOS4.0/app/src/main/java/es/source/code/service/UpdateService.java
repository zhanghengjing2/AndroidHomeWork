package es.source.code.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.source.code.Utils.CommonUtils;
import es.source.code.Utils.Constant;
import es.source.code.activity.FoodDetailed;
import es.source.code.activity.MainScreen;
import es.source.code.activity.MyApplication;
import es.source.code.activity.R;
import es.source.code.model.Dish;
import es.source.code.model.DishList;
import es.source.code.model.ResultJson;
import es.source.code.model.ResultXml;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateService extends IntentService {

    private static final String TAG = "UpdateService";

    // 请求flag
    private static final int FLAG_REQUEST = 100;
    // 删除通知flag
    private static final int FLAG_CLEAN = 101;

    private static final int NOTIFICATION_ID = 5445;

    private static final int TYPE_JSON = 1;
    private static final int TYPE_XML = 2;
    private static int Type;

    private List<Dish> foodlist = new ArrayList<>();

    private Context mContext;


    public UpdateService(){
        super("UpdateService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");

        mContext = this;
        // 这里修改类型
        Type = TYPE_JSON;
        //Type = TYPE_XML;
        //getFoodCollection();
        //foodlist= MyApplication.getInstance().getListFromJson();
        getServerUpdate();
    }

    @SuppressLint("CheckResult")
    public void getServerUpdate() {
        Observable.create(new ObservableOnSubscribe< List<Dish>   >() {
            @Override
            public void subscribe(ObservableEmitter<List<Dish>> e) throws Exception {
                if(Type==TYPE_JSON)
                {
                    // json
                    InputStream resultStream = CommonUtils.requestGet(null, Constant.URL.FOOD, "application/json");
                    if (resultStream == null) {
                        // 取消业务
                        e.onComplete();
                    }
                    String resultString = CommonUtils.streamToString(resultStream);
                    Log.d("UpdateServiceRequrst", "request = " + resultString);
                    ResultJson resultJson = new Gson().fromJson(resultString, ResultJson.class);
                    String foodString = resultJson.getData();
                    java.lang.reflect.Type type = new TypeToken<ArrayList<Dish>>() {
                    }.getType();

                    // 解析列表统计时间
                    Date startDate = new Date(System.currentTimeMillis());
                    List<Dish> foodList = new Gson().fromJson(foodString, type);
                    Date endDate = new Date(System.currentTimeMillis());
                    long duration = endDate.getTime() - startDate.getTime();
                    Log.d("JsonTime", "Json test parse time = " + String.valueOf(duration) + "ms , size = " + String
                            .valueOf(foodList.size()));
                    e.onNext(foodList);
                }else if(Type==TYPE_XML)
                {
                    // xml
                    InputStream resultStream = CommonUtils.requestGet(null, Constant.URL.FOOD, "text/xml");
                    if (resultStream == null) {
                        // 取消业务
                        e.onComplete();
                    }

                    // 解析列表统计时间
                    Date startDate = new Date(System.currentTimeMillis());
                    ResultXml resultXml = CommonUtils.getResultFromXml(CommonUtils.streamToXml(resultStream));
                    Date endDate = new Date(System.currentTimeMillis());
                    long duration = endDate.getTime() - startDate.getTime();
                    Log.d("XMLTime", "XML test parse time = " + String.valueOf(duration) + "ms , size = " + String
                            .valueOf(resultXml.getDataList().size()));
                    for(Dish dish :resultXml.getDataList())
                        Log.i("XMLDataDish",dish.getName()+" "+dish.getPrice());
                    e.onNext(resultXml.getDataList());
                }

                /*
                foodlist= MyApplication.getInstance().getDishList();
                for(Dish food:foodlist)
                    Log.i(TAG,food.getName());
                e.onNext(foodlist);
                */
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Dish>>() {
            @Override
            public void accept(List<Dish> dishes) throws Exception {
                // 构造打开首页的PendingIntent
                Intent intent = new Intent(mContext, MainScreen.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent
                        .FLAG_CANCEL_CURRENT);
                // 发送状态栏通知
                sendServerNotification(dishes, pendingIntent);
                // 播放提示音
                playNotification();
            }
        });
    }


    //description:  调用MediaPlayer播放消息通知
    private void playNotification() {
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, ringtone);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }




     //description: 发通知
    private void sendNotification(Dish food, PendingIntent intent) {
        String price = String.valueOf(food.getPrice());
        //String content = getString(R.string.notify_new_food_content, food.getName(), price);
        String catgory=null;
        switch (food.getCatgory())
        {
            case 0:catgory="冷菜";break;
            case 1:catgory="热菜";break;
            case 2:catgory="海鲜";break;
            case 3:catgory="饮料";break;
        }
        String content = "新品上架:菜名:"+food.getName()+",价格:"+food.getPrice()+",类型:"+catgory;
        Bitmap foodBitMap = BitmapFactory.decodeResource(getResources(), food.getImageurl(), new BitmapFactory.Options());

        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // 设置图标和食物图片、标题、内容
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(foodBitMap)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)

                //设置intent、点击自动消除
                .setContentIntent(intent)
                .setAutoCancel(true);
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(10, builder.build());
    }



    //description: 发服务器通知
    private void sendServerNotification(List<Dish> foodList, PendingIntent intent) {
        String content = getString(R.string.notify_new_food_title) + getString(R.string
                .notify_new_food_content_server, foodList.size());

        Intent serviceIntent = new Intent("scos.intent.action.CLOSE_NOTIFICATION");
        serviceIntent.putExtra("notification_id", NOTIFICATION_ID);
        PendingIntent closeIntent = PendingIntent.getBroadcast(mContext, FLAG_CLEAN, serviceIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);

        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // 设置图标和食物图片、标题、内容
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .addAction(R.drawable.ic_clean_blue, "清除", closeIntent)
                //设置intent、点击自动消除
                .setContentIntent(intent)
                .setAutoCancel(true);
        //通过builder.build()方法生成Notification对象，设置提示音，并发送通知,id=1
        Notification notification = builder.build();
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notifyManager.notify(NOTIFICATION_ID, builder.build());
    }


    //description: 模拟获取更新数据
    private void getFoodCollection() {
        DishList foodCollection = MyApplication.getInstance().getFullListFromJson();
        List<Dish> hotFoodList = foodCollection.getHotFoodList();

        int position = (int) (Math.random() * (hotFoodList.size() - 1));
        List<Dish> newFoodList = new ArrayList<>();
        newFoodList.add(hotFoodList.get(position));
        Intent intent = new Intent(this, FoodDetailed.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedDish", hotFoodList.get(position));
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        sendNotification(hotFoodList.get(position), pendingIntent);
    }

}
