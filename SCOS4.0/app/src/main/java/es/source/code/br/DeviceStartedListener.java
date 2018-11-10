package es.source.code.br;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.source.code.service.UpdateService;

public class DeviceStartedListener extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG,intent.getAction());
        String action = intent.getAction().toString();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {//开机启动完成后，要做的事情 
            Log.i(TAG,"BootBroadcastReceiver onReceive(), Do thing!");
            Intent serviceIntent = new Intent(context, UpdateService.class);
            context.startService(serviceIntent);
        }else if (intent.getAction().toString().equals("scos.intent.action.CLOSE_NOTIFICATION")) {
            Log.i("cancel","Cancel it");
            NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context
                    .NOTIFICATION_SERVICE);
            notifyManager.cancel(intent.getIntExtra("notification_id", 0));
        }

    }
}
