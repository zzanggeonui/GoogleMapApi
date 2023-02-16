package com.rjsgml1105.googlemapapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rjsgml1105.googlemapapi.adpater.PlaceAdapter;
import com.rjsgml1105.googlemapapi.api.NetworkClient;
import com.rjsgml1105.googlemapapi.api.PlaceApi;
import com.rjsgml1105.googlemapapi.config.Config;
import com.rjsgml1105.googlemapapi.model.Place;
import com.rjsgml1105.googlemapapi.model.PlaceList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText editKeyword;
    ProgressBar progressBar;
    ImageView imgSearch;



    PlaceAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();

    // 내위치정보 관련
    LocationManager locationManager;
    LocationListener locationListener;

    double currentlat;
    double currentlng;


    String keyword;
    String  pagetoken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 보이면!!!!
                // 네트워크 통해서 데이터를 추가로 받아와라!!
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition +1 == totalCount){
                    if(pagetoken != null){
                        addNetworkData();

                    }
                }

            }
        });



        editKeyword = findViewById(R.id.editKeyword);
        imgSearch = findViewById(R.id.imgSearch);

        // api 호출에 필요한 위치정보 불러오기
        // 위치를 가져오기 위해서는, 시스템서비스로부터
        // 로케이션 메니저를 받아온다.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 로케이션 리스너를 만든다.
        // 위치가 변할때마다 호출되는 함수 작성!
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // 위도 경도값을 여기서 뽑아내서, 우리에 맞는 코드를 작성.
                currentlat = location.getLatitude();
                currentlng = location.getLongitude();
            Log.i("myLocation",""+currentlat);

            }
        };
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)  !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)  !=
                        PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        // 위치기반으로 GPS 정보 가져오는 코드를 실행하는 부분
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener);


        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = editKeyword.getText().toString().trim();

                if(keyword.isEmpty()){
                    Toast.makeText(MainActivity.this, "필수 항목 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentlat == 0){
                    Toast.makeText(MainActivity.this, "위치를 찾는 중입니다. 잠시후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //네트워크로 api 호출해서 데이터 받아오고 화면에 표시하기
                getNetworkData();

            }
        });

    }

    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceLIST(keyword,
                currentlat+"," + currentlng,
                2000,
                "ko",
                Config.MAPS_API_KEY,
                pagetoken
        );

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){
                    pagetoken = response.body().getNext_page_token();
                    placeArrayList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();

                }else{

                }

            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void getNetworkData() {
        progressBar.setVisibility(View.VISIBLE);

        pagetoken = "";

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);

        Call<PlaceList> call = api.getPlaceLIST(keyword,
              currentlat+"," + currentlng,
                2000,
                "ko",
                Config.MAPS_API_KEY,
                pagetoken
                );
            call.enqueue(new Callback<PlaceList>() {
                @Override
                public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                    progressBar.setVisibility(View.GONE);

                    if(response.isSuccessful()){
                        pagetoken = response.body().getNext_page_token();
                        placeArrayList.clear();
                        placeArrayList.addAll(response.body().getResults());

                        adapter = new PlaceAdapter(MainActivity.this ,placeArrayList);

                        adapter.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
                            @Override
                            public void onCardViewClick(int index) {
                                Place place = placeArrayList.get(index);

                                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                                intent.putExtra("place", place);

                                startActivity(intent);

                            }
                        });
                        recyclerView.setAdapter(adapter);

                    }else {

                    }
                }

                @Override
                public void onFailure(Call<PlaceList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){

            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)  !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)  !=
                            PackageManager.PERMISSION_GRANTED
            ){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
                return;
            }

            // 위치기반으로 GPS 정보 가져오는 코드를 실행하는 부분
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,
                    -1,
                    locationListener);

        }
    }



}