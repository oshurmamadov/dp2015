package driver.com.driverapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import driver.com.driverapp.utils.CallBack;
import driver.com.driverapp.utils.DataController;
import driver.com.driverapp.utils.SaveSharedPrefrances;

public class MainActivity extends ActionBarActivity {

    private GoogleMap gMap;
    private AQuery aq;
    Button but;
    private DataController dc;

    TextView driver_full_name ;
    TextView cab_number ;

    ImageButton logoutButton;


    final Handler handler = new Handler();
    Timer    timer = new Timer();
    TimerTask doAsynchronousTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aq = new AQuery(getApplicationContext());
        dc = DataController.getInstance(this);

        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar);

       // but = (Button)findViewById(R.id.button);
        driver_full_name = (TextView)findViewById(R.id.driver_full_name);
        cab_number = (TextView) findViewById(R.id.cab_number);

        updateLocation();

       /* but.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            //   dc.loginRequest();


           }
       });*/

        if(dc.enterByMain == true) getDataFromServer();

        driver_full_name.setText(dc.driverFullName);
        cab_number.setText("борт: "+dc.cabNumber);


        try {
                setUpMap();
        }
        catch(Exception e){
                 e.printStackTrace();
        }

        logoutButton = (ImageButton) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPrefrances.clearData(MainActivity.this);

                dc.enterByMain = false;
                dc.enterByLogin = true;

                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public  void updateLocation()
    {
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(thread);
            }
        };
        timer.schedule(doAsynchronousTask, 25000, 10000);
    }

    Runnable thread = new Runnable() {
        @Override
        public void run() {
            try {
                if( checkGeolocationService() )
                {
                    dc.monitoring(dc._latitude, dc._longitude,
                            new CallBack() {
                                @Override
                                public void process(String o) {

                                    updateNotification();
                                }
                            },
                            new CallBack() {
                                @Override
                                public void process(String o) {
                                    Toast toast3 = Toast.makeText(getApplicationContext(), "Error Status :" + dc.status, Toast.LENGTH_SHORT);
                                    toast3.show();
                                }
                            });
                }


            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public void getDataFromServer(){

        dc.loginRequest(SaveSharedPrefrances.getNumber(MainActivity.this),
                SaveSharedPrefrances.getPassword(MainActivity.this),
                new CallBack() {
                    @Override
                    public void process(String o) {

                        driver_full_name.setText(dc.driverFullName);
                        cab_number.setText("борт: " + dc.cabNumber);
                    }
                },
                new CallBack() {
                    @Override
                    public void process(String o) {
                        Toast toast3 = Toast.makeText(getApplicationContext(), "Error Status :" + dc.status, Toast.LENGTH_SHORT);
                        toast3.show();
                    }
                }
        );
    }

    private void updateNotification(){
        if(dc.saveStatus !=null){
            String flag = "saved";
            if(dc.saveStatus.equals(flag)){
                Toast toast = Toast.makeText(getApplicationContext(),"Location updated",Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"Failure ! The data was not saved",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void setUpMap(){
       if(gMap == null) {

            gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);

            checkGeolocationService();

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

    public boolean checkGeolocationService(){
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled = false , flag = true;
        if(lm==null)
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        if(!gps_enabled && !network_enabled){
            flag = false;
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
           // dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setTitle("Ошибка");
            dialog.setMessage("Ну удалось определить Ваше местоположение. Пожалуйста включите GPS");
            dialog.setPositiveButton("Настройки", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        }
        return flag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }

    protected void onStop() {
        super.onStop();
        Log.e("Tracking", "onStop");
        handler.removeCallbacks(thread);
        if(doAsynchronousTask != null) doAsynchronousTask.cancel();

    }




}
