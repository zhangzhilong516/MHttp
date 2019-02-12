package zcdog.com.mhttp.request;


import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public class UploadRequest extends BaseRequest {
    public static final String LINE_LINE = "--";
    private static final String CRLF = "\r\n";
    public String BOUNDARY;
    public String START_BOUNDARY;
    public String END_BOUNDARY;

    public UploadRequest(Builder builder) {
        super(builder);
        BOUNDARY = "HTTP" + UUID.randomUUID().toString();
        START_BOUNDARY = LINE_LINE + BOUNDARY;
        END_BOUNDARY = START_BOUNDARY + LINE_LINE;
    }


    @Override
    public void callBack(ICallback callback) {
        MHttpClient.getInstance().engine().enqueue(this, callback);
    }

    @Override
    public String execute() throws ServerException {
        return MHttpClient.getInstance().engine().execute(this);
    }

    public void writeParams(OutputStream outputStream) throws IOException {
        for (Map.Entry<String, Object> param : params().entrySet()) {
            Object value = param.getValue();
            if (value instanceof List || value instanceof File) {
                continue;
            }

            if (value == null) {
                value = "";
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(START_BOUNDARY);
            stringBuilder.append(CRLF);
            stringBuilder.append("Content-Disposition: form-data; name = \"" + param.getKey() + "\"");
            stringBuilder.append(CRLF);
            stringBuilder.append(CRLF);
            stringBuilder.append((String) value);
            stringBuilder.append(CRLF);
            outputStream.write(stringBuilder.toString().getBytes());
        }
    }

    public void writeBinary(OutputStream outputStream) throws IOException {

        for (Map.Entry<String, Object> param : params().entrySet()) {
            Object value = param.getValue();
            if (value instanceof File) {
                File file = (File) value;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(START_BOUNDARY);
                stringBuilder.append(CRLF);
                stringBuilder.append("Content-Disposition: form-data; name = \"" + param.getKey() + "\""
                        + "; filename = \"" + file.getName() + "\"");
                stringBuilder.append(CRLF);
                stringBuilder.append("Content-Type:" + guessMimeType(file.getAbsolutePath()));
                stringBuilder.append(CRLF);
                stringBuilder.append(CRLF);
                outputStream.write(stringBuilder.toString().getBytes());
                writeFile(outputStream, file);
            } else if (value instanceof List) {
                List list = (List) value;
                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);
                    if (obj instanceof File) {
                        File file = (File) value;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(START_BOUNDARY);
                        stringBuilder.append(CRLF);
                        stringBuilder.append("Content-Disposition: form-data; name = \"" + param.getKey() + "\""
                                + "; filename = \"" + file.getName() + "\"");
                        stringBuilder.append(CRLF);
                        stringBuilder.append("Content-Type:" + guessMimeType(file.getAbsolutePath()));
                        stringBuilder.append(CRLF);
                        stringBuilder.append(CRLF);
                        outputStream.write(stringBuilder.toString().getBytes());
                        writeFile(outputStream, file);
                    }
                }
            }
        }
    }

    private void writeFile(OutputStream outputStream, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, length);
        }
        outputStream.write(CRLF.getBytes());
        fileInputStream.close();
        outputStream.flush();
    }


    protected String guessMimeType(String filePath) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();

        String mimType = fileNameMap.getContentTypeFor(filePath);

        if (TextUtils.isEmpty(mimType)) {
            return ContentType.Stream_MediaType;
        }
        return mimType;
    }

    public static class Builder extends BaseRequest.Builder {
        String fileParamName;
        List<File> fileArray;

        public Builder(Method method) {
            super(method);
            this.cacheMode = CacheMode.NO_CACHE;
            this.fileParamName = "file";
            this.fileArray = new ArrayList<>();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addParam(String key, Object value) {
            if (mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.put(key, value);
            return this;
        }

        public Builder addParams(HashMap<String, Object> params) {
            if (params != null) {
                if (mParams == null) {
                    mParams = new HashMap<>();
                }
                mParams.putAll(params);
            }
            return this;
        }


        public Builder addHeader(String key, String value) {
            if (mHeaders == null) {
                mHeaders = new HashMap<>();
            }
            mHeaders.put(key, value);
            return this;
        }

        public Builder addHeaders(HashMap<String, String> headers) {
            if (headers != null) {
                if (mHeaders == null) {
                    mHeaders = new HashMap<>();
                }
                mHeaders.putAll(headers);
            }
            return this;
        }

        public Builder addFiles(String name, File... files) {
            addParam(name, Arrays.asList(files));
            return this;
        }

        public Builder addFile(String name, File file) {
            addParam(name, file);
            return this;
        }

        public Builder addFiles(String name, List<File> files) {
            addParam(name, files);
            return this;
        }

        @Override
        protected UploadRequest build() {
            return new UploadRequest(this);
        }

    }
}
