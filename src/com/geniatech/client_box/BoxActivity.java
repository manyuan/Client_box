package com.geniatech.client_box;

import java.util.List;

import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BoxActivity extends Activity {

	private Button mButton;
	private TextView mTextView;
	private IntentFilter mIntentFilter;
	private WifiManager mWifiManager;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiStateChanged(intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState)
                        intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                handleStateChanged(((NetworkInfo) intent.getParcelableExtra(
                        WifiManager.EXTRA_NETWORK_INFO)).getDetailedState());
            }else if(WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)){
            	handleWifiApStateChanged(intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_AP_STATE, WifiManager.WIFI_AP_STATE_FAILED));
            }
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_box);
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		new WifiUtils(mWifiManager);
		
		mTextView = (TextView)findViewById(R.id.textView1);
		
		mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        
		//startService(new Intent(this,BoxService.class));
		
		mButton = (Button)findViewById(R.id.button1);
		mButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mButton.setText(WifiUtils.getIP());
				Log.i("asdfadf","===============removeConfigs======3===-----");
				getwifi();
			}
		});
		
		//new SimpleWeb().ServerStart();
	}

	public void getwifi(){
		WifiConfiguration conf = BoxService.getWifiConfig("ChinaNet-GTL", "qwer123w4", 1);
    	List<WifiConfiguration> conList = mWifiManager.getConfiguredNetworks();
    	for(WifiConfiguration con:conList){
    		mWifiManager.removeNetwork(con.networkId);
    	}
		int networkId = mWifiManager.addNetwork(conf);
        if (networkId != -1) {
            mWifiManager.enableNetwork(networkId, false);
            conf.networkId = networkId;
            connect(networkId);
        }
	}
	private void connect(int networkId) {
        if (networkId == -1) {
            return;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.networkId = networkId;
        //config.priority = 0;
        mWifiManager.updateNetwork(config);
        mWifiManager.saveConfiguration();
        mWifiManager.enableNetwork(networkId, true);
        mWifiManager.reconnect();
    }
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
		mTextView.setText(WifiUtils.getActiveSSID());
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//stopService(new Intent(this,BoxService.class));
	}
	private void handleWifiStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                mTextView.setText(R.string.wifi_starting);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                mTextView.setText(null);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
            	mTextView.setText(R.string.wifi_stopping);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
            	mTextView.setText("已经停止");
                break;
            default:
            	mTextView.setText(R.string.wifi_error);
        }
    }

    private void handleStateChanged(NetworkInfo.DetailedState state) {
        if (state != null) {
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (info != null) {
            	String str = Summary.get(this, info.getSSID(), state);
            	if(str == null || str =="") return;
            	mTextView.setText(str);
            }
        }
    }
    private void handleWifiApStateChanged(int state) {
        switch (state) {
            case WifiManager.WIFI_AP_STATE_ENABLING:
            	mTextView.setText(R.string.wifi_starting);
                break;
            case WifiManager.WIFI_AP_STATE_ENABLED:
            	mTextView.setText("已开启热点:" + WifiUtils.getActiveSSID());
                break;
            case WifiManager.WIFI_AP_STATE_DISABLING:
            	mTextView.setText(R.string.wifi_stopping);
                break;
            case WifiManager.WIFI_AP_STATE_DISABLED:
            	//mTextView.setText("AP disabled.");
                break;
            default:
            	mTextView.setText(R.string.wifi_error);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	return true;
    }
}
