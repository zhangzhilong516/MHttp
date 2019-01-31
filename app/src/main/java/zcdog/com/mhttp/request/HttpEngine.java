package zcdog.com.mhttp.request;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.utils.HttpUtils;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public class HttpEngine extends BaseEngine {

    public HttpEngine() {
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void initConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
        HttpsURLConnection.setDefaultHostnameVerifier(httpConfig.getHostnameVerifier());
        HttpsURLConnection.setDefaultSSLSocketFactory(HttpUtils.getSslSocketFactory());
    }


    @Override
    public void get(final GetRequest request, final ICallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(get(request));
                } catch (ServerException e) {
                    callback.onError(e);
                }
            }
        });
    }

    @Override
    public void post(final PostRequest request, final ICallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(post(request));
                } catch (ServerException e) {
                    callback.onError(e);
                }
            }
        });
    }

    @Override
    public String get(GetRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Connection", "Keep-Alive");
            connection.setRequestMethod("GET");
            connection.setReadTimeout(httpConfig.getReadTimeout());
            connection.setConnectTimeout(httpConfig.getConnectTimeout());

            if (request.headers() != null) {
                for (Map.Entry<String, String> entry : request.headers().entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.connect();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = HttpUtils.inputStreamToString(connection.getInputStream());
                connection.disconnect();
                setCache(request, response);
                return response;
            } else {
                throw new ServerException(responseCode,connection.getResponseMessage());
            }
        } catch (final IOException e) {
            throw new ServerException(e);
        } catch (ServerException e) {
            throw e;
        }
    }

    @Override
    public String post(PostRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.addRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", request.contentType());
            connection.setReadTimeout(httpConfig.getReadTimeout());
            connection.setConnectTimeout(httpConfig.getConnectTimeout());

            if (request.headers() != null) {
                for (Map.Entry<String, String> entry : request.headers().entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(request.getPostBody());
            writer.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = HttpUtils.inputStreamToString(connection.getInputStream());
                connection.disconnect();
                setCache(request, response);
                return response;
            } else {
                throw new ServerException(responseCode,connection.getResponseMessage() );
            }
        } catch (final IOException e) {
            throw new ServerException(e);
        } catch (ServerException e) {
            throw e;
        }
    }
}
