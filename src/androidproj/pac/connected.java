package androidproj.pac;



import android.app.Activity;
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
import androidproj.pac.main.Globals;

public class connected extends Activity{
	
	Button up;
	Button down;
	Button left;
	Button right;
	Button x;
	boolean mDown;
	String data;
	private Handler mHandler;
	String[] pids=new String[2];
	int[] pidi=new int[2];
	View view;
	 public connected () {
	        mHandler = new Handler();
	        pids[0]="None";
	        pids[1]="None";
	        pidi[0]=-1;
	        pidi[1]=-1;
	        data="";
	    }
	private Runnable longPressThread = new Runnable() {
        public void run() {
        		if(!data.equals(""))
        		{
            	Globals.socketOut.println(data);
				Globals.socketOut.flush();
				mHandler.postAtTime(this, SystemClock.uptimeMillis() + 100);
        		}
        		else
        		{
        			Globals.socketOut.println("None");
        			mHandler.removeCallbacks(this);
        		}
        	
        	 
				             
        					}
    };
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.connected);
	       
	        x= (Button) findViewById(R.id.xxx);
	        up= (Button) findViewById(R.id.up);
	        down= (Button) findViewById(R.id.down);
	        left= (Button) findViewById(R.id.left);
	        right= (Button) findViewById(R.id.right);
	        
	        view = findViewById(R.id.view1); 
	        
	        
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
	            	
	            	switch(actionCode)
	            	{
	            		case MotionEvent.ACTION_DOWN:
	            			id=findButtonId(x,y);
	            			if(id!=-1)
	            			{
	            			temp=(Button) findViewById(id);
	            			s=temp.getText().toString();
	            			temp.setPressed(true);
	            			data=""+s.charAt(0);
	            			mHandler.post(longPressThread); 
	            			}
	            			pids[0]=s;
	            			pidi[0]=id;
	            			Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_POINTER_DOWN:
	            			
	            			pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            			x=(int)motionevent.getX(pid);
	            			y=(int)motionevent.getY(pid);
	            			id=findButtonId(x,y);
	            			
	            			if(id!=-1)
	            			{
	            			temp=(Button) findViewById(id);
	            			s=temp.getText().toString();
	            			temp.setPressed(true);
	            			data+=s.charAt(0);
	            			}
	            			pids[pid]=s;
	            			pidi[pid]=id;
	            			Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_POINTER_UP:
	            			
	            			pid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;
	            			x=(int)motionevent.getX(pid);
	            			y=(int)motionevent.getY(pid);
	            			id=findButtonId(x,y);
	            			if(id!=-1)
	            			{
	            				temp=(Button) findViewById(id);
	            				if(temp.isPressed())
	            					temp.setPressed(false);
	            				s=temp.getText().toString();
	        
	            				data=removeChar(s.charAt(0));
	            				
	            			}
	            			pids[pid]="None";
	            			pidi[pid]=-1;
	            			Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            		case MotionEvent.ACTION_UP:
	            			
	            			pid = motionevent.getPointerId(0);
	            			x=(int)motionevent.getX(0);
	            			y=(int)motionevent.getY(0);
	            			id=findButtonId(x,y);
	            			if(id!=-1)
	            			{
	            				temp=(Button) findViewById(id);
	            				if(temp.isPressed())
	            					temp.setPressed(false);
	            				data="";
	            			}
	            			//mHandler.removeCallbacks(longPressThread);
	            			pids[pid]="None";
	            			pidi[pid]=-1;
	            			Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            			break;
	            			
	            		case MotionEvent.ACTION_MOVE:
	            			
	            			for(int i=0;i<motionevent.getPointerCount();i++)
	            			{
	            			pid=motionevent.getPointerId(i);
	            			x=(int)motionevent.getX(i);
	            			y=(int)motionevent.getY(i);
	            			id=findButtonId(x,y);      
	            			if(id==-1 && !pids[pid].equals("None"))
	            				{
	            				temp=(Button) findViewById(pidi[pid]);
	            				temp.setPressed(false);
	            				s=temp.getText().toString();
	            				data=removeChar(s.charAt(0));
	            				pids[pid]="None";
	            				pidi[pid]=-1;
	            				Log.d("Touch Test", "Pressed Button:"+pids[0]+";"+pids[1]);
	            				}
	            			}
	            			break;
	            	}
	            	Log.d("Touch Test", "String:"+data);
	                return true;
	            } 
	        }; 
	        
	        
	        view.setOnTouchListener(tl);
	        
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
	 
	 int findButtonId(int x, int y)
	 {
		 int i;
		 Button b;
		 View v;
		 int b_left,b_right,b_top,b_bottom;
		AbsoluteLayout al=(AbsoluteLayout)findViewById(R.id.absolute);
		for(i=0;i<al.getChildCount();i++)
		{
			v = al.getChildAt(i);
			if(v instanceof Button)
			{
				b=(Button)v;
				b_left=b.getLeft();
				b_right=b.getRight();
				b_top=b.getTop();
				b_bottom=b.getBottom();
				if(x >= b_left && x <= b_right && y >= b_top && y <= b_bottom)
					return b.getId();
			}
		}
		return -1;
	 }
	 
	 String removeChar(char c)
	 {
		 String s="";
		 int i;
		 for(i=0;i<data.length();i++)
			 if(c!=data.charAt(i))
				 s+=data.charAt(i);
		 return s;
				 
	 }
}
