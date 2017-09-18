package cn.easyar.samples.helloar;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import de.materna.ar.model.Vehicle;
import de.materna.ar.model.VehicleBuilder;
import cn.easyar.samples.helloar.databinding.ActivityCentralBinding;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class CentralActivity extends AppCompatActivity {
    Vehicle vehicle;
    FrameLayout m_button_close;
    FrameLayout m_button_play;
    TextView name, top_speed, power, acceleration, financing, x, y, z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);
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
                .videoTitle("Speed & Comfort")
                .build();

//        Bundle bundle = this.getIntent().getExtras();
//        if (bundle != null) {
//            vehicle= bundle.getParcelable("Vehicle");
//        }
        ActivityCentralBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_central);
        binding.setVehicle(vehicle);
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

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}