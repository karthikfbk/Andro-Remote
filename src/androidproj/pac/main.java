package androidproj.pac;

import java.io.BufferedReader;
import java.util.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class main extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	public static final String TAG="Connection";
	public static final int TIMEOUT=10;
	Intent i=null;
	//TextView tv=null;
	private String connectionStatus=null;
	private String socketData = null;
	private Handler mHandler=null;
	ServerSocket server=null;
	Socket client=null;
	String ip=null;
	Hashtable Keyval = new Hashtable();
	
	
	
	//Scanner socketIn;
	//PrintWriter socketOut;
	
	public void onDestroy() {
        super.onDestroy();
        try{
        client.close();
        }
        catch(Exception e)
        {Log.e(TAG, ""+e);}
	}
   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        ip="192.168.1.3";
      //Set up click listeners for the buttons
        Button connectButton = (Button)findViewById(R.id.button1);
        connectButton.setOnClickListener(this);       
        
        Globals.connected=false;
        i = new Intent(this, controller.class);
        mHandler=new Handler();
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.button1:
    	//tv = (TextView) findViewById(R.id.connection_text);
    	String msg;
    	//initialize server socket in a new separate thread
    	if(!Globals.connected)
    	{
    		TextView et=(TextView)findViewById(R.id.editText1);
            ip=et.getText().toString();
    	Thread t=new Thread(initializeConnection);
    	t.start();
    	msg="Attempting to connect…"+ip;
    	}
    	else
    	{
    		msg="Already Connected";
    		startActivity(i);
    	}
    	Toast.makeText(main.this, msg, msg.length()).show();
    	break;
    	}
    	}
    
    private Runnable initializeConnection = new Thread() {
    	public void run() {

    	
    	// initialize server socket
    	try{
    		/*server = new ServerSocket(38300);
    		server.setSoTimeout(TIMEOUT*1000);
    		client = server.accept();*/
    		
    		client=new Socket(ip, 8888);
    		Globals.socketIn=new BufferedReader(new InputStreamReader(client.getInputStream()));
    		Globals.socketOut = new PrintWriter(client.getOutputStream(), true);
    		} 
    		catch (SocketTimeoutException e) {
    			// print out TIMEOUT
    			connectionStatus="Connection has timed out! Please try again";
    			mHandler.post(showConnectionStatus);
    		}
    		catch (IOException e) {
    			Log.e(TAG, ""+e);
    		}
    	finally {
    			//close the server socket
    			try {
    				if (server!=null)
    					server.close();
    				}
    			catch (IOException ec) {
    					Log.e(TAG, "Cannot close server socket"+ec);
    							}
    			}

    	if (client!=null) 
    		{
    		Globals.connected=true;
    		// print out success
    		connectionStatus="Connection was succesful!";
    		mHandler.post(showConnectionStatus);
    		try{
    			socketData=Globals.socketIn.readLine();    
    		                     mHandler.post(socketStatus);
    		                 
    			}
    		catch(IOException ie)
    			{Log.e(TAG, "Cannot read"+ie);}
    		startActivity(i);
    		}
    	}
    	};
    	
    	/**
    	* Pops up a “toast” to indicate the connection status
    	*/
    	private Runnable showConnectionStatus = new Runnable() {
    	public void run() {
    	Toast.makeText(main.this, connectionStatus, Toast.LENGTH_SHORT).show();
    	}
    	};
    	
    	private Runnable socketStatus = new Runnable() {
    		        public void run() {
    		            TextView tv = (TextView) findViewById(R.id.connection_text);
    		            tv.setText(socketData);
    		        }
    		    };
    		    public static class Globals {
    		        public static boolean connected;
    		        public static BufferedReader socketIn;
    		        public static PrintWriter socketOut;
    		    }

}