package zcdog.com.mhttp.engine;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.FileCallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.request.ContentType;
import zcdog.com.mhttp.request.DownloadRequest;
import zcdog.com.mhttp.request.GetRequest;
import zcdog.com.mhttp.request.OkMultipartBody;
import zcdog.com.mhttp.request.PostRequest;
import zcdog.com.mhttp.request.UploadRequest;
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

    /**
     * **********************************GET*************************************
     */

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
                setCache(request, response);
                return response;
            } else {
                throw new ServerException(execute.code(), execute.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
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
                onError(callback, new ServerException(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess(result);
                    setCache(request, result);
                } else {
                    onError(callback, new ServerException(response.code(), response.message()));
                }
            }
        });
    }

    /**
     * **********************************POST*************************************
     */

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
                setCache(request, response);
                return response;
            } else {
                throw new ServerException(execute.code(), execute.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
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
                onError(callback, new ServerException(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    callback.onSuccess(result);
                    setCache(request, result);
                } else {
                    onError(callback, new ServerException(response.code(), response.message()));
                }
            }
        });
    }

    /**
     * **********************************DOWNLOAD*************************************
     */
    @Override
    public File downloadFile(DownloadRequest request) throws ServerException {
        try {
            Request okRequest = new Request.Builder().url(request.url()).build();

            Response response = okHttpClient.newCall(okRequest).execute();
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();

                File dir = new File(request.getDesPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, request.getFileName());
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 8];
                int length;
                while ((length = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, length);
                }
                fos.flush();
                inputStream.close();
                fos.close();
                return file;
            } else {
                throw new ServerException(response.code(), response.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }


    @Override
    public void downloadFile(final DownloadRequest request, final FileCallback fileCallback) {
        Request okRequest = new Request.Builder().url(request.url()).build();
        okHttpClient.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                fileCallback.onError(new ServerException(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                File dir = new File(request.getDesPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, request.getFileName());
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 8];
                int length;
                long totalLength = response.body().contentLength();
                long currLength = 0;
                while ((length = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, length);
                    currLength += length;
                    if (fileCallback != null) {
                        fileCallback.onProgress(totalLength, currLength);
                    }
                }
                fos.flush();
                inputStream.close();
                fos.close();
                fileCallback.onSuccess(file);
            }
        });
    }

    @Override
    public InputStream download(DownloadRequest request) throws ServerException {
        try {
            Request okRequest = new Request.Builder().url(request.url()).build();

            Response response = okHttpClient.newCall(okRequest).execute();
            if (response.isSuccessful()) {
                return response.body().byteStream();
            } else {
                throw new ServerException(response.code(), response.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }


    /**
     * **********************************UPLOAD*************************************
     */

    @Override
    public String uploadFile(UploadRequest request) throws ServerException {
        final Request.Builder okRequestBuilder = new Request.Builder().url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                okRequestBuilder.header(header.getKey(), header.getValue());
            }
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (request.params() != null) {
            for (Map.Entry<String, Object> param : request.params().entrySet()) {
                Object value = param.getValue();

                if(value instanceof String){
                    builder.addFormDataPart(param.getKey(), (String) param.getValue());
                }else if(value instanceof File){
                    File file = (File) value;
                    builder.addFormDataPart(param.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                }else if(value instanceof List){
                    List list = (List) value;
                    for (int i = 0; i < list.size() ; i++) {
                        Object obj = list.get(i);
                        if(obj instanceof File){
                            File file = (File) obj;
                            builder.addFormDataPart(param.getKey(), file.getName(),
                                    RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                        }else {
                            throw new IllegalArgumentException();
                        }
                    }
                }else {
                    if(value == null){
                        builder.addFormDataPart(param.getKey(), "");
                    }else {
                        builder.addFormDataPart(param.getKey(), (String) value);
                    }
                }
            }
        }

        OkMultipartBody okMultipartBody = new OkMultipartBody(builder.build());

        Request okRequest = okRequestBuilder
                .post(okMultipartBody)
                .build();
        try {
            Response execute = okHttpClient.newCall(okRequest).execute();
            if (execute.isSuccessful()) {
                String response = execute.body().string();
                LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "NetworkResponse==" + response);
                return response;
            } else {
                throw new ServerException(execute.code(), execute.message());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public void uploadFile(final UploadRequest request, final ICallback callback) {
        final Request.Builder okRequestBuilder = new Request.Builder().url(request.url());

        if (request.headers() != null) {
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                if (header.getValue() == null) {
                    continue;
                }
                okRequestBuilder.header(header.getKey(), header.getValue());
            }
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (request.params() != null) {
            for (Map.Entry<String, Object> param : request.params().entrySet()) {
                Object value = param.getValue();

                if(value instanceof String){
                    builder.addFormDataPart(param.getKey(), (String) param.getValue());
                }else if(value instanceof File){
                    File file = (File) value;
                    builder.addFormDataPart(param.getKey(), file.getName(),
                            RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                }else if(value instanceof List){
                    List list = (List) value;
                    for (int i = 0; i < list.size() ; i++) {
                        Object obj = list.get(i);
                        if(obj instanceof File){
                            File file = (File) obj;
                            builder.addFormDataPart(param.getKey(), file.getName(),
                                    RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                        }else {
                            throw new IllegalArgumentException();
                        }
                    }
                }else {
                    if(value == null){
                        builder.addFormDataPart(param.getKey(), "");
                    }else {
                        builder.addFormDataPart(param.getKey(), (String) value);
                    }
                }
            }
        }
        OkMultipartBody okMultipartBody = new OkMultipartBody(builder.build(),callback);

        final Request okRequest = okRequestBuilder
                .post(okMultipartBody).build();
        okHttpClient.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                onError(callback, new ServerException(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "NetworkResponse==" + responseStr);
                    callback.onSuccess(responseStr);
                } else {
                    onError(callback, new ServerException(response.code(), response.message()));
                }
            }
        });
    }
}
