package com.test.gpslogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class LoggerActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    boolean isActive = false;
    String num ;
    TextView text1,text2, text3;
    Button btn1,btn2;
    String user;
    int count = 0;
    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger);
        final String userName = getIntent().getStringExtra("user_name");
        final String no = getIntent().getStringExtra("no");
        user = userName;
        num = no;
        text1 = findViewById(R.id.textView);
        text2 = findViewById(R.id.textView2);
        text3 = findViewById(R.id.textView3);
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoggerActivity.this);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isActive = true;
                getLocation();

            }
        });
//        getLocation();

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive = false;
                Toast.makeText(LoggerActivity.this, "Stopped.", Toast.LENGTH_LONG).show();
                pushzero();
                finish();
            }
        });


    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                    currentLocation = location;
                    count++;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    text3.setText("refreshed: "+count);
                    text1.setText(String.valueOf(currentLocation.getLatitude()));
                    text2.setText(String.valueOf(currentLocation.getLongitude()));
                    pushdata(currentLocation.getLatitude(),currentLocation.getLongitude());
                    //SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    //assert supportMapFragment != null;
                    //supportMapFragment.getMapAsync(MapsActivity.this);
                    if(isActive){
                        refresh(2000);
                    }


            }
        });
    }

    private void refresh(int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoggerActivity.this);
                getLocation();
            }
        };

        handler.postDelayed(runnable, milliseconds);
    }

    private void pushdata(double lat,double longi) {
            AndroidNetworking.get("http://padma.soumit.tech/uploadsachin.php")
                    .addQueryParameter("us", String.valueOf(user))
                    .addQueryParameter("la",String.valueOf(lat))
                    .addQueryParameter("lo",String.valueOf(longi))
                    .addQueryParameter("no",String.valueOf(num))
                    .addQueryParameter("st","1")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int status = jsonObject.getInt("status");
                                String message = jsonObject.getString("message");
                                if(status == 0){
                                    Toast.makeText(LoggerActivity.this, "Unable To Upload Data" + message, Toast.LENGTH_SHORT).show();
                                }
                                else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoggerActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                        }
                    });

    }



    private void pushzero() {
        AndroidNetworking.get("http://padma.soumit.tech/uploadsachin.php")
                .addQueryParameter("us", String.valueOf(user))
                .addQueryParameter("la","0")
                .addQueryParameter("lo","0")
                .addQueryParameter("no","0")
                .addQueryParameter("st","0")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            if(status == 0){
                                Toast.makeText(LoggerActivity.this, "Unable To Upload Data" + message, Toast.LENGTH_SHORT).show();
                            }
                            else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoggerActivity.this, "Parsing Error", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }





}
