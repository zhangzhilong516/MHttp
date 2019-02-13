package zcdog.com.retrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.R;
import zcdog.com.mhttp.callback.HttpCallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.utils.LogUtils;

public class TestActivity extends AppCompatActivity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = findViewById(R.id.text_view);


        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("Accept-Version", "com.zcdog.customer+json;1.0");
        hashMap.put("VersionCode", "7021");
        hashMap.put("AppId", "zcdog");
        hashMap.put("VersionName", "5.7");
        hashMap.put("ChannelId", "normal");
        hashMap.put("parentTabId", "14");

        RetrofitClient.getServiceApi().getHomePageContentV3(hashMap)
                .compose(RetrofitClient.<JsonBean>transformer())
                .subscribe(new Subscriber<JsonBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonBean jsonBean) {
                        LogUtils.print(new Gson().toJson(jsonBean));
                    }
                });

    }

}
