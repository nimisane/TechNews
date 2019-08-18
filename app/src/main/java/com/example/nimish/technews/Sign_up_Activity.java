package com.example.nimish.technews;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class Sign_up_Activity extends AppCompatActivity {

    private static final Pattern Name_Pattern = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern UserID_Pattern =Pattern.compile("^[a-zA-Z]+([a-zA-Z0-9](_|-| )[a-zA-Z0-9])*[a-zA-Z0-9]+$");
    private static final Pattern Password_Pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=\\S+$)(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}");
    public TextInputLayout name,email,userId,password;
    ProgressBar pb;
    private static String url_create_user ="https://nstechnews.000webhostapp.com/TechNews/create_user.php";
    String email_input;
    String name_input;
    String userid_input;
    String password_input;
    Button signup;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(SharedPreferenceManager.getInstance(this).isLoggedIn()){
            finish();
            i=new Intent(Sign_up_Activity.this,News_feed.class);
            startActivity(i);
            return;
        }

        name=(TextInputLayout) findViewById(R.id.name_layout);
        email=(TextInputLayout) findViewById(R.id.email_id_layout);
        userId=(TextInputLayout) findViewById(R.id.user_ID_sign_up_layout);
        password=(TextInputLayout) findViewById(R.id.textInputLayout3);
        pb=(ProgressBar) findViewById(R.id.sign_up_progress);
        pb.setVisibility(View.GONE);
        signup=findViewById(R.id.sign_up_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail() | !validateName() | !validateUserid() | !validatePassword())
                {
                    pb.setVisibility(View.GONE);
                    return;
                }
                else
                {
                    if(online()) {
                        pb.setVisibility(View.VISIBLE);
                        regiterUser();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean validateEmail()
    {
        email_input = email.getEditText().getText().toString().trim();
        if(!(Patterns.EMAIL_ADDRESS.matcher(email_input).matches())){
            email.setError("Enter a valid Email ID");
            return false;
        }
        else {
            email.setError(null);
            return true;
        }
    }

    private boolean validateName()
    {
        name_input = name.getEditText().getText().toString().trim();
        if(!(Name_Pattern.matcher(name_input).matches()) || name_input.length()<2)
        {
            name.setError("Enter a valid Name");
            return false;
        }
        else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateUserid()
    {
        userid_input = userId.getEditText().getText().toString().trim();
        if(!(UserID_Pattern.matcher(userid_input).matches()))
        {
            userId.setError("Enter a valid UserID");
            return false;
        }
        else{
            userId.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        password_input = password.getEditText().getText().toString().trim();
        if(!Password_Pattern.matcher(password_input).matches())
        {
            password.setError("Password should Contain atleast: \n " +
                    "1 lowercase & Uppercase Letter \n " +
                    "1 numeric value & 1 special Character \n " +
                    "and length of minimum 8 Characters");
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

    public void regiterUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.register_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getInt("success")==1){
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        i = new Intent(Sign_up_Activity.this, MainActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",userid_input);
                params.put("name",name_input);
                params.put("email",email_input);
                params.put("user_password",password_input);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
