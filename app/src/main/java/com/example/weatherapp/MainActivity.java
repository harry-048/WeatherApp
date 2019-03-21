package com.example.weatherapp;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView txtmain;
    TextView txrDescription;
    TextView txttemp;
    TextView txtHumidity;
    TextView txtPressure;
    TextView txtlocation;
    Button btnweather;
    double latitude;
    double longitude;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        editText = (EditText) findViewById(R.id.editText);
        txtmain = (TextView) findViewById(R.id.txtweatherResult);
        txrDescription = (TextView) findViewById(R.id.description);
        txttemp = (TextView) findViewById(R.id.temperature);
        txtHumidity = (TextView) findViewById(R.id.humidity);
        txtPressure = (TextView) findViewById(R.id.pressure);
        txtlocation = (TextView) findViewById(R.id.place);
        btnweather = (Button) findViewById(R.id.button);
        btnweather.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("latitude",10.0558383333);
            longitude = extras.getDouble("longitude",76.354331);
           // Toast.makeText(getBaseContext(),latitude+" and "+longitude,Toast.LENGTH_SHORT).show();
            Log.d("finalloaction",latitude+" and "+longitude);
         //   btnweather.setVisibility(View.VISIBLE);

            DownloadTask weather = new DownloadTask();
            weather.execute("https://openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=b6907d289e10d714a6e88b30761fae22");

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
            //The key argument here must match that used in the other activity
        }
    }

    public void getWeather(View view) {
        DownloadTask weather = new DownloadTask();
        weather.execute("https://openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    public void openMap(View view) {

        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        startActivity(intent);
        finish();
    }

    public void fromCityName(String s){


    }

    public void fromLatAndLong(String s){
        try {
            btnweather.setVisibility(View.INVISIBLE);
            JSONObject jsonObject = new JSONObject(s);
            String weatherInfo = jsonObject.getString("weather");
            String main;
            String description;
            String temp;
            String place;
            String pressure;
            String humidity;
            // Log.d("temper",jsonObject.getJSONObject("main").getString("temp"));
            String message = "";
            if(weatherInfo!=null){
                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++){
                    JSONObject jsonpart = arr.getJSONObject(i);

                    main = jsonpart.getString("main");
                    description = jsonpart.getString("description");
                    if(main!=""&&description!=""){
                        txtmain.setText(main);
                        txrDescription.setText(description);
                        message=main+","+description;
                    }
                }
            }

            temp=jsonObject.getJSONObject("main").getString("temp")+" degree";
            pressure=jsonObject.getJSONObject("main").getString("pressure")+" hpa";
            humidity=jsonObject.getJSONObject("main").getString("humidity")+" %";
            place=jsonObject.getString("name");

            txtHumidity.setText(humidity);
            if (place==null||place==""){
                txtlocation.setText("Unknown Location");
            }
            else {
                txtlocation.setText(place);
            }

            txtPressure.setText(pressure);
            txttemp.setText(temp);

            if(message!=""){
                //  txtweatherResult.setText(message);
            }
            else
                Toast.makeText(getBaseContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            fromLatAndLong(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data!= -1){
                    char current = (char) data;
                    result+=current;
                    data= reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

                return null;

            }


        }

    }
}
