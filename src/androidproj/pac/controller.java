package androidproj.pac;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidproj.pac.main.Globals;

public class controller extends Activity implements AccelerometerListener{
	
	Button up;
	Button down;
	Button left;
	Button right;
	Button x;
	boolean mDown;
	String data;
	private Handler mHandler;
	char[] pids=new char[2];
	int[] pidi=new int[2];
	View view;
	private static Context CONTEXT;
    AccelerometerManager acm;
	 public controller () {
	        mHandler = new Handler();
	        pids[0]='X';
	        pids[1]='X';
	        pidi[0]=-1;
	        pidi[1]=-1;
	        data="XX";
	    }
	private Runnable longPressThread = new Runnable() {
        public void run() {
        		if(!data.equals("XX"))
        		{
            	Globals.socketOut.println(data);
				Globals.socketOut.flush();
				mHandler.postAtTime(this,SystemClock.uptimeMillis()+100);
        		}
        		else
        		{
        			Globals.socketOut.println("XX");
        			mHandler.removeCallbacks(this);
        		}
        	
        	 
				             
        					}
    };
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.controller);
	        
	        view = findViewById(R.id.view1); 
	        CONTEXT = this;
	        acm=new AccelerometerManager();
	        
	        OnTouchListener tl=new OnTouchListener() 
	        { 
	            public boolean onTouch(View view, MotionEvent motionevent) 
	            { 
	            	Button temp;
	            	String s="None";
	            	//dumpEvent(motionevent);
	            	int action = motionevent.getAction();
	            	int x=(int)motionevent.getX();
	            	int y=(int)motionevent.getY();
	            	int actionCode = action & MotionEvent.ACTION_MASK;
	            	int id,pid;
	            	char pos;
	            	switch(actionCode)
	            	{
	            		case MotionEvent.ACTION_DOWN:
	            			pos=findTouchPos(x,y);
	            			id=findPosId(pos);
	            			data=setChar(pos);
	            			pids[0]=pos;
	            			mHandler.post(longPressThread);
	            			Log.d("ACTION_DOWN", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_POINTER_DOWN:
	            			
	            			pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            			x=(int)motionevent.getX(pid);
	            			y=(int)motionevent.getY(pid);
	            			pos=findTouchPos(x,y);
	            			id=findPosId(pos);
	            			if(data.indexOf(pos)==-1)
	            				data=setChar(pos);
	            				
	            			pids[pid]=pos;
	            			
	            			Log.d("ACTION_POINTER_DOWN", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_POINTER_UP:
	            			
	            			pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            			x=(int)motionevent.getX(pid);
	            			y=(int)motionevent.getY(pid);
	            			pos=findTouchPos(x,y);
	            			if(data.indexOf(pos)!=-1)
	            			{
	            				if(pids[0]!=pids[1])
	            				{
	            					data=removeChar(pos);
	            				}
	            			}
	            			
	            			pids[pid]='X';
	            			Log.d("ACTION_POINTER_UP", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            		case MotionEvent.ACTION_UP:
	            			
	            			pid = motionevent.getPointerId(0);
	            			x=(int)motionevent.getX(0);
	            			y=(int)motionevent.getY(0);
	            			pos=findTouchPos(x,y);
	            			if(data.indexOf(pos)!=-1)	            			
	            					data=removeChar(pos);
	            			pids[pid]='X';
	            			Log.d("ACTION_UP", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_MOVE:
	            			
	            			for(int i=0;i<motionevent.getPointerCount();i++)
	            			{
	            			pid=motionevent.getPointerId(i);
	            			x=(int)motionevent.getX(i);
	            			y=(int)motionevent.getY(i);
	            			pos=findTouchPos(x,y);      
	            			
	            				if(pids[pid]!=pos && pids[0]!=pids[1])
	            					{
	            					if(data.indexOf(pids[pid])!=-1)
	            						data=removeChar(pids[pid]);
	            					}
	            				
	            			}
	            			//Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            	}
	            	Log.d("Touch Test", "String:"+data);
	                return true;
	            } 
	        }; 
	        
	        
	        view.setOnTouchListener(tl);
	        
	    }
	 
	 /**
	     * onShake callback
	     */
	    public void onShake(float force) {
	        Toast.makeText(this, "Phone shaked : " + force, 1000).show();
	    }
	 
	    /**
	     * onAccelerationChanged callback
	     */
	    public void onAccelerationChanged(float x, float y, float z) {
	        
	    	char pos=findSensorPos(y);
	    	
	    	if(pos!='X' && data.equals("XX"))
	    	{
	    		data=setChar(pos,1);
	    		mHandler.post(longPressThread);
	    	}
	    	else
	    		data=setChar(pos,1);
	    		
	    		//Log.d("Sensor Test", "String:"+data);	
	    }
	 
	 protected void onResume() {
	        super.onResume();
	        if (acm.isSupported(CONTEXT)) {
	            acm.startListening(this);
	        }
	    }
	 
	    protected void onDestroy() {
	        super.onDestroy();
	        if (acm.isListening()) {
	            acm.stopListening();
	        }
	 
	    }
	 
	    public static Context getContext() {
	        return CONTEXT;
	    }
	 
	 private void dumpEvent(MotionEvent event) {
  	   String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
  	      "POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
  	   StringBuilder sb = new StringBuilder();
  	   int action = event.getAction();
  	   int actionCode = action & MotionEvent.ACTION_MASK;
  	   sb.append("event ACTION_" ).append(names[actionCode]);
  	   if (actionCode == MotionEvent.ACTION_POINTER_DOWN
  	         || actionCode == MotionEvent.ACTION_POINTER_UP) {
  	      sb.append("(pid " ).append(
  	      action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
  	      sb.append(")" );
  	   }
  	   sb.append("[" );
  	   for (int i = 0; i < event.getPointerCount(); i++) {
  	      sb.append("#" ).append(i);
  	      sb.append("(pid " ).append(event.getPointerId(i));
  	      sb.append(")=" ).append((int) event.getX(i));
  	      sb.append("," ).append((int) event.getY(i));
  	      if (i + 1 < event.getPointerCount())
  	         sb.append(";" );
  	   }
  	   sb.append("]" );
  	   Log.d("Touch Test", sb.toString());
  	}
	 
	 char findTouchPos(int x, int y)
	 {
		 int i;
		 View v=findViewById(R.id.view1);
		 
		 if(x>v.getWidth()/2)
			 return 'U';
		 
		return 'D';
	 }
	 
	 char findSensorPos(float v)
	 {
		 
		 if(v<-1.5)
			 return 'L';
		 else if(v>1.5)
			 return 'R';
		 else
			 return 'X';
	 }
	 
	 int findPosId(char pos)
	 {
		 if(pos=='U')
			 return 0;
		 else if(pos=='D')
			 return 0;
		 else if(pos=='L')
			 return 1;
		 else
			 return 1;
			 
	 }
	 String removeChar(char c)
	 {
		 String s="";
		 int i;
		 for(i=0;i<data.length();i++)
			 if(c!=data.charAt(i))
				 s+=data.charAt(i);
			 else
				 s+='X';
		 return s;
				 
	 }
	 
	 String setChar(char c)
	 {
		 String s="";
		 for(int i=0;i<data.length();i++)
			 if(i==0)
				 s+=c;
			 else
				 s+=data.charAt(i);
		 return s;
	 }
	 
	 String setChar(char c,int index)
	 {
		 String s="";
		 for(int i=0;i<data.length();i++)
			 if(i==index)
				 s+=c;
			 else
				 s+=data.charAt(i);
		 return s;
	 }
	 
	 String setandremoveChar(char c,int index)
	 {	
		 int j;
		 if(index%2==0)
			 j=index+1;
		 else
			 j=index-1;
		 String s="";
		 for(int i=0;i<data.length();i++)
			 if(i==index)
				 s+=c;
			 else if(i==j)
				 s+='X';
			 else
				 s+=data.charAt(i);
		 return s;
	 }
}
