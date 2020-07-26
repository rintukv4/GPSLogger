package com.test.gpslogger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText name;
    EditText pass;
    Button signin,out;
    private ProgressBar loading;
    private SharedPreferences sharedpreferences;
    String url = "http://padma.soumit.tech/login1.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        signin = findViewById(R.id.signin);
        loading = findViewById(R.id.progresslogin);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUser = name.getText().toString().trim();
                String mPass = pass.getText().toString().trim();

                if (!mUser.isEmpty() || !mPass.isEmpty()) {
                    login(mUser, mPass);
                } else {
                    name.setError("Please Enter Username");
                    pass.setError("Please Enter Password");
                }
            }
        });
    }

    public void login(final String mUser, final String mPass) {
        loading.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Successful."+response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){
                                for( int i=0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String user = object.getString("user").trim();
                                    String no = object.getString("no").trim();
                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Login Successful. \nUser :"+user, Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(MainActivity.this,LoggerActivity.class);
                                    intent.putExtra("user_name", user);
                                    intent.putExtra("no", no);
                                    startActivity(intent);
                                }

                            }
                            else{
                                loading.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "User Id Or Password Incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Login Unsuccessful 111s."+e.toString(), Toast.LENGTH_LONG).show();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Login Unsuccessful 1."+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("us",mUser);
                params.put("pa",mPass);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}

