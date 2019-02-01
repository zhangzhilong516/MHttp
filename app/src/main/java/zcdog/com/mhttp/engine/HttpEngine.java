package zcdog.com.mhttp.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import okio.Utf8;
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
 * @des:
 */
public class HttpEngine extends BaseEngine {

    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DASH_DASH = {'-', '-'};
    private static final byte[] COLON_SPACE = {':', ' '};
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
    public void uploadFile(UploadRequest request, FileCallback callback) {
        try {
            callback.onSuccess(uploadFile(request));
        } catch (ServerException e) {
            callback.onError(e);
        }
    }

    @Override
    public String uploadFile(UploadRequest request) throws ServerException {
        try {
            final String BOUNDARY = UUID.randomUUID().toString();
            URL url = new URL(request.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type","multipart/form-data; BOUNDARY=" + BOUNDARY);
            connection.connect();

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            for (Map.Entry<String, Object> param : request.params().entrySet()) {
                Object object = param.getValue();
                if (object instanceof File) {
                    continue;
                }
                outputStream.write(DASH_DASH);
                outputStream.write(BOUNDARY.getBytes());
                outputStream.write(CRLF);
                outputStream.writeBytes(("Content-Disposition: form-data; name=\"" + param.getKey() + "\""));
                outputStream.write(CRLF);
                outputStream.write(CRLF);
                outputStream.writeBytes(URLEncoder.encode((String) param.getValue(), "utf-8"));
                outputStream.write(CRLF);
            }

            for (Map.Entry<String, Object> param : request.params().entrySet()) {
                Object object = param.getValue();
                if (object instanceof File) {
                    File file = (File) object;
                    outputStream.write(DASH_DASH);
                    outputStream.writeBytes(BOUNDARY);
                    outputStream.write(CRLF);
                    outputStream.writeBytes(("Content-Disposition: form-data; " + "name=\""
                            + request.getFileParamName() + "\"" + "; filename=\"" + file.getName() + "\""));
                    outputStream.write(CRLF);
                    outputStream.write(CRLF);
                    writeFile(outputStream,file);
                    outputStream.write(CRLF);
                }
            }
            outputStream.write(DASH_DASH);
            outputStream.writeBytes(BOUNDARY);
            outputStream.write(DASH_DASH);
            outputStream.write(CRLF);
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


    private void writeFile(DataOutputStream outputStream, File file) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, length);
        }
        fileInputStream.close();
        outputStream.flush();
    }
}
