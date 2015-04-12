package driver.com.driverapp.utils;

import driver.com.driverapp.utils.CallBack;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import driver.com.driverapp.*;

/**
 * Created by parviz on 4/11/15.
 */
public class DataController {

    public Context context;
    private static DataController instance;
    public SharedPreferences preferences;
    private AQuery aq;
    public JSONObject loginJSon;
    public JSONObject saveJSon;
    public String status;
    public String saveStatus;
    public String driverFullName ;
    public String cabNumber;

    public double _latitude = 0.0;
    public double _longitude = 0.0;

    public boolean enterByLogin = false , enterByMain = false;
    private  DataController(Context context){

        this.context = context;
        aq = new AQuery(context);
    }

    public static DataController getInstance(Context context){

        if(instance == null) instance = new DataController( context);
        if(instance.context == null) instance.context = context;

        return instance;
    }

    public void loginRequest(String phoneNumber, String driverPassword , final CallBack success, final CallBack failure) {

        String url = String.format("http://serverdp.herokuapp.com/login?phone_number=%s&password=%s",phoneNumber,driverPassword);
        requestServer(url,
                new CallBack() {
                    @Override
                    public void process(String o) {
                        if(!o.equals("")) {
                            //Log.e("Jush", "Callback 4");
                            try {

                                loginJSon = new JSONObject(o.toString());

                                status         = loginJSon.getString("status");
                                driverFullName = loginJSon.getString("driver_full_name");
                                cabNumber      = loginJSon.getString("cab_number");

                                Log.e("Jush",status);
                                Log.e("Jush",loginJSon.getString("cab_number"));
                                Log.e("Jush",loginJSon.getString("driver_full_name"));



                            } catch (JSONException e) {
                                Log.e("Jush", "Catched JSONException. result was: " + o);
                            }
                        }
                        success.process(o);
                    }
                },
                new CallBack() {
                    @Override
                    public void process(String o) {
                          failure.process(null);
                    }
                }
        );
    }

    public void monitoring(double latitude , double longitude , final CallBack success, final CallBack failure){

        String url = String.format("http://serverdp.herokuapp.com/save_data?lat=%s&lng=%s&cab_number=%s",latitude,longitude,cabNumber);
        requestServer(url,
                new CallBack() {
                    @Override
                    public void process(String o) {
                        if(!o.equals("")) {
                            //Log.e("Jush", "Callback 4");
                            try {

                                saveJSon = new JSONObject(o.toString());

                                saveStatus  = saveJSon.getString("status");

                                Log.e("Jush",saveStatus);

                            } catch (JSONException e) {
                                Log.e("Jush", "Catched JSONException. result was: " + o);
                            }
                        }
                        success.process(o);
                    }
                },
                new CallBack() {
                    @Override
                    public void process(String o) {
                           failure.process(null);
                    }
                }
        );
    }

    private void requestServer(final String url, final CallBack success, final CallBack failure) {
        AjaxCallback<String> callback = new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                int responseCode = status.getCode();
                if (responseCode == 200) success.process(object);
                else failure.process(null);
                super.callback(url, object, status);
            }
        };
        callback.url(url);
        callback.type(String.class);
        callback.timeout(30000);
        callback.encoding("UTF-8");
        callback.method(Constants.METHOD_GET);
        aq.ajax(callback);
    }
}
