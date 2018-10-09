package es.source.code.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;


public class SCOSEntry extends AppCompatActivity {

    //本文来自 techsd 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/techsd/article/details/27086201?utm_source=copy
    private GestureDetector gestureDetector; 					//手势检测
    private OnGestureListener onSlideGestureListener = null;	//左右滑动手势检测监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);




        //左右滑动手势监听器
        onSlideGestureListener = new OnSlideGestureListener();
        gestureDetector = new GestureDetector(this, onSlideGestureListener);


    }
    //将touch动作事件交由手势检测监听器来处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    /*********************************************
     * 左右滑动手势监听器
     ********************************************/
    private class OnSlideGestureListener implements OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // 参数解释：
            // e1：第1个ACTION_DOWN MotionEvent
            // e2：最后一个ACTION_MOVE MotionEvent
            // velocityX：X轴上的移动速度，像素/秒
            // velocityY：Y轴上的移动速度，像素/秒
            // 触发条件 ：
            // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
            if ((e1 == null) || (e2 == null)){
                return false;
            }
            int FLING_MIN_DISTANCE = 100;
            int FLING_MIN_VELOCITY = 100;
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE )//&& Math.abs(velocityX) > FLING_MIN_VELOCITY)
            {
                // 向左滑动

                Intent intent = new Intent();
                intent.putExtra("id","FromEntry");
                intent.setClass(SCOSEntry.this, MainScreen.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);	//不重复打开多个界面
                startActivity(intent);
                //overridePendingTransition(R.anim.move_left_in, R.anim.move_right_out);
            }
            else
            {
                Intent intent = new Intent();
                intent.putExtra("id","Other");
                intent.setClass(SCOSEntry.this, MainScreen.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);	//不重复打开多个界面
                startActivity(intent);
            }

            return false;
        }
    }
}
