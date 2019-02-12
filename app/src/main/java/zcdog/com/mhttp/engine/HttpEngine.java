package zcdog.com.mhttp.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.callback.FileCallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.request.DownloadRequest;
import zcdog.com.mhttp.request.GetRequest;
import zcdog.com.mhttp.request.PostRequest;
import zcdog.com.mhttp.request.UploadRequest;
import zcdog.com.mhttp.utils.HttpUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des: HttpUrlConnection
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

    /**
     * *********************************GET********************************
     */

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
    public String get(GetRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
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
                throw new ServerException(responseCode, connection.getResponseMessage());
            }
        } catch (final IOException e) {
            throw new ServerException(e);
        } catch (ServerException e) {
            throw e;
        }
    }

    /**
     * *********************************POST********************************
     */
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
    public String post(PostRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
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
                throw new ServerException(responseCode, connection.getResponseMessage());
            }
        } catch (final IOException e) {
            throw new ServerException(e);
        } catch (ServerException e) {
            throw e;
        }
    }

    /**
     * *********************************DOWNLOAD********************************
     */
    @Override
    public File downloadFile(DownloadRequest request) throws ServerException {
        return download(request, null);
    }

    @Override
    public InputStream download(DownloadRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setReadTimeout(httpConfig.getReadTimeout());
            connection.setConnectTimeout(httpConfig.getConnectTimeout());
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.getInputStream();
            } else {
                throw new ServerException(connection.getResponseCode(), connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Override
    public void downloadFile(final DownloadRequest request, final FileCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(download(request, callback));
                } catch (ServerException e) {
                    callback.onError(e);
                }
            }
        });
    }

    private File download(final DownloadRequest request, final FileCallback fileCallback) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setReadTimeout(httpConfig.getReadTimeout());
            connection.setConnectTimeout(httpConfig.getConnectTimeout());
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                File dir = new File(request.getDesPath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, request.getFileName());//根据目录和文件名得到file对象
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[1024 * 8];
                int length;
                int totalLength = connection.getContentLength();
                int currLength = 0;
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
                connection.disconnect();
                return file;
            } else {
                throw new ServerException(responseCode, connection.getResponseMessage());
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }


    /**
     * *********************************UPLOAD********************************
     */
    @Override
    public void uploadFile(final UploadRequest request, final ICallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onSuccess(uploadFile(request));
                } catch (ServerException e) {
                    callback.onError(e);
                }
            }
        });
    }

    @Override
    public String uploadFile(UploadRequest request) throws ServerException {
        try {
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(httpConfig.getReadTimeout());
            connection.setConnectTimeout(httpConfig.getConnectTimeout());
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + request.BOUNDARY);

            if (request.headers() != null) {
                for (Map.Entry<String, String> entry : request.headers().entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            request.writeParams(outputStream);
            request.writeBinary(outputStream);

            outputStream.write(request.END_BOUNDARY.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = HttpUtils.inputStreamToString(connection.getInputStream());
                connection.disconnect();
                return response;
            }else{
                throw new ServerException(responseCode, connection.getResponseMessage());
            }
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

}
