package rdm.restartscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


public class RestartServiceReceiver extends BroadcastReceiver {
    //This receiver is for restarting the service if anything destroys the first service


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, testService.class));
        } else {
            context.startService(new Intent(context, testService.class));
        }
    }
}
