package com.geniatech.client_box;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class BootupReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i("","-------------------boxService bootup receive.-------------------------");
    	//Intent intt = new Intent(context,BoxActivity.class);
    	Intent intt = new Intent(context,TmpActivity.class);
    	intt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intt);
    	
    	Intent inttt = new Intent();
    	inttt.setComponent(new ComponentName( "com.geniatech.client_box","com.geniatech.client_box.BoxService"));
    	//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(inttt);
    }
}