package zcdog.com.mhttp.request;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.utils.HttpUtils;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class OkhttpEngine extends BaseEngine {
    private OkHttpClient okHttpClient;

    public OkhttpEngine() {
        okHttpClient = new OkHttpClient();
        executorService = okHttpClient.dispatcher().executorService();
    }

    @Override
    public void initConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        okHttpClient.newBuilder().sslSocketFactory(httpConfig.getSslSocketFactory(), HttpUtils.UnSafeTrustManager)
                .hostnameVerifier(httpConfig.getHostnameVerifier())
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.MICROSECONDS).build();

    }

    @Override
    public void get(final GetRequest request, final ICallback callback) {
        Request.Builder builder = new Request.Builder()
                .get()
                .url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                builder.header(header.getKey(), header.getValue());
            }
        }
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                onError(callback,new ServerException(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess(result);
                    setCache(request,result);
                } else {
                    onError(callback,new ServerException(response.code(),response.message()));
                }
            }
        });

    }

    @Override
    public void post(final PostRequest request, final ICallback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(request.contentType()),
                request.getPostBody());

        Request.Builder builder = new Request.Builder()
                .post(requestBody)
                .url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                builder.header(header.getKey(), header.getValue());
            }
        }
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
               onError(callback,new ServerException(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess(result);
                    setCache(request,result);
                } else {
                    onError(callback,new ServerException(response.code(),response.message()));
                }
            }
        });
    }

    @Override
    public String get(GetRequest request) throws ServerException {
        Request.Builder builder = new Request.Builder()
                .get()
                .url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                builder.header(header.getKey(), header.getValue());
            }
        }
        try {
            Response execute = okHttpClient.newCall(builder.build()).execute();
            if (execute.isSuccessful()) {
                String response = execute.body().string();
                LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "NetworkResponse==" + response);
                setCache(request,response);
                return response;
            } else {
                throw new ServerException(execute.code(),execute.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public String post(PostRequest request) throws ServerException {
        RequestBody requestBody = null;
        if (ContentType.Form_MediaType.equals(request.contentType())) {
            FormBody.Builder builder = new FormBody.Builder();
            if (request.body() instanceof HashMap) {
                HashMap<String, Object> params = (HashMap<String, Object>) request.body();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (param.getValue() == null) {
                        continue;
                    }
                    builder.add(param.getKey(), (String) param.getValue());
                }
                requestBody = builder.build();
            }
        } else if (ContentType.Json_MediaType.equals(request.contentType())) {
            requestBody = RequestBody.create(MediaType.parse(request.contentType()),
                    MHttpClient.getInstance().getJsonConvert().toJsonString(request.body()));
        } else {
            requestBody = RequestBody.create(MediaType.parse(request.contentType()), (String) request.body());
        }
        Request.Builder builder = new Request.Builder()
                .post(requestBody)
                .url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                builder.header(header.getKey(), header.getValue());
            }
        }
        try {
            Response execute = okHttpClient.newCall(builder.build()).execute();
            if (execute.isSuccessful()) {
                String response = execute.body().string();
                setCache(request,response);
                return response;
            } else {
                throw new ServerException(execute.code(),execute.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
