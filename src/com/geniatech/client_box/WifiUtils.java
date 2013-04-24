package com.geniatech.client_box;

import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtils{
	private static WifiManager mWifiManager;
	WifiUtils(WifiManager wifim){
		mWifiManager = wifim;
	}
	public static boolean isWifiConnected(){
		int state = mWifiManager.getWifiState();
		if(state!=WifiManager.WIFI_STATE_ENABLED){
			return false;
		}
		WifiInfo info = mWifiManager.getConnectionInfo();
		if(info.getIpAddress()!=0){
			return true;
		}else{
			return false;
		}
	}
	public static String getActiveSSID(){
		int state = mWifiManager.getWifiApState();
		if(state == WifiManager.WIFI_AP_STATE_ENABLED){
			WifiConfiguration conf = mWifiManager.getWifiApConfiguration();
			if(conf != null) return conf.SSID;
		}
		if(isWifiConnected()){
			WifiInfo info = mWifiManager.getConnectionInfo();
			String ssid = info.getSSID();
			return ssid;
		}else{
			return null;
		}
	}
	public static String getIP(){
		WifiInfo winfo = mWifiManager.getConnectionInfo();
    	return StrOfLongIP(changeEndian(winfo.getIpAddress()));
	}
	public static String getNetMask(){
		DhcpInfo dinfo = mWifiManager.getDhcpInfo();
    	return StrOfLongIP(changeEndian(dinfo.netmask));
	}
	public static String getGateway(){
		DhcpInfo dinfo = mWifiManager.getDhcpInfo();
    	return StrOfLongIP(changeEndian(dinfo.gateway));
	}
	public static String getBroadcastIp(){
		DhcpInfo dinfo = mWifiManager.getDhcpInfo();
		int ip = changeEndian(dinfo.gateway);
		if(Config.DEBUG) Log.i("getGateway","======================getBroadcastIp:======"+StrOfLongIP(ip+254));
    	return StrOfLongIP(ip+254);
	}
	public static int changeEndian(int s){
    	int dest=0;
    	dest = ((s & 0x00ff00ff)<<8)+((s & 0xff00ff00)>>>8);
    	dest = ((dest & 0x0000ffff)<<16)+((dest & 0xffff0000)>>>16);
    	return dest;
    }
	 private static String StrOfLongIP(int longIP)
	    {
	        return String.valueOf(longIP>>>24) + "."
	                + String.valueOf((longIP&0x00FFFFFF)>>>16) + "."
	                + String.valueOf((longIP&0x0000FFFF)>>>8) + "."
	                + String.valueOf(longIP&0x000000FF);
	    }
}