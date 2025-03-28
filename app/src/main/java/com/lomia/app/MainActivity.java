package com.lomia.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText cityInput = null;
    private Button fetchButton = null;
    private TextView tempView = null;
    private TextView humidityView = null;
    private TextView descView = null;
    private ImageView weatherIcon = null;
    private ProgressBar loading = null;
    private SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityEditText);
        fetchButton = findViewById(R.id.getWeatherButton);
        tempView = findViewById(R.id.temperature);
        humidityView = findViewById(R.id.humidity);
        descView = findViewById(R.id.description);
        weatherIcon = findViewById(R.id.weatherIcon);
        loading = findViewById(R.id.progressBar);
        sharedPreferences = getSharedPreferences("WeatherApp", MODE_PRIVATE);

        fetchButton.setOnClickListener(listener);
    }

    private void getWeatherInfo(String city) {
        loading.setVisibility(View.VISIBLE);
        String apiKey = BuildConfig.API_KEY;
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" +
                apiKey + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                loading.setVisibility(View.GONE);
                try {
                    JSONObject main = response.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    int humidity = main.getInt("humidity");
                    JSONObject weather = response.getJSONArray("weather").getJSONObject(0);
                    String description = weather.getString("description");
                    String icon = weather.getString("icon");
                    tempView.setText("Temperature: " + temp + " ℃");
                    humidityView.setText("Humidity: " + humidity + "%");
                    descView.setText("Condition: " + description);
                    String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
                    Glide.with(MainActivity.this).load(iconUrl).into(weatherIcon);
                    sharedPreferences.edit().putString("lastCity", city).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                loading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to fetch data",
                        Toast.LENGTH_SHORT).show();
            });

        queue.add(request);
    };

    private final View.OnClickListener listener = view -> {
        if (view.getId() == R.id.getWeatherButton) {
            String city = cityInput.getText().toString();
            if (city.isEmpty()) {
                city = sharedPreferences.getString("lastCity", "Toronto");
            }
            getWeatherInfo(city);
        }
    };
}

