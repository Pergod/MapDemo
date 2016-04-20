package com.geekband.mywork11;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;


/**
 * Created by Hyper on 2016/4/4.
 */
public class MapApplication extends Application {
    private static MapApplication mInstance = null;
    public BMapManager mBMapManager = null;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        ApiStoreSDK.init(this,"feb3c53abc6ace62005497a5a54f03fc");
        mInstance=this;
        initEngineManager(this);
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(MapApplication.getInstance().getApplicationContext(), "BMapManager  初始化错误!",Toast.LENGTH_SHORT).show();
        }
    }

    public static MapApplication getInstance() {
        return mInstance;
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(MapApplication.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
