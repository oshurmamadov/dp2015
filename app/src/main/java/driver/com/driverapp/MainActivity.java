package driver.com.driverapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import driver.com.driverapp.utils.DataController;

public class MainActivity extends ActionBarActivity {

    private GoogleMap gMap;
    private AQuery aq;
    Button but;
    private DataController dc;

    TextView driver_full_name ;
    TextView cab_number ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aq = new AQuery(getApplicationContext());
        dc = DataController.getInstance(this);

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar);

        but = (Button)findViewById(R.id.button);
        driver_full_name = (TextView)findViewById(R.id.driver_full_name);
        cab_number = (TextView) findViewById(R.id.cab_number);

       but.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            //   dc.loginRequest();
               dc.monitoring(dc._latitude,dc._longitude);
               updateNotification();
           }
       });

        driver_full_name.setText(dc.driverFullName);
        cab_number.setText("cub number :"+dc.cabNumber);

        try {
                setUpMap();
        }
        catch(Exception e){
                 e.printStackTrace();
        }
    }


    private void updateNotification(){
        if(dc.saveStatus !=null){
            String flag = "saved";
            if(dc.saveStatus.equals(flag)){
                Toast toast = Toast.makeText(getApplicationContext(),"The data was saved",Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Fail ! The data was not saved",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void setUpMap(){
       if(gMap == null) {

            gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);

            gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    dc._latitude = location.getLatitude();
                    dc._longitude = location.getLongitude();

                    gMap.clear();
                    gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are here!").snippet("Consider yourself located"));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),12.0f ) );

                }
            });

       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }


}
