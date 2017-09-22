package cn.easyar.samples.helloar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import cn.easyar.samples.helloar.databinding.ActivityCentralBinding;
import de.materna.ar.model.Vehicle;
import de.materna.ar.model.VehicleBuilder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.color.white;
import static cn.easyar.samples.helloar.R.color.ar_light_blue;

public class CentralActivity extends AppCompatActivity {
    Button play;
    Button details;
    Button close;
    Vehicle vehicle;
    VideoView video;
    boolean financingHighlighted;
    TextView financing;
    ImageView whiteBox;
    ImageView arrowCircle;
    Context context;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


//        Bundle bundle = this.getIntent().getExtras();
//        if (bundle != null) {
//            vehicle= bundle.getParcelable("Vehicle");
//        }
//        video = (VideoView) findViewById(R.id.video);
//        String vidAddress = "android.resource://" + getPackageName()+"/"+ R.raw.add;
//        Uri vidUri = Uri.parse(vidAddress);
//        MediaController vidControl = new MediaController( this );
//        vidControl.setAnchorView(video);
//        video.setVideoURI(vidUri);
//        video.setMediaController(vidControl);
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
    public void goToMain(View view) {
        onBackPressed();
    }

    public void goToVideoPlay(View view) {
        video.start();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        ActivityCentralBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_central);
        binding.setVehicle(vehicle);
        context = getBaseContext();
        financing = (TextView) findViewById(R.id.vehicle_financing);
        whiteBox = (ImageView) findViewById(R.id.background_financing);
        financingHighlighted = false;
        arrowCircle = (ImageView) findViewById(R.id.arrow_circle);
        play = (Button) findViewById(R.id.button_play);
        details = (Button) findViewById(R.id.button_details);


        details.setOnHoverListener(new ButtonHoverListener(details, this));
        play.setOnHoverListener(new ButtonHoverListener(play, this));

    }


    public void goToTestAnimation() {
        //StateListDrawable as Drawable is taken via Selector in "button_main.xml"
        AnimatedVectorDrawable btnAnimation = (AnimatedVectorDrawable) arrowCircle.getDrawable();
        btnAnimation.stop();
        btnAnimation.start();

    }

    public void goToDetails(View view) {

        if (!financingHighlighted) {
            whiteBox.setVisibility(View.VISIBLE);
            arrowCircle.setVisibility(View.VISIBLE);
            financing.setTextColor(getResources().getColor(ar_light_blue));
            goToTestAnimation();


        } else {
            whiteBox.setVisibility(View.INVISIBLE);
            arrowCircle.setVisibility(View.INVISIBLE);
            financing.setTextColor(getResources().getColor(white));
        }
        financingHighlighted = !financingHighlighted;


    }
}


//    public void bindVehicleToLayout(Vehicle vehicle){
//        name = (TextView) findViewById(R.id.vehicle_name);
//        top_speed = (TextView) findViewById(R.id.vehicle_topSpeed);
//        power = (TextView) findViewById(R.id.vehicle_power);
//        financing = (TextView) findViewById(R.id.vehicle_financing);
//        acceleration = (TextView) findViewById(R.id.vehicle_acceleration);
//        x = (TextView) findViewById(R.id.vehicle_x);
//        y = (TextView) findViewById(R.id.vehicle_y);
//        z = (TextView) findViewById(R.id.vehicle_z);
//    }
