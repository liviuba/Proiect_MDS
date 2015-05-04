package proiectMDS.superMApp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by andrei on 03.05.2015.
 */
public class MusicService extends Service {

    MediaPlayer mp = null;
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.setLooping(true);
        mp.start();
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onCreate(){
        Log.i("MusicService.onCreate()",":sunt aici");
        mp = MediaPlayer.create(this,R.raw.alarm);
    }
}
