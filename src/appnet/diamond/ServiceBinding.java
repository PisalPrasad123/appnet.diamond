package appnet.diamond;

import appnet.diamond.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ServiceBinding extends Activity{
	private DiamondService mBoundService;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        bindService(new Intent(ServiceBinding.this, 
                DiamondService.class), mConnection, Context.BIND_AUTO_CREATE);
        
//        ImageButton btnGameStatus = (ImageButton) findViewById(R.id.gameStatus);
//		btnGameStatus.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View view)
//			{
				 mBoundService.startGame();
//			}
//		});
		
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((DiamondService.LocalBinder)service).getService();            
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

}
