package com.example.trackfromdb;

import android.Manifest;

import android.app.AlertDialog;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.net.Uri;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.overlay.MultipartPathOverlay;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.CircleOverlay;

import java.util.Arrays;

//phpexample 합치려고 추가
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//값저장용
import android.content.SharedPreferences;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private NaverMap mMap;
    LatLng prev_LOC = null;
    LatLng curr_LOC;
    Marker mk = new Marker(); //아이마크
    Marker mk2 = new Marker(); //엄마마크
    Marker mk3 = new Marker(); //테스트마크
    CircleOverlay circle = new CircleOverlay();
    PathOverlay path = new PathOverlay();
    ArrayList<LatLng> PathLatLng = new ArrayList<LatLng>();

    //알람한번만켜저라
    Global global_wc;
    int WCcheck = 1;

    //값저장용
    //SharedPreferences a;
    String table;
    //String table = a.getString("mytablename", "");

    LocationManager locationManager;
    LocationListener locationListener;

    private static String IP_ADDRESS = "home114.myds.me";
    private static String TAG = "Location Tracking Parent";

    private String mJsonString;
    //private int count=0;
    int rowlength=-1;
    int alarmagain = 0;
    //private boolean isend =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //지도를 출력할 프래그먼트 인식
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map,mapFragment).commit();
        }

        //지도시작전 아이먼저확인
        SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
        table = a.getString("mytablename", "wrongchild");

        if (table == "wrongchild")
        {
            Intent intent = new Intent(getApplicationContext(), Access.class);
            //it.putExtra("it_tag", tag);
            startActivity(intent);
        }

        //지도 사용이 준비되면 onMapReady 콜백 메소드 호출
        mapFragment.getMapAsync(this);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //지도 객체를 여러 메소드에서 사용할 수 있도록 글로벌 객체로 할당
        mMap = naverMap;


        //GPS용
        locationListener = new LocationListener() {
            //위치가 변할 때마다 호출
            public void onLocationChanged(Location location) {
                //updateMap();
                P_Mark(location);
            }

            //위치 서비스가 변경될 때
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //alertStatus(provider);
            }

            //사용자에 의해 Provider가 사용 가능하게 설정될 때
            public void onProviderEnabled(String provider) {
                alertProvider(provider);
            }

            //사용자에 의해 provider가 사용 불가능하게 설정될 때
            public void onProviderDisabled(String provider) {
                checkProvider(provider);
            }
        };

        //시스템 위치 서비스 관리 객체 생성
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //정확한 위치 접근 권한이 설정되어 있지 않으면 사용자에게 권한 요구
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)

        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return; }

        String locationProvider;
        //GPS에 의한 위치 변경 요구
        locationProvider=LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider,1,1,locationListener);
        //통신사 기지국에 의한 위치 변경 요구
        locationProvider=LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider,1,1,locationListener);
        //GPS용


        updateMap();
        //Toast.makeText(MainActivity.this, "온맵레디호출", Toast.LENGTH_LONG).show();
    }

    //GPS용함수 3개
    public void checkProvider(String provider) {
        Toast.makeText(this, provider + "에 의한 위치서비스가 꺼져 있습니다. 켜주세요...",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
    public void alertProvider(String provider) {
        Toast.makeText(this, provider+"서비스가 켜졌습니다!",Toast.LENGTH_LONG).show();
    }
    public void alertStatus(String provider) {
        //Toast.makeText(this, "위치서비스가" + provider + "로 변경되었습니다!", Toast.LENGTH_LONG).show();
    }

    public void P_Mark(Location location){
        double p_latitude = location.getLatitude();
        double p_longitude = location.getLongitude();
        double between;
        LatLng P_LOC = new LatLng(p_latitude, p_longitude);

        LocationOverlay locationOverlay = mMap.getLocationOverlay();
        //보호자 위치 마커
        mk2.setVisible(false);
        mk2.setPosition(P_LOC);
        mk2.setMap(mMap);

        mk2.setIcon(OverlayImage.fromResource(R.drawable.mom));
        mk2.setWidth(90);
        mk2.setHeight(90);
        mk2.setZIndex(100);
        mk2.setVisible(true);

        //테스트용
        mk3.setVisible(false);
        mk3.setPosition(P_LOC);
        mk3.setMap(mMap);
        mk3.setWidth(70);
        mk3.setHeight(90);
        mk3.setZIndex(0);
        mk3.setVisible(true);

        //거리계산
        between = distance(p_latitude, p_longitude);

        SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
        int myradius = a.getInt("myradius", 100);

        //Toast.makeText(this, "between : " + between + "\nalarming_r : " + g_alarm,Toast.LENGTH_LONG).show();

        circle.setVisible(false);
        circle.setCenter(new LatLng(p_latitude, p_longitude));
        circle.setRadius(myradius);

        if(myradius == 0){
            circle.setVisible(false);
        }

        else if(between < myradius) {
            circle.setColor(0x6646AAEB);
            alarmagain = 0;
        }
        else{
            circle.setColor(0x66EB5A5A);
            alert();
        }
        circle.setMap(mMap);
        circle.setVisible(true);

        updateMap();


    }

    //거리계산 추가함수 3개
    private double distance(double lat2, double lon2){
        double lat1 = curr_LOC.latitude;
        double lon1 = curr_LOC.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000;

        //Toast.makeText(MainActivity.this, "거리계산 : " + dist + "m", Toast.LENGTH_LONG).show();
        return (dist);
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad){
        return (rad / Math.PI * 180.0);
    }

    public void updateMap(){

        SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
        table = a.getString("mytablename", "wrongchild");


        //Toast.makeText(MainActivity.this, "table = " + table, Toast.LENGTH_LONG).show();

        //한번만켜져라
        GetData task = new GetData();
        //인자추가
        task.execute("http://" + IP_ADDRESS + "/returntable.php",table);
    }


    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;
        String latitude;
        String longitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);

            if (result == null){
                //Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //원하는테이블받기
            String table = params[1];
            String serverURL = params[0];
            String postParameters = "table=" + table;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

        private void showResult(){
            String TAG_JSON="webnautes";
            String TAG_LATITUDE = "latitude";
            String TAG_LONGITUDE ="longitude";


            //Toast.makeText(MainActivity.this, "리절트 호출", Toast.LENGTH_LONG).show();

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                rowlength=jsonArray.length();

                PathLatLng.clear();
                //Toast.makeText(MainActivity.this, "클리어", Toast.LENGTH_LONG).show();
                for(int i=rowlength-10; i<rowlength;i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    latitude= item.getString(TAG_LATITUDE);
                    longitude= item.getString(TAG_LONGITUDE);

                    //Toast.makeText(MainActivity.this, "Result\nlat : " + latitude + "\nlong : " + longitude, Toast.LENGTH_LONG).show();

                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    curr_LOC = new LatLng(lati,longi);
                    PathLatLng.add(curr_LOC); //패스용 추가

                }
                //wrongchild면 걸러내기
                global_wc = (Global) getApplication();
                WCcheck = global_wc.getWc();
                //Toast.makeText(MainActivity.this, "wc = " + WCcheck, Toast.LENGTH_LONG).show();

                if (PathLatLng.get(3).latitude == 37 && PathLatLng.get(3).longitude == 126 && WCcheck == 1){
                    WCcheck = 0;
                    Toast.makeText(MainActivity.this, "등록된 아이가 없습니다!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Access.class);
                    //it.putExtra("it_tag", tag);
                    startActivity(intent);
                    //MainActivity.onCreate(Bundle );

                }

                //Toast.makeText(MainActivity.this, "어레이길이 : " + PathLatLng.size() + "\n3번째 lat : " + PathLatLng.get(3).latitude, Toast.LENGTH_LONG).show();
                Pathline();
                prev_LOC = null;

            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }
        }
    }

    public void Pathline() {
        //Toast.makeText(MainActivity.this, "패스라인호출", Toast.LENGTH_LONG).show();

        path.setCoords(Arrays.asList(
                new LatLng(PathLatLng.get(0).latitude, PathLatLng.get(0).longitude),
                new LatLng(PathLatLng.get(1).latitude, PathLatLng.get(1).longitude),
                new LatLng(PathLatLng.get(2).latitude, PathLatLng.get(2).longitude),
                new LatLng(PathLatLng.get(3).latitude, PathLatLng.get(3).longitude),
                new LatLng(PathLatLng.get(4).latitude, PathLatLng.get(4).longitude),
                new LatLng(PathLatLng.get(5).latitude, PathLatLng.get(5).longitude),
                new LatLng(PathLatLng.get(6).latitude, PathLatLng.get(6).longitude),
                new LatLng(PathLatLng.get(7).latitude, PathLatLng.get(7).longitude),
                new LatLng(PathLatLng.get(8).latitude, PathLatLng.get(8).longitude),
                new LatLng(PathLatLng.get(9).latitude, PathLatLng.get(9).longitude)
        ));
        path.setMap(mMap);
        path.setOutlineWidth(0);
        path.setPatternImage(OverlayImage.fromResource(R.drawable.yfootprint));
        path.setPatternInterval(50);
        //path.setOutlineColor(Color.BLACK);
        path.setColor(Color.TRANSPARENT);
        //path.setColor(Color.YELLOW);
        //path.setWidth(30);
        path.setWidth(60);

        mk.setVisible(false);
        mk.setPosition(curr_LOC);
        mk.setMap(mMap);
        mk.setVisible(true);
        mk.setIcon(OverlayImage.fromResource(R.drawable.kid));
        mk.setWidth(70);
        mk.setHeight(70);
    }


    //알람함수
    public void alert() {
        if(alarmagain == 0)
        {
            SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
            int myradius = a.getInt("myradius", 100);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher_background);
            builder.setMessage("아이가 " + myradius + "미터 반경을 넘어갔습니다!");
            builder.setTitle("알림");
            builder.setPositiveButton("확인",null);

            AlertDialog alertDialog;

            startSound();

            alertDialog=builder.create();
            alertDialog.show();
            alarmagain = 1;
        }
    }

    public void startSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    public void setting(View v){
        Intent intent = new Intent(getApplicationContext(), Setting.class);
        startActivity(intent);
    }
}