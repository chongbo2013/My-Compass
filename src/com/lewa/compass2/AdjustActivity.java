package com.lewa.compass2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

public class AdjustActivity extends Activity{
    private boolean adjustflag=false;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    boolean isZero=false;
    boolean isNinety=false;
    boolean isOne_eighty=false;
    boolean istwo_seventy=false;
    boolean isSuccess=false;
    boolean isSenserValue = false;
    private static int sCount=0;
    private Vibrator vibrator;
    private TextView tv;
    private int TYPE = 0;//type==1 用户手动校准，可退出，type ==2 受干扰自动校准
    private OrientationDetector mOrientationDetector;
    int count = 0;
    ImageView image;
    CircleView circleView = null;
    TextView tv_step;
    static List<Activity> list = new ArrayList<Activity>();
    String TAG = "AdjustActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 //5.0以上沉浸式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      Window window = getWindow(); 
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN    
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); 
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);      
      window.setStatusBarColor(Color.TRANSPARENT);         
        setContentView(R.layout.adjust);
        init();
    }
    private void init(){
    	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        mOrientationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mOrientationDetector=new OrientationDetector(this);
       tv =  (TextView)findViewById(R.id.tv);
       image = (ImageView) findViewById(R.id.iv);
       tv_step = (TextView) findViewById(R.id.tv_step);
       circleView = (CircleView) findViewById(R.id.circle);
           Intent intent = getIntent();
           TYPE = intent.getIntExtra("type", 0);
        
        if(TYPE==1){
            tv.setText(R.string.adjust_active);
        }else if(TYPE == 2){
            tv.setText(R.string.adjust_tv);
        }else{
            tv.setText(getString(R.string.adjust_firstIn));
        }
        list.add(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener,
                    mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
            mOrientationDetector.enable();
        }
        sCount = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
            mOrientationDetector.disable();
        }
    }
    @Override
    protected void onDestroy() {
        list.remove(this);
        super.onDestroy();
    }
    private class OrientationDetector extends OrientationEventListener{

        public OrientationDetector(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if(orientation>330||orientation<30){
                isZero=true;
            }else if(orientation>60&&orientation<120){
                isNinety=true;
            }else if(orientation>150&&orientation<210){
                isOne_eighty=true;
            }else if(orientation>240&&orientation<300){
                istwo_seventy=true;
            }

            if(isZero&&isNinety &&isOne_eighty&&istwo_seventy && isSenserValue){
                sCount++;
                isZero=false;
                isNinety=false;
                isOne_eighty=false;
                istwo_seventy=false;
                
            }
            
            
            switch (sCount) {
            
            case 1:
                Log.i(TAG, "sCount"+sCount);
                circleView.updateStep(sCount);
                break;
            case 2:
                Log.i(TAG, "sCount"+sCount);
                circleView.updateStep(sCount);
                break;
            case 3:
                Log.i(TAG, "sCount"+sCount);
                circleView.updateStep(sCount);
                break;
            case 4:
                Log.i(TAG, "sCount"+sCount);
                circleView.updateStep(sCount);
                check_first_in();
                vibrator.vibrate(800);
                finish();
                break;
            default:
                break;
            }
        }
    }
    void check_first_in(){
    	SharedPreferences pref = getSharedPreferences("first_in", Context.MODE_PRIVATE);
    	 SharedPreferences.Editor ed = pref.edit();
         ed.putBoolean("first_in",false);
         ed.commit();
    }
    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG,"onSensorChanged  "+event.accuracy);
            if(event.accuracy==SensorManager.SENSOR_STATUS_ACCURACY_HIGH){
                isSuccess=true;
            }else{
                isSuccess=false;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i(TAG, "accuracy_adjust  ==  "+accuracy);
              if(accuracy==SensorManager.SENSOR_STATUS_ACCURACY_HIGH){
                  isSenserValue = true;
            }
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	 
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            count ++;
            if(TYPE != 1){
                if(count != 2){
                Toast.makeText(getApplication(), getResources().getString(R.string.finish), 0).show();
            }else if(count == 2){
                check_adjust();
                if(!list.isEmpty()){
                    for(int i = 0; i < list.size(); i++)
                        list.get(i).finish();
                    }
                }
            }else{
                list.remove(this);
                this.finish();
            }
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
    void check_adjust(){
         SharedPreferences pref = getSharedPreferences("first_in", Context.MODE_PRIVATE);
         SharedPreferences.Editor ed = pref.edit();
         ed.putBoolean("first_in",true);
         ed.putInt("type", TYPE);
         ed.commit();
    }
}
