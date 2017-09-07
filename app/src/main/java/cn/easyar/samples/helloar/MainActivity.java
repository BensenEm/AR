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
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.easyar.Drawable;
import cn.easyar.Image;
import cn.easyar.engine.EasyAR;
import cn.easyar.samples.helloar.databinding.ActivityMainBinding;
import cn.easyar.samples.helloar.databinding.ActivityTeaserFullBinding;
import de.materna.ar.model.Vehicle;
import de.materna.ar.model.VehicleBuilder;


public class MainActivity extends AppCompatActivity implements ArListener {
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
    public TextView sampleText;
    public ConstraintLayout m_widget;
    public ImageView m_imageView;
    public ImageView buttonBorder;
    public android.graphics.drawable.Drawable boarder;
    public ImageButton m_button;
    public Context context;
    public Vehicle vehicle;
    public ImageButton testButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!EasyAR.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
        }
        vehicle = VehicleBuilder.create()
                .name("SE 750")
                .introShort("Intro Texte an dieser Stelle könnnen ruhig auch etwas länger sein, oder täusche ich mich? Falls nein bitte ansagen...")
                .power(420)
                .acceleration(3.8f)
                .topSpeed(305)
                .financing(220)
                .x("Allrad")
                .y("Extras")
                .z("Premium")
                .build();

        m_button = (ImageButton) findViewById(R.id.button_more);
        testButton = (ImageButton) findViewById(R.id.testbutton);
        m_widget = (ConstraintLayout) findViewById(R.id.widget);
        glView = new GLView(this, this);
        context = getBaseContext();
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setVehicle(vehicle);

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
    public void updateVisibility(final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("UI Thread", "I am the UI thread");
                m_widget.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);

                android.graphics.drawable.Drawable drawable = m_button.getDrawable();

                if (!visible) {

                    if (drawable instanceof Animatable) {
                        Log.e("Animatable", "yes");
                        ((Animatable) drawable).stop();
//                        ((Animatable) drawable2).stop();
                    }
                }
                if (visible) {
                    if (drawable instanceof Animatable) {
                        Log.e("Animatable", "yes");
                        ((Animatable) drawable).start();

                    }
                }

            }
        });
    }

    @Override
    public void updatePosition(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.e("POSITION", "" + x+ "--"+ y);
//                sampleText.setText("I am " + Build.MODEL);
                m_widget.setY(y);
                m_widget.setX(x);
                testButton.setX(x);
                testButton.setY(y);
            }
        });

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    /** Called when the user taps the Send button */
    public void goToTeaserFull(View view) {
        Intent intent = new Intent(this, TeaserFull.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Vehicle", vehicle);
        intent.putExtras(bundle);
        startActivity(intent);
    }



}
