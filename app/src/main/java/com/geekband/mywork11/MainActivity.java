package com.geekband.mywork11;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String CITY_NAME = "cityName";
    public static final String COORDINATE="coordinate";

    private MapView mMapView =null;
    public BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private ImageButton mapImagebutton;
    private ImageButton sateliteImagebutton;
    private ImageButton trafficImageButton;
    private ImageButton heatImageButton;
    private ImageButton navigateImageButton;
    private ImageButton searchImageButton;
    private ImageButton panoramaImageButton;
    private ImageButton modeImageButton;

    private int flagMap=0;
    private int flagSatelite=0;
    private int flagtTraffic =0;
    private int flagHeat=0;
    private int flagMode=0;

    private LatLng latLng;
    private Boolean isFirstLocate=true;
    private StringBuffer cityName=new StringBuffer();

    private double[] coordinate=new double[2];
    private SensorManager sensorManager;
    private Sensor sensor;
    private MyLocationConfiguration.LocationMode mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setClickMethod();

        mBaiduMap=mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        setSensor();
        initLocation();
    }

    public void setSensor() {
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void findViews() {
        mMapView = (MapView) findViewById(R.id.baidu_map_view);
        navigateImageButton= (ImageButton) findViewById(R.id.navigate_image_button);
        trafficImageButton= (ImageButton) findViewById(R.id.traffic_image_button);
        mapImagebutton= (ImageButton) findViewById(R.id.map_image_button);
        sateliteImagebutton = (ImageButton) findViewById(R.id.satlite_image_button);
        heatImageButton= (ImageButton) findViewById(R.id.heat_image_button);
        searchImageButton= (ImageButton) findViewById(R.id.search_image_button);
        panoramaImageButton= (ImageButton) findViewById(R.id.panorama_image_button);
    }

    public void setClickMethod() {
        searchImageButton.setOnClickListener(this);
        mapImagebutton.setOnClickListener(this);
        sateliteImagebutton.setOnClickListener(this);
        trafficImageButton.setOnClickListener(this);
        heatImageButton.setOnClickListener(this);
        navigateImageButton.setOnClickListener(this);
        panoramaImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_image_button:
                if (flagMap==0)
                {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    flagMap=1;
                }
                else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                    flagMap=0;
                }
                break;

            case R.id.satlite_image_button:
                if (flagSatelite==0){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    flagSatelite=1;
                }else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    flagSatelite=0;
                }
                break;

            case R.id.traffic_image_button:
                if (flagtTraffic ==0){
                    mBaiduMap.setTrafficEnabled(true);
                    flagtTraffic =1;
                }else {
                    mBaiduMap.setTrafficEnabled(false);
                    flagtTraffic =0;
                }
                break;

            case R.id.heat_image_button:
                if (flagHeat==0){
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    flagHeat=1;
                }else {
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                    flagHeat=0;
                }
                break;
            case R.id.search_image_button:
                Intent searchIntent=new Intent(MainActivity.this,PoiSearchActivity.class);
                searchIntent.putExtra(CITY_NAME,cityName.toString());
                startActivity(searchIntent);
                break;

            case R.id.navigate_image_button:
                Intent navigateIntent=new Intent(MainActivity.this,RoutePlanActivity.class);
                navigateIntent.putExtra(CITY_NAME,cityName.toString());
                startActivity(navigateIntent);
                break;

            case R.id.panorama_image_button:
                Intent panoramaIntent=new Intent(MainActivity.this,PanoramaActivity.class);
                panoramaIntent.putExtra(COORDINATE,coordinate);
                startActivity(panoramaIntent);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span=1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private SensorEventListener listener=new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float xValue=Math.abs(event.values[0]);
            float yValue=Math.abs(event.values[1]);
            float zValue=Math.abs(event.values[2]);
            if (xValue>15||yValue>15||zValue>15){
//                Toast.makeText(MainActivity.this,"Succeed",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra(CITY_NAME,cityName.toString());
                startActivity(intent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            LocationTo(location);
        }
    }

    public void LocationTo(BDLocation location) {
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.map_marker);
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfigeration(config);

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(0).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);

        if (isFirstLocate) {
            coordinate[0]=location.getLongitude();
            coordinate[1]=location.getLatitude();

            cityName.append(location.getCity());
            cityName.deleteCharAt(cityName.length()-1);
            Log.i("MainActivity : ",cityName +","+ location.getLocType()+","+cityName);

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(latLng).zoom(16f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            isFirstLocate=false;
        }
    }
}
