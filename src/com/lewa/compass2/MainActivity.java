package com.lewa.compass2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {

    private final float MAX_ROATE_DEGREE = 1.0f;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private float mDirection;
    private float mTargetDirection;
    private AccelerateInterpolator mInterpolator;
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;
    private static final String FIRST_IN = "first_in";
    private LocationManager mLocationManager;// 位置管理对象
    private String mLocationProvider;// 位置提供者名称，GPS设备还是网络  
    CompassView mPointer;
    AboveView aboveView;
    TextView mCompossdirection;
    TextView directiondegree;
    private String north;
    private String northeast;
    private String east;
    private String southeast;
    private String south;
    private String southwest;
    private String west;
    private String northwest;
    private TextView mlatitudeTextView;
    private TextView mlongitudeTextView;
	LewaLocationListener LocationListener[] = {new LewaLocationListener(LocationManager.GPS_PROVIDER),new LewaLocationListener(LocationManager.NETWORK_PROVIDER)};
	View LocationView;
	String TAG="MainActivity";
    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {
                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }
                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE
                                : (-1.0f * MAX_ROATE_DEGREE);
                    }
                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator
                                    .getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.2f
                                            : 0.3f)));
                    mPointer.updateDirection(mDirection);
                    aboveView.updateDirection(mDirection);
                }
                updateDirection(mDirection);
                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
