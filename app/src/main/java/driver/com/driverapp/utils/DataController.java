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
    public  String status;
    public String driverFullName ;
    public String cabNumber;

    private  DataController(Context context){

        this.context = context;
        aq = new AQuery(context);
    }

    public static DataController getInstance(Context context){

        if(instance == null) instance = new DataController( context);
        if(instance.context == null) instance.context = context;

        return instance;
    }

    public void loginRequest() {

        String url = "http://serverdp.herokuapp.com/login?phone_number=0557234539&password=test1000";
        requestServer(url,
                new CallBack() {
                    @Override
                    public void process(String o) {
                        if(!o.equals("")) {
                            //Log.e("Jush", "Callback 4");
                            try {
                                // JSONArray object = new JSONArray(o);

                                loginJSon = new JSONObject(o.toString());

                                   /* if(song.getString("streamable").equals("true"))items.add(new SongItem(
                                            song.getString("title"),
                                            song.getString("uri"),
                                            context,
                                            song.getString("artwork_url"),
                                            //song.getJSONObject("user").getString("avatar_url"),
                                            song.getString("permalink_url"),
                                            song.getJSONObject("user").getString("username"),
                                            song.getJSONObject("user").getString("permalink_url")

                                    ));*/
                                status = loginJSon.getString("status");
                                driverFullName = loginJSon.getString("driver_full_name");
                                cabNumber = loginJSon.getString("cab_number");
                                Log.e("Jush",status);
                                Log.e("Jush",loginJSon.getString("cab_number"));
                                Log.e("Jush",loginJSon.getString("driver_full_name"));



                            } catch (JSONException e) {
                                Log.e("Jush", "Catched JSONException. result was: " + o);
                            }
                        }
                        //success.process(o);
                    }
                },
                new CallBack() {
                    @Override
                    public void process(String o) {
                        //   failure.process(null);
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
