package rdm.restartscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class TimeChangedReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //this receiver is for if the user changes the time on the phone

        /***** For start Service  ****/
        Intent myIntent = new Intent(context, testService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, testService.class));
        } else {
            context.startService(new Intent(context, testService.class));
        }


    }
}
