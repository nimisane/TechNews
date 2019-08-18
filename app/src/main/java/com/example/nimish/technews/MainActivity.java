package com.example.nimish.technews;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.android.volley.VolleyLog.TAG;

public class MainActivity extends AppCompatActivity {

    private static final Pattern UserID_Pattern =Pattern.compile("^[a-zA-Z]+([a-zA-Z0-9](_|-| )[a-zA-Z0-9])*[a-zA-Z0-9]+$");
    private static final Pattern Password_Pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}");
    TextView skip_login;
    public TextInputLayout user_id;
    public TextInputLayout password;
    Button sign_up;
    Button login;
    ProgressBar pb;
    Intent i;
    String userid_input;
    String password_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPreferenceManager.getInstance(this).isLoggedIn()){
            finish();
            i=new Intent(MainActivity.this,News_feed.class);
            startActivity(i);
            return;
        }

        user_id = (TextInputLayout) findViewById(R.id.user_ID_layout);
        password = (TextInputLayout) findViewById(R.id.password_login_layout);
        login = findViewById(R.id.login);
        sign_up=findViewById(R.id.sign_up);
        skip_login=findViewById(R.id.skip);
        pb=(ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUserid() | !validatePassword())
                {
                    pb.setVisibility(View.GONE);
                    return;
                }
                else
                {
                    if(online()) {
                        pb.setVisibility(View.VISIBLE);
                        userLogin();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!SharedPreferenceManager.getInstance(this).isLoggedIn()){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        else{
            super.onBackPressed();
        }
    }

    public void SignUp(View v)
    {
        i = new Intent(this,Sign_up_Activity.class);
        startActivity(i);
    }

    public void Skip_login(View v)
    {
        SharedPreferenceManager.getInstance(getApplicationContext()).userlogin("Guest","123");
        i = new Intent(this,News_feed.class);
        startActivity(i);
        finish();
    }

    private boolean validateUserid()
    {
        userid_input = user_id.getEditText().getText().toString().trim();
        if(!(UserID_Pattern.matcher(userid_input).matches()))
        {
            user_id.setError("Invalid UserID");
            return false;
        }
        else{
            user_id.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        password_input = password.getEditText().getText().toString().trim();
        if(!Password_Pattern.matcher(password_input).matches())
        {
            password.setError("Invalid Password");
            return false;
        }
        else{
            password.setError(null);
            return true;
        }
    }

    protected boolean online()
    {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if(info != null && info.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void userLogin(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getInt("success")==1){ //!jsonObject.getBoolean("error")

                        SharedPreferenceManager.getInstance(getApplicationContext()).userlogin(jsonObject.getString("userid"),jsonObject.getString("user_password"));
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        i=new Intent(MainActivity.this,News_feed.class);
                        startActivity(i);
                        finish();
                    }
                    else if(jsonObject.getInt("success")==0) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Check Your Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.e(TAG, "LoginError", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "LoginVolleyError", error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",userid_input);
                params.put("user_password",password_input);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
