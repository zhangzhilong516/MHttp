package zcdog.com.mhttp.request;

import java.util.HashMap;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class GetRequest extends BaseRequest {
    public GetRequest(Builder builder) {
        super(builder);
    }
    @Override
    public String url() {
        return toCacheKey();
    }

    @Override
    public void callBack(ICallback callback) {
        MHttpClient.getInstance().engine().enqueue(this,callback);
    }

    @Override
    public String execute() throws ServerException {
        return MHttpClient.getInstance().engine().execute(this);
    }

    public static class Builder extends BaseRequest.Builder{

        public Builder(Method method) {
            super(method);
        }

        public Builder cacheMode(CacheMode cacheMode) {
            this.cacheMode = cacheMode;
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
        protected BaseRequest build() {
            return new GetRequest(this);
        }

    }
}
