package com.rjsgml1105.googlemapapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rjsgml1105.googlemapapi.model.Place;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
         {
             LocationManager locationManager;
             LocationListener locationListener;
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        place = (Place) getIntent().getSerializableExtra("place");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }

             // 마커 눌렀을때 처리할 코드 작성

             @Override
             public boolean onMarkerClick(@NonNull Marker marker) {


                 int tag = (int) marker.getTag();


                 return false;
             }

             // 맵이 준비되면 처리할 코드 작성.
             @Override
             public void onMapReady(@NonNull GoogleMap googleMap) {

                double lat = place.getGeometry().getLocation().getLat();
                double lng = place.getGeometry().getLocation().getLng();


                 // 내 위치정보를 가져와서,
                 LatLng center = new LatLng(lat,lng);


                 // 지도의 중심을 내 위치로 셋팅
                 googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,17));


                 // 마커를 만들어서, 지도에 표시
                 MarkerOptions markerOptions = new MarkerOptions();
                 markerOptions.position(center).title(place.getName());





             }
         }