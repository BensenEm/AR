//================================================================================================================================
//
//  Copyright (c) 2015-2017 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
//  EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
//  and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloar;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import cn.easyar.engine.EasyAR;
import de.materna.ar.model.Vehicle;
import de.materna.ar.model.VehicleBuilder;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//SENSORS FOR EPSON MOVERIO


public class MainActivity extends AppCompatActivity implements ArListener, SensorEventListener, KeyListener {
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloAR
    *      Package Name: cn.easyar.samples.helloar
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "5h2ccGDFwB5Ce8f8ZmclfYaSONJshe1iqTxiAKQkCN50sKKXJheWFi13N9z4Q3qFgiWJlG8EP7vwCNTM7btO5ppjiX4Z7ytzqtm6gDTpqgmJxFIOT0aKuiDmagqBi7g5uCul0Hg01uTuoDITdFlH2QOnK30JCH6dNeBGwh5rf3mvF6zfmEpe9y1UEhjdoIRUnmj4jyp5";
    private GLView glView;
    public TextView vehicle_intro;
    public LinearLayout m_widget;
    public ImageView m_imageView;
    public ImageView buttonBorder;
    public android.graphics.drawable.Drawable boarder;
    Button button;
    public Context context;
    public Vehicle vehicle;
    public FrameLayout buttonFrame;
    public AnimatedVectorDrawable vectorAnimation;
    public Drawable current;
    public StateListDrawable btnAnimation;
    TextView testView;
    TextView vehicleIntroShort;
    TextView vehicleName;
    int tapCounterRight = 0;
    int tapCounterLeft = 0;
    public static final int HEADSET_TAP = 0x00002001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (!EasyAR.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
        }


        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(HEADSET_TAP);
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        m_widget = (LinearLayout) findViewById(R.id.widget);
        m_widget.setVisibility(View.INVISIBLE);

        glView = new GLView(this, this);
        context = getBaseContext();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });
    }


    @Override
    public void updatePosition(final float x, final float y, final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("[updatePosition]", "updating positions with " + x + ", " + y + " and visibility " + visible);
                if (visible) {
                    Log.i("[updatePosition]", "position update!");
                    Log.d("POSITION", "" + x + "--" + y);
                    int height = m_widget.getHeight();
                    int width = m_widget.getWidth();
                    m_widget.layout((int) x, (int) y, (int) x + width, (int) y + height);
                    int buttonHeight = button.getHeight();
                    int buttonWidth = button.getWidth();
                    int spacer = 50;
                } {
                    Log.i("[updatePosition]", "Not racked setting visibility to INVISIBLE!");
                }
                m_widget.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean change){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == HEADSET_TAP) {
            testView.setText("Tapping..." + tapCounterRight++);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {

        return false;
    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                button.setFocusableInTouchMode(true);
                button.requestFocus();
            case KeyEvent.KEYCODE_DPAD_LEFT:
                button.setFocusableInTouchMode(true);
                button.clearFocus();
        }
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {

    }

    private interface PermissionCallback {
        void onSuccess();

        void onFailure();
    }

    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;

    @TargetApi(23)
    public void requestCameraPermission(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glView != null) {
            glView.onResume();
        }
        if (context == null) {
            context = getBaseContext();
        }



        vehicle = VehicleBuilder.create()
                .name("SE 750")
                .introShort("Premium Cabriolet")
                .power(420)
                .acceleration(3.8f)
                .topSpeed(305)
                .financing(220)
                .x("Allrad")
                .y("Extras")
                .z("Premium")
                .build();

        testView = (TextView) findViewById(R.id.test_view);
        vehicleName = (TextView) findViewById(R.id.vehicle_name);
        vehicleName.setText(vehicle.getName());
        vehicleIntroShort = (TextView) findViewById(R.id.vehicle_introShort);
        vehicleIntroShort.setText(vehicle.getIntroShort());
        button = (Button) findViewById(R.id.button);
        button.setOnHoverListener(new ButtonHoverListener(button, this));

        button.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                Drawable img = context.getResources().getDrawable(R.drawable.microphone_normal);
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    img = getResources().getDrawable(R.drawable.microphone_blue);
                }
                if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT || event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    img.setBounds(0, 0, 24, 24);
                    button.setCompoundDrawables(img,null, null, null);
//                    event.getAction();
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        if (glView != null) {
            glView.onPause();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Called when the user taps the Send button
     */
    public void goToCentralActivity(View view) {
        Intent intent = new Intent(this, CentralActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("Vehicle", vehicle);
//        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void buttonAnimation() {
        button = (Button) findViewById(R.id.button);
        //StateListDrawable as Drawable is taken via Selector in "button_main.xml"
        btnAnimation = (StateListDrawable) button.getBackground();
        current = btnAnimation.getCurrent();
        if (current instanceof AnimatedVectorDrawable) {
            vectorAnimation = (AnimatedVectorDrawable) current;
            vectorAnimation.stop();
            vectorAnimation.start();
        }
    }

}
