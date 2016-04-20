package com.geekband.mywork11;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.ImageMarker;
import com.baidu.lbsapi.panoramaview.OnTabMarkListener;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.TextMarker;
import com.baidu.lbsapi.tools.Point;

/**
 * 全景主Activity
 */
public class PanoramaActivity extends Activity {

    private PanoramaView mPanoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 先初始化BMapManager
        initBMapManager();
        setContentView(R.layout.panoramaview_main);
        mPanoView = (PanoramaView) findViewById(R.id.panorama);
        Intent intent = getIntent();
        if (intent != null) {
            showPanoByCoordinate(intent.getDoubleArrayExtra(MainActivity.COORDINATE));
        }
    }

    private void showPanoByCoordinate(double[] coordinate) {
        mPanoView.setShowTopoLink(true);
        mPanoView.setPanorama(coordinate[0], coordinate[1]);
        mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
    }

    private void initBMapManager() {
        MapApplication app = (MapApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new MapApplication.MyGeneralListener());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }

}
