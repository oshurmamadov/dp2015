package driver.com.driverapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;

import driver.com.driverapp.utils.CallBack;
import driver.com.driverapp.utils.DataController;
import driver.com.driverapp.utils.SaveSharedPrefrances;


public class LoginActivity extends ActionBarActivity {

    private DataController dc;
    private AQuery aq;

    ProgressDialog mProgressDialog ;
    EditText number;
    EditText password;
    Button connectBtn;

    TextView driver_full_name ;
    TextView cab_number ;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar);

        driver_full_name = (TextView)findViewById(R.id.driver_full_name);
        cab_number = (TextView) findViewById(R.id.cab_number);
        logoutButton = (Button) findViewById(R.id.logout_button);

        driver_full_name.setText("Аутентификация");

        cab_number.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.INVISIBLE);

        dc = DataController.getInstance(this);
        aq = new AQuery(getApplicationContext());

        number = (EditText)findViewById(R.id.number_field);
        password = (EditText) findViewById(R.id.password_field);
        connectBtn = (Button) findViewById(R.id.connectBtn);



        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //dc.loginRequest(number.getText().toString(),password.getText().toString());
                mProgressDialog = ProgressDialog.show(LoginActivity.this, "Подождите", "Загружается карта...", true);
                saveData();
                dc.loginRequest(SaveSharedPrefrances.getNumber(LoginActivity.this),
                                SaveSharedPrefrances.getPassword(LoginActivity.this),
                                new CallBack() {
                                    @Override
                                    public void process(String o) {
                                        mProgressDialog.dismiss();
                                        beginTracking();
                                    }
                                },
                                new CallBack() {
                                    @Override
                                    public void process(String o) {
                                        Toast toast3 = Toast.makeText(getApplicationContext(), "Ошибка:" + dc.status, Toast.LENGTH_SHORT);
                                        toast3.show();
                                    }
                                }
                                );


            }
        });
    }

    public void saveData()
    {
        SaveSharedPrefrances.setNumber(this,number.getText().toString());
        SaveSharedPrefrances.setPassword(this,password.getText().toString());
    }

    public void beginTracking(){



        if(dc.status != null)
        {
            Log.e("Tracking", dc.status);

            String flag = "success";
            if (dc.status.equals(flag))
            {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivityForResult(intent, 1);
                finish();
            } else
            {
                SaveSharedPrefrances.setNumber(this,"");
                SaveSharedPrefrances.setPassword(this,"");

                Toast toast = Toast.makeText(getApplicationContext(), "Введены некорректные данные", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }



}
