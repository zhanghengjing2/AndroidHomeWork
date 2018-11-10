package es.source.code.service;


import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import es.source.code.activity.MyApplication;
import es.source.code.model.Dish;
import es.source.code.model.DishList;
import es.source.code.observer.SimpleObserver;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class ServerObserverService extends Service {

    Context mContext;//上下文

    private boolean run = false;

    private  DishList foodes ;

    //被观察者
    Observable<DishList> dishObservable;

    //一次性
    Disposable disposable;

    private Handler cMessageHandler = new CMessageHandler();

    //Messenger管理
    public Messenger sMessenger = new Messenger(cMessageHandler);
    public Messenger cMessenger;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //this.foodes = (DishList) intent.getSerializableExtra("FullFishList");
        return sMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

    public boolean isRun(){ return run;}
    public void setRun(boolean run){ this.run=run;}

    //内部类
    private class CMessageHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("DataFromClient",String.valueOf(msg.what));
            switch (msg.what)
            {
                case 1:
                    cMessenger = msg.replyTo;
                    setRun(true);


                    if(dishObservable == null)
                        dishObservable=getDishObservable();
                    if (disposable == null) {
                        dishObservable.subscribe(foodObserver);
                    }
                    break;
                case 0:
                    setRun(false);
                    break;
            }
        }
    }

    private Observable<DishList> getDishObservable() {

        return Observable.interval(300, TimeUnit.MILLISECONDS)
                .filter(new Predicate<Long>() {
            @Override
            public boolean test(Long aLong) throws Exception {
                return isRun()&&aLong%5==0;
            }
        }).map(new Function<Long, DishList>() {
                    @Override
                    public DishList apply(Long run) throws Exception {
                        Log.d("取数据环节", "取到数据");
                        foodes=MyApplication.getInstance().getFullListFromJson();
                        /*
                        for(int i=0;i<foodes.getColdFoodList().size();i++)
                        {
                            Dish food = foodes.getColdFoodList().get(i);
                            Log.d("foodListService", food.getName()+","+String.valueOf(food.getDishcount()));
                        }
                        */
                        Log.d("取数据环节", "修改数据 start");
                        setFoodStoreAmount(foodes.getColdFoodList());
                        setFoodStoreAmount(foodes.getHotFoodList());
                        setFoodStoreAmount(foodes.getSeaFoodList());
                        setFoodStoreAmount(foodes.getDrinkingList());
                        Log.d("取数据环节", "修改数据 end");
                        return foodes;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    FoodObserver foodObserver = new FoodObserver();

    private class FoodObserver extends SimpleObserver<DishList> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            super.onSubscribe(d);
            disposable = d;
        }

        @Override
        public void onEvent(DishList foodCollection) {
            //Log.d("ToClient", String.valueOf(isProcessRunning(mContext, "es.source.code.activity")));
            if (isProcessRunning(mContext, "es.source.code.activity") && cMessenger != null) {
                // 发送消息到Activity
                try {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("fullDishlist", foodCollection);
                    for(int i=0;i<foodCollection.getColdFoodList().size();i++)
                    {
                        Dish food = foodCollection.getColdFoodList().get(i);
                        Log.d("foodListService", food.getName()+","+String.valueOf(food.getDishcount()));
                    }
                    Message message = Message.obtain(null, 10);
                    Log.d("发往客户端", String.valueOf(message.what));
                    message.setData(bundle);
                    cMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    //description: 模拟生成库存
    private void setFoodStoreAmount(List<Dish> foodList) {
        for (Dish food : foodList) {
            food.setDishcount((int) (Math.random() * 20));
            //Log.d("foodData", food.getName()+","+String.valueOf(food.getDishcount()));
        }
    }


     // description: 检测进程是否在运行
    public static boolean isProcessRunning(Context mContext, String processName) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> processInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processInfoList) {
            if (processName.equals(info.processName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }

}
