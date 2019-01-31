package zcdog.com.mhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.HttpCallback;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.request.HttpEngine;

public class TestActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = findViewById(R.id.text_view);
//        MHttpClient.getInstance().exchangeEngine(new HttpEngine());
//        MHttpClient.get()
//                .url("www.baidu.com")
//                .callBack(new ICallback() {
//            @Override
//            public void onSuccess(final String result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText(result);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(ServerException e) {
//                e.printStackTrace();
//            }
//        });

//        MHttpClient.get()
//                .addHeader("Accept-Version", "com.zcdog.customer+json;1.0")
//                .addHeader("VersionCode", "7021")
//                .addHeader("AppId", "zcdog")
//                .addHeader("VersionName", "5.7")
//                .addHeader("ChannelId", "normal")
//                .addParam("parentTabId", "-1")
//                .url("https://apis.zcdog.com:50183/api/user/userMgr/user/getHomePageContentV3")
//                .callBack(new HttpCallback<String>() {
//                    @Override
//                    public void onSuccess(final String s) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                textView.setText(s);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(ServerException e) {
//                        e.printStackTrace();
//                    }
//                });
        MHttpClient.post()
                .url("http://l-zcgtest10.dev.cn2.corp.agrant.cn:9201/api/mall/getCommodityDetail")
                .addParam("commodityId","ZMCOMD958170821110656555")
                .addHeader("Accept-Version", "com.zcdog.mall+json;1.0")
                .addHeader("VersionCode", "7021")
                .addHeader("AppId", "zcdog")
                .addHeader("VersionName", "5.7")
                .addHeader("ChannelId", "normal")
                .cacheMode(CacheMode.NO_CACHE)
                .callBack(new HttpCallback<String>() {
                    @Override
                    public void onError(ServerException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(s);
                            }
                        });
                    }
                });
    }
}
