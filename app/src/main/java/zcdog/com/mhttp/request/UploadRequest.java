package zcdog.com.mhttp.request;


import java.io.File;
import java.util.HashMap;

import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public class UploadRequest extends BaseRequest {
    final String fileParamName;
    public UploadRequest(Builder builder) {
        super(builder);
        this.fileParamName = builder.fileParamName;
    }

    @Override
    public void callBack(ICallback callback) {

    }

    @Override
    public <T> T execute() throws ServerException {
        return null;
    }

    public String getFileParamName() {
        return fileParamName;
    }
    public static class Builder extends BaseRequest.Builder {
        String fileParamName;

        public Builder(Method method) {
            super(method);
            this.cacheMode = CacheMode.NO_CACHE;
            this.fileParamName = "file";
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

        public Builder addFiles(String name,File ... files){
            for (File file : files) {
                addParam(file.getAbsolutePath(),file);
            }
            return this;
        }

        public Builder fileParamName(String name){
            fileParamName = name;
            return this;
        }

        @Override
        protected UploadRequest build() {
            return new UploadRequest(this);
        }

    }
}
