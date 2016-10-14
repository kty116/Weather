package com.kka.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kka.weather.ParsingService;
import com.kka.weather.R;
import com.kka.weather.dialog.CurrentWeatherDialog;
import com.kka.weather.model.GeoParsingModel;
import com.kka.weather.model.WeatherParsingModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener, View.OnKeyListener {

    private GoogleMap mMap;
    private LatLng currentPosition;
    private String TAG = MapsActivity.class.getSimpleName();
    private EditText searchAdress;
    private ImageButton searchAButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchAdress = (EditText) findViewById(R.id.search_adress);
        searchAButton = (ImageButton) findViewById(R.id.search_button);

        searchAdress.setOnKeyListener(this);
        searchAButton.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //처음 시작 위치
        LatLng seoulStation = new LatLng(37.5545168, 126.9706483);
        mMap.addMarker(new MarkerOptions().position(seoulStation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulStation, 15));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 퍼미션 결과를 요청하는 곳
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(MapsActivity.this, "권한 사용을 동의하지않아 앱을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        marker.setTitle("날씨 보기");
        marker.showInfoWindow();

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng).title("날씨 보기")).showInfoWindow();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(MapsActivity.this, "위치 서비스를 이용하시려면 GPS를 켜주세요", Toast.LENGTH_SHORT).show();
        } else if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            currentPosition = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15), 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.addMarker(new MarkerOptions().position(currentPosition).title("날씨 보기")).showInfoWindow();

                }

                @Override
                public void onCancel() {
                }
            });
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        //날씨 정보 다이얼로그 띄우기
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            Toast.makeText(MapsActivity.this, "인터넷을 켜야 해당 서비스를 이용하실 수 있습니다", Toast.LENGTH_SHORT).show();
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ParsingService.weatherUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ParsingService parsingService = retrofit.create(ParsingService.class);

            Call<WeatherParsingModel> convertedWeather = parsingService.getWeather("" + marker.getPosition().latitude, "" + marker.getPosition().longitude, "3050935e004522d9aa21c84795e5ab7e");

            convertedWeather.enqueue(new Callback<WeatherParsingModel>() {
                @Override
                public void onResponse(Call<WeatherParsingModel> call, Response<WeatherParsingModel> response) {
                    if (!(response.body().getWeather().get(0).getMain() == null)) {

                        String weather = response.body().getWeather().get(0).getMain();
                        String description = response.body().getWeather().get(0).getDescription();
                        String temp = response.body().getTemp();

                        Intent weatherDialogIntent = new Intent(MapsActivity.this, CurrentWeatherDialog.class);
                        weatherDialogIntent.putExtra("weather", weather);
                        weatherDialogIntent.putExtra("description", description);
                        weatherDialogIntent.putExtra("temp", temp);
                        startActivity(weatherDialogIntent);
                        Log.d(TAG, "성공");
                    } else {
                        Toast.makeText(MapsActivity.this, "날씨 정보가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherParsingModel> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.toString());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(searchAdress.getWindowToken(), 0);

        String adressText = searchAdress.getText().toString().trim();
        if (!TextUtils.isEmpty(adressText)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ParsingService.geocodingUrl)
                    .addConverterFactory(GsonConverterFactory.create()).build();

            ParsingService service = retrofit.create(ParsingService.class);

            Call<GeoParsingModel> convertedGeo = service.getGeo(adressText, "ko", "AIzaSyBUoaBT3vjE5gvAOYtu7xlgXwKwXVLrDA0");

            convertedGeo.enqueue(new Callback<GeoParsingModel>() {
                @Override
                public void onResponse(Call<GeoParsingModel> call, Response<GeoParsingModel> response) {

                    if (response.body().getStatus().equals("OK")) {

                        Snackbar.make(getWindow().getDecorView().getRootView(), response.body().getResults().get(0).getFormatted_address(), 6000).show();
                        double lat = response.body().getResults().get(0).getLat();
                        double lon = response.body().getResults().get(0).getLon();

                        final LatLng searchPosition = new LatLng(lat, lon);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPosition, 15), 1000, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                mMap.addMarker(new MarkerOptions().position(searchPosition).title("날씨 보기")).showInfoWindow();

                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                    } else {
                        Toast.makeText(MapsActivity.this, "주소를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GeoParsingModel> call, Throwable t) {
                    Log.d(TAG, "" + t);
                }
            });
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {

            onClick(searchAButton);
            return true;
        }
        return false;
    }
}
