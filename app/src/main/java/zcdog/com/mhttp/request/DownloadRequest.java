package zcdog.com.mhttp.request;

import android.text.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public class DownloadRequest extends BaseRequest {
    private static final String DES_PATH = "des_path";
    private static final String FILE_NAME = "file_name";

    public DownloadRequest(Builder builder) {
        super(builder);
    }

    @Override
    public File execute() throws ServerException {
        return MHttpClient.getInstance().engine().downloadFile(this);
    }

    public InputStream executeIo() throws ServerException{
        return MHttpClient.getInstance().engine().download(this);
    }
    public String getDesPath() {
        if (mParams == null) {
            throw new IllegalStateException("there is no desPath");
        }
        String desPath = (String) mParams.get(DES_PATH);
        if (TextUtils.isEmpty(desPath)) {
            throw new IllegalStateException("there is no desPath");
        }
        return desPath;
    }

    public String getFileName() {
        if (mParams != null) {
            String fileName = (String) mParams.get(FILE_NAME);
            if (!TextUtils.isEmpty(fileName)) {
                return fileName;
            }
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public void callBack(ICallback callback) {
        MHttpClient.getInstance().engine().enqueue(this, callback);
    }


    public static class Builder extends BaseRequest.Builder {

        public Builder(Method method) {
            super(method);
            this.cacheMode = CacheMode.NO_CACHE;
        }

        public Builder desPath(String desPath) {
            addParam(DES_PATH, desPath);
            return this;
        }

        public Builder fileName(String fileName) {
            addParam(FILE_NAME, fileName);
            return this;
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

        @Override
        protected DownloadRequest build() {
            return new DownloadRequest(this);
        }

        public InputStream executeIo() throws ServerException{
            return build().executeIo();
        }
    }
}
