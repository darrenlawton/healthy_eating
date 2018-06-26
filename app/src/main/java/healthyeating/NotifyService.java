package healthyeating;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by darren on 4/15/17.
 */

public class NotifyService extends Service {

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        NotificationManager notManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent notIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent notPending = PendingIntent.getActivity(this,0,notIntent,0);

        Notification notNofity = new Notification.Builder(this)
                .setContentTitle("Daily Health Log")
                .setContentText("You need to log your daily outcome")
                .setContentIntent(notPending)
                .addAction(0,"Load App",notPending)
                .build();

        notManager.notify(1,notNofity);
    }
}
