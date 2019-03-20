package rdm.restartscheduler;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "TEST";
    public static final String CHANNEL_ID2 = "TEST2";
    public static final String CHANNEL_ID3 = "TEST3";


    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel servicechannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Main Foreground Service",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel servicechannel2 = new NotificationChannel(
                    CHANNEL_ID2,
                    "On reset channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationChannel servicechannel3 = new NotificationChannel(
                    CHANNEL_ID3,
                    "Alerts while dozing",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(servicechannel);
            manager.createNotificationChannel(servicechannel2);
            manager.createNotificationChannel(servicechannel3);
        }
    }
}
