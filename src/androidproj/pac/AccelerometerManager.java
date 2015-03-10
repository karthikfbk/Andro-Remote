package androidproj.pac;
	
	import java.util.List;
	 
	import android.content.Context;
	import android.hardware.Sensor;
	import android.hardware.SensorEvent;
	import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
	 
	/**
	 * Android Accelerometer Sensor Manager Archetype
	 * @author antoine vianey
	 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
	 */
	public class AccelerometerManager {
	 
	    /** Accuracy configuration */
	    private  float threshold     = 0.2f;
	    private  int interval     = 100;
	 
	    private Sensor sensor;
	    private SensorManager sensorManager;
	    // you could use an OrientationListener array instead
	    // if you plans to use more than one listener
	    private AccelerometerListener listener;
	    private Context context;
	    /** indicates whether or not Accelerometer Sensor is supported */
	    private Boolean supported;
	    /** indicates whether or not Accelerometer Sensor is running */
	    private boolean running = false;
	 
	    /**
	     * Returns true if the manager is listening to orientation changes
	     */
	    public boolean isListening() {
	        return running;
	    }
	 
	    /**
	     * Unregisters listeners
	     */
	    public void stopListening() {
	        running = false;
	        try {
	            if (sensorManager != null && sensorEventListener != null) {
	                sensorManager.unregisterListener(sensorEventListener);
	            }
	        } catch (Exception e) {}
	    }
	 
	    /**
	     * Returns true if at least one Accelerometer sensor is available
	     */
	    public boolean isSupported(Context context) {
	    	this.context=context;
	        if (supported == null) {
	            if (context != null) {
	                sensorManager = (SensorManager) context.
	                        getSystemService(Context.SENSOR_SERVICE);
	                List<Sensor> sensors = sensorManager.getSensorList(
	                        Sensor.TYPE_ACCELEROMETER);
	                supported = new Boolean(sensors.size() > 0);
	            } else {
	                supported = Boolean.FALSE;
	            }
	        }
	        return supported;
	    }
	 
	    /**
	     * Configure the listener for shaking
	     * @param threshold
	     *             minimum acceleration variation for considering shaking
	     * @param interval
	     *             minimum interval between to shake events
	     */
	    public void configure(int threshold, int interval) {
	        this.threshold = threshold;
	        this.interval = interval;
	    }
	 
	    /**
	     * Registers a listener and start listening
	     * @param accelerometerListener
	     *             callback for accelerometer events
	     */
	    public void startListening(
	            AccelerometerListener accelerometerListener) {
	        sensorManager = (SensorManager) context.
	                getSystemService(Context.SENSOR_SERVICE);
	        List<Sensor> sensors = sensorManager.getSensorList(
	                Sensor.TYPE_ACCELEROMETER);
	        if (sensors.size() > 0) {
	            sensor = sensors.get(0);
	            running = sensorManager.registerListener(
	                    sensorEventListener, sensor, 
	                    SensorManager.SENSOR_DELAY_GAME);
	            listener = accelerometerListener;
	        }
	    }
	 
	    /**
	     * Configures threshold and interval
	     * And registers a listener and start listening
	     * @param accelerometerListener
	     *             callback for accelerometer events
	     * @param threshold
	     *             minimum acceleration variation for considering shaking
	     * @param interval
	     *             minimum interval between to shake events
	     */
	    public void startListening(
	            AccelerometerListener accelerometerListener, 
	            int threshold, int interval) {
	        configure(threshold, interval);
	        startListening(accelerometerListener);
	    }
	 
	    /**
	     * The listener that listen to events from the accelerometer listener
	     */
	    private SensorEventListener sensorEventListener = 
	        new SensorEventListener() {
	 
	        private long now = 0;
	        private long timeDiff = 0;
	        private long lastUpdate = 0;
	        private long lastShake = 0;
	 
	        private float x = 0;
	        private float y = 0;
	        private float z = 0;
	        private float lastX = 0;
	        private float lastY = 0;
	        private float lastZ = 0;
	        private float force = 0;
	 
	        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	 
	        public void onSensorChanged(SensorEvent event) {
	            // use the event timestamp as reference
	            // so the manager precision won't depends 
	            // on the AccelerometerListener implementation
	            // processing time
	            now = System.currentTimeMillis();
	            timeDiff = now - lastUpdate;
	           // Log.d("TD:", ""+timeDiff);
	            if(timeDiff > interval)
	            {
	            	
	            	
	            	lastUpdate = now;
	            	x = event.values[0];
	            	y = event.values[1];
	            	z = event.values[2];
	 
	            // if not interesting in shake events
	            // just remove the whole if then else bloc
	            
	                force = Math.abs(x + y + z - lastX - lastY - lastZ) 
	                                / timeDiff*10000;
	                //Log.d("Force:", ""+force);
	                if (force > threshold) {
	                	// trigger shake event
	                    //listener.onShake(force);
	                    }
	                    lastX = x;
	                    lastY = y;
	                    lastZ = z;
	                    
	                }
	            
	            // trigger change event
	            listener.onAccelerationChanged(x, y, z);
	        }
	 
	    };
	 
	}