//                  //5.0以上沉浸式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      Window window = getWindow(); 
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN    
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); 
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);      
      window.setStatusBarColor(Color.TRANSPARENT);         
        setContentView(R.layout.main);
        initResources();
        initServices();
        firstInAt();
        AdjustActivity.list.add(this);
    }

     public void firstInAt(){
        Boolean isFirsIn = false;
        SharedPreferences pref = getSharedPreferences(FIRST_IN, Context.MODE_PRIVATE);
        isFirsIn = pref.getBoolean(FIRST_IN,true);
        int type = pref.getInt("type", 0);
       if(isFirsIn){
        Intent intent = new Intent(MainActivity.this, AdjustActivity.class);
         intent.putExtra("isfrstInAt", true);
         intent.putExtra("type", type);
            startActivity(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationProvider != null) { 
        	Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
        	updateLocation(location);
			
        } else {
        	LocationView.setVisibility(View.INVISIBLE);
        }
        
        try {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000, 10f, LocationListener[0]);// 2秒或者距离变化10米时更新一次地理位置
		} catch (Exception e) {
			 Log.i(TAG, LocationManager.GPS_PROVIDER+"fail to request location update, ignore");
		}
        try {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 2000, 10f, LocationListener[1]);// 2秒或者距离变化10米时更新一次地理位置
		} catch (Exception e) {
			 Log.i(TAG, LocationManager.NETWORK_PROVIDER+"fail to request location update, ignore");
		}
        if(null != mSensorManager){
             mSensorManager.registerListener(mOrientationSensorEventListener,
                    mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
             Log.i(TAG, (mOrientationSensor==null?true:false) +"");
        }
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStopDrawing = true;
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
        }
        for (int i = 0; i < LocationListener.length; i++) {
            mLocationManager.removeUpdates(LocationListener[i]);
            }
    }
    @Override
    protected void onDestroy() {
        AdjustActivity.list.remove(this);
        super.onDestroy();
    }
    private void initResources() {
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;
        mPointer = (CompassView) findViewById(R.id.compass_pointer);
        aboveView = (AboveView) findViewById(R.id.bit_above);
        mCompossdirection = (TextView) findViewById(R.id.direction);
        directiondegree = (TextView) findViewById(R.id.direction_degree);
        north = getString(R.string.north);
        northeast = getString(R.string.northeast);
        east = getString(R.string.east);
        southeast = getString(R.string.southeast);
        south = getString(R.string.south);
        southwest = getString(R.string.southwest);
        west = getString(R.string.west);
        northwest = getString(R.string.northwest);
        mlatitudeTextView = (TextView) findViewById(R.id.tv_latitude);
        mlongitudeTextView = (TextView) findViewById(R.id.tv_longitude);
        mlatitudeTextView.setTextSize(17);
        mlongitudeTextView.setTextSize(17);

        LocationView = findViewById(R.id.locationView);
        ImageView image = (ImageView) findViewById(R.id.btn_adjust);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AdjustActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });
        Typeface face = Typeface.createFromAsset(getAssets(), "LEWA_LightV2.4.otf");
        directiondegree.setTypeface(face);
    }

    private void initServices() {
        // sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        Criteria criteria = new Criteria();// 条件对象，即指定条件过滤获得LocationProvider  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 较高精度  
        criteria.setAltitudeRequired(false);// 是否需要高度信息  
        criteria.setBearingRequired(false);// 是否需要方向信息  
        criteria.setCostAllowed(false);// 是否产生费用  
        criteria.setPowerRequirement(Criteria.POWER_LOW);// 设置低电耗  
        mLocationProvider = mLocationManager.getBestProvider(criteria, true);// 获取条件最好的Provider  
        Log.i(TAG, mLocationProvider);
    }

    private void updateDirection(float direc) {
        direc = 360 - direc;
        int direction = (int) direc;
        if (direc >= 0 && direc <= 22.5) {
            mCompossdirection.setText(north );
            directiondegree.setText(" " + direction);
        } else if (direc > 22.5 && direc <= 67.5) {
            mCompossdirection.setText(northeast);
            directiondegree.setText("" + direction);
        } else if (direc > 67.5 && direc <= 112.5) {
            mCompossdirection.setText(east);
            directiondegree.setText(" " + direction);
        } else if (direc > 112.5 && direc <= 157.5) {
            mCompossdirection.setText(southeast);
            directiondegree.setText("" + direction);
        } else if (direc > 157.5 && direc <= 202.5) {
            mCompossdirection.setText(south);
            directiondegree.setText(" " + direction);
        } else if (direc > 202.5 && direc <= 247.5) {
            mCompossdirection.setText(southwest);
            directiondegree.setText("" + direction);
        } else if (direc > 247.5 && direc <= 292.5) {
            mCompossdirection.setText(west);
            directiondegree.setText(" " + direction);
        } else if (direc > 292.5 && direc <= 337.5) {
            mCompossdirection.setText(northwest);
            directiondegree.setText("" + direction);
        } else if (direc > 337.5 && direc <= 360) {
            mCompossdirection.setText(north);
            directiondegree.setText(" " + direction);
        }
    }

    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
        	 Log.d(TAG,"onSensorChanged  "+event.accuracy);
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        	Log.i(TAG, "accuracy_adjust"+accuracy);
           if (accuracy <= 1) {
               Intent intent = new Intent(MainActivity.this, AdjustActivity.class);
               intent.putExtra("type", 2);
               startActivity(intent);
           }
        }
    };
    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }
    
 // 更新位置显示  
    private void updateLocation(Location location) { 
    	
        if(location != null ){
        	LocationView.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();  
            double latitude = location.getLatitude();  
            double longitude = location.getLongitude();  
  
            if (latitude >= 0.0f) {  
                sb.append(
                        getLocationString(latitude));  
            } else {  
                sb.append(  
                        getLocationString(-1.0 * latitude));  
            }  
            mlatitudeTextView.setTextSize(15);
            mlatitudeTextView.setText(sb.toString());
            //sb.append("    ");  
            sb = new StringBuilder();
            if (longitude >= 0.0f) {
                sb.append(getLocationString(longitude));
                } else {
                    sb.append(getLocationString(-1.0 * longitude));
                }
            mlongitudeTextView.setTextSize(15);
            mlongitudeTextView.setText(sb.toString());// 显示经纬度，其实还可以作反向编译，显示具体地址  
        //}  
        }else{
            Log.i(TAG, "location == null");
            LocationView.setVisibility(View.INVISIBLE);
        }
    }
    // 把经纬度转换成度分秒显示  
    private String getLocationString(double input) {  
    
        int du = (int) input;  
        int fen = (((int) ((input - du) * 3600))) / 60;  
        int miao = (((int) ((input - du) * 3600))) % 60;  
        return String.valueOf(du) + "°" + String.valueOf(fen) + "′"  
                + String.valueOf(miao) + "″";  
    }  
    // 位置信息更新监听  
    
    class LewaLocationListener implements LocationListener{
         Location mLastLocation;
         String mProvider;
         public LewaLocationListener(String provider) {
             mProvider = provider;
             mLastLocation = new Location(mProvider);
         }
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            updateLocation(location);// 更新位置  
            }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged");
            if (status != LocationProvider.OUT_OF_SERVICE) { 
                if(provider== null){
                    Log.i(TAG, "provider == null");
                }
                updateLocation(mLocationManager  
                        .getLastKnownLocation(provider));
            } else {  
                LocationView.setVisibility(View.INVISIBLE);
            }  
        }
        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
            updateLocation(mLocationManager  
                    .getLastKnownLocation(provider));
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    }
}
