package com.geniatech.client_box;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.util.Log;

public class SimpleWeb{
	private ServerSocket mServerSocket = null;
	private final static int SER_PORT = 8080; 
	private boolean mIsListen = true;
	private Handler mHandle;
	private static Context mContext;
	public SimpleWeb(Handler handle,Context cntx) {
		mHandle = handle;
		mContext = cntx;
	}
	public void ServerStart(){
		mIsListen = true;
		new Thread(new Runnable() {
			public void run() {
				try{
					mServerSocket = new ServerSocket(SER_PORT);
				}catch(Exception e){}
				while(mIsListen){
			        Socket socket = null;
			        BufferedReader br = null;
			        PrintWriter pw = null;
			        try {
			            socket = mServerSocket.accept();
			            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			            pw = new PrintWriter(socket.getOutputStream());
			            
			            String str = br.readLine(); 
			            Log.i("web",str);
			            if(!str.contains("GET")){
				            while(str!=null){
					            str = br.readLine();
					            Log.i("web",str);
					            if(str.contains("Connection")){
					            	break;
					            }
				            }
			            }
			            pw.print("HTTP/1.1 200 OK\r\n");
			            pw.print( "Connection: close\r\n");
			            //pw.print("Date: "+"Thu,26,Jul 2013 14:01:01 GMT"+"\r\nServer: " + "many wifi" + "\r\n");
			            //pw.print("Content-Type:text/plain\n\n");
			            //sendPage("/data/index.html",socket.getOutputStream());
			            handleRequest(str, socket.getOutputStream());
			            pw.flush();
			        } catch (SocketTimeoutException e) {
			            
			        }catch(Exception e){
			        	e.printStackTrace();
			        }finally{
			            try {
			                if(br!=null) br.close();
			                if(pw!=null) pw.close();
			                if(socket!=null) socket.close();
			                //if(s!=null) s.close();
			            } catch (Exception e2) {
			                e2.printStackTrace();
			            }
			        }
				}
			}
		}).start();
	}
	public void ServerStop(){
		mIsListen = false;
		if(mServerSocket != null){
			try{
				mServerSocket.close();
			}catch(Exception e){}
		}
	}
	public void handleRequest(String queryStr,OutputStream os){
		String cmd = queryStr.substring(queryStr.indexOf('/'));
		cmd = cmd.substring(0, cmd.indexOf(' '));
		Log.i("web",cmd);
		//sendPage(os);
		if(cmd.equals("/")){
			if(WifiUtils.isAPMode()){
				sendPage("a2w.html",os);
			}else{
				sendPage("w2a.html",os);
			}
		}else if(cmd.contains("a2w")){
			sendPage("a2w_info.html",os);
			if(cmd.contains("?")){
				cmd = cmd.substring(cmd.indexOf('?')+1);
				startWifi(cmd);
			}
		}else if(cmd.contains("w2a")){
			sendPage("w2a_info.html",os);
			statAp();
		}
		
	}
	public static void sendPage(String filename,OutputStream os){
    	//File page = new File(filename);
    	//if(page.exists()){
    		try{
                //InputStream is = new FileInputStream(page);
    			AssetManager assm = mContext.getAssets();
                InputStream is = assm.open(filename);
                byte[] buff = new byte[1024*4];
                int read_len;
                while((read_len = is.read(buff))!=-1){
                    os.write(buff, 0, read_len);
                }
                os.close();
                is.close();
    		}catch(Exception e){}
        //}
    }
	public static void sendPage(OutputStream os){
		PrintWriter pw = new PrintWriter(os);
    	pw.print("Content-type:text/html\n\n");
    	pw.print("<htmel>"+
				"<head>"+
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=gbk\" />"+
				"</head>"+
				"<body>"+
				"<form action=\"a3w\" method=\"get\">"+
				"<INPUT TYPE=\"text\" NAME=\"ssid\" SIZE=20 VALUE=\"SSID\"><br>"+
				"<INPUT TYPE=\"text\" NAME=\"passwd\" SIZE=20 VALUE=\"password\"><br>"+
				"<INPUT TYPE=\"submit\" value=\"commmmit!!\"></body>"+
				"</form>"+
				"</body>"+
				"<htmel>");
    }
	public void statAp(){
		Log.i("web","------------start ap-------------------------");
		MsgUtils msgutil = new MsgUtils(Config.CMD_BOX_WIFI_2_AP);
        Message msg = Message.obtain(mHandle, BoxService.MSG_REMOTE_INFO, msgutil);
        mHandle.sendMessage(msg);
	}
	public void startWifi(String queryStr){
		/*
		String str = Config.CMD_BOX_AP_2_WIFI+"#"+
    			Config.CMD_BOX_SSID+ssid+"#"+
    			Config.CMD_BOX_SEC_TYPE+"1" + "#" +
    			Config.CMD_BOX_PASSWORD + passwd;
		*/
		Log.i("web","------------start Wifi-------------------------");
		String str = Config.CMD_BOX_AP_2_WIFI+"#"+
    			queryStr.replaceAll("&", "#")+"#"+
    			Config.CMD_BOX_SEC_TYPE+"1" ;
		MsgUtils msgutil = new MsgUtils(str);
        Message msg = Message.obtain(mHandle, BoxService.MSG_REMOTE_INFO, msgutil);
        mHandle.sendMessage(msg);
	}
}