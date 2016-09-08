package com.kka.weather.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kka.weather.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private LatLng currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //처음 시작 위치

        LatLng seoulStation = new LatLng(35,35);
        mMap.addMarker(new MarkerOptions().position(seoulStation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoulStation));

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        //날씨 정보 다이얼로그 띄우기
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {


        mMap.addMarker(new MarkerOptions().position(latLng));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                //말풍선 띄우기
                return null;
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {

        currentPosition = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15), 1000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //쉐어드 프리퍼런스에 좌표 저장해서 앱 켤 때 마지막 내 위치로 자동 설정하기
                mMap.addMarker(new MarkerOptions().position(currentPosition));

            }

            @Override
            public void onCancel() {
            }
        });

        return true;
    }
}
