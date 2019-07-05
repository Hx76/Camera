package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends Activity {
    private MapView mMapView = null;
    @RequiresApi(api = Build.VERSION_CODES.M-1)
    void Request() {             //获取相机拍摄读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本判断
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SYNC_SETTINGS,
                        Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WAKE_LOCK}, 1);
            }
        }
    }
    private double change(String string) {
        double num = 0.0;
        if (null==string){
            return num;
        }
        //用 ，将数值分成3份
        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            //用112/1得到度分秒数值
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            //将分秒分别除以60和3600得到度，并将度分秒相加
            num=num+v/Math.pow(60,i);
        }
        return num;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        double lat = change(latitude);
        double lon = change(longitude);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = findViewById(R.id.bmapView);
        BaiduMap baiduMap = mMapView.getMap();
        MapStatusUpdate update = MapStatusUpdateFactory.zoomTo(12.5f);
        baiduMap.animateMapStatus(update);
        LatLng ll = new LatLng(lat,lon);
        MapStatusUpdate update1 = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update1);
        Request();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

}
