package appnet.diamond;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;


public class DiamondService extends Service {
    private NotificationManager mNM;
    private Intent i;
    private DiamondActivity diamondGame;
    Bundle savedInstanceState;
    
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public DiamondService getService() {
            return DiamondService.this;
        }
    }
    
    @Override
    public void onCreate() {    	
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        diamondGame.onCreate(savedInstanceState);
    }
    
    public void startGame(){
//    	i=new Intent(DiamondService.this,DiamondActivity.class);
//        startActivity(i);
//    	diamondGame.startNewGame();
//    	diamondGame.onStart();
    }
    
    @Override
    public IBinder onBind(Intent intent) {    	    	
        return mBinder;
    }
   
}