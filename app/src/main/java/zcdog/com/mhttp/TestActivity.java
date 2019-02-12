package zcdog.com.mhttp;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.FileCallback;
import zcdog.com.mhttp.callback.HttpCallback;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.engine.HttpEngine;
import zcdog.com.mhttp.engine.OkhttpEngine;
import zcdog.com.mhttp.utils.LogUtils;

public class TestActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = findViewById(R.id.text_view);

        MHttpClient.getInstance().exchangeEngine(new HttpEngine());
        MHttpClient.get()
                .url("https://www.baidu.com")
                .callBack(new ICallback() {
                    @Override
                    public void onSuccess(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(result);
                            }
                        });
                    }

                    @Override
                    public void onError(ServerException e) {
                        e.printStackTrace();
                    }
                });

        MHttpClient.get()
                .addHeader("Accept-Version", "com.zcdog.customer+json;1.0")
                .addHeader("VersionCode", "7021")
                .addHeader("AppId", "zcdog")
                .addHeader("VersionName", "5.7")
                .addHeader("ChannelId", "normal")
                .addParam("parentTabId", "14")
                .url("https://apis.zcdog.com:50183/api/user/userMgr/user/getHomePageContentV3")
                .callBack(new HttpCallback<String>() {
                    @Override
                    public void onSuccess(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(s);
                            }
                        });
                    }

                    @Override
                    public void onError(ServerException e) {
                        e.printStackTrace();
                    }
                });

        new Thread() {
            @Override
            public void run() {
                try {
                    final String response = MHttpClient.get()
                            .addHeader("Accept-Version", "com.zcdog.customer+json;1.0")
                            .addHeader("VersionCode", "7021")
                            .addHeader("AppId", "zcdog")
                            .addHeader("VersionName", "5.7")
                            .addHeader("ChannelId", "normal")
                            .addParam("parentTabId", "14")
                            .url("https://apis.zcdog.com:50183/api/user/userMgr/user/getHomePageContentV3").execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(response);
                        }
                    });
                } catch (ServerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                try {
                    final String response = MHttpClient.post()
                            .url("http://l-zcgtest10.dev.cn2.corp.agrant.cn:9201/api/mall/getCommodityDetail")
                            .addParam("commodityId", "ZMCOMD958170821110656555")
                            .addHeader("Accept-Version", "com.zcdog.mall+json;1.0")
                            .addHeader("VersionCode", "7021")
                            .addHeader("AppId", "zcdog")
                            .addHeader("VersionName", "5.7")
                            .addHeader("ChannelId", "normal")
                            .execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(response);
                        }
                    });
                } catch (ServerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "DiskCache");
//        MHttpClient.download()
//                .desPath(file.getAbsolutePath())
//                .url("http://static.zcdog.com/zcdog/apk/mall/6.0/1/Mall6.0_zcdog_web.apk")
//                .callBack(new FileCallback() {
//                    @Override
//                    public void onProgress(final long total, final long curr) {
//                        runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        textView.setText(curr * 100 /total+ "");
//                                    }
//                                });
//                        LogUtils.print("total======" + total + "curr ================" + curr );
//                    }
//
//                    @Override
//                    public void onSuccess(File file) {
//                        LogUtils.print("======================下载成功！=================" + file.getAbsolutePath());
//                    }
//
//                    @Override
//                    public void onError(ServerException e) {
//                        e.printStackTrace();
//                    }
//                });
        File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "test.jpg");
        MHttpClient.upload()
                .url("https://apis.zcdog.com:50183/api/user/userMgr/user/uploadHeadIcon")
                .addFile("headIcon",file1)
                .addHeader("Accept-Version", "com.zcdog.customer+json;1.0")
                .addHeader("Token","67da40d0-3dfc-4099-9b3e-7061db7bc48d")
                .callBack(new HttpCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        LogUtils.print(s);
                    }

                    @Override
                    public void onError(ServerException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onProgress(long total, long curr) {
                        LogUtils.print("total ===" + total + "; curr ===" + curr);
                    }
                });
    }
}
