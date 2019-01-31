package zcdog.com.mhttp.request;


import java.util.HashMap;
import java.util.Map;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class PostRequest extends BaseRequest {
    Object body;
    final String contentType;

    public PostRequest(Builder builder) {
        super(builder);
        this.contentType = builder.contentType;
        if(builder.body != null){
            this.body = builder.body;
        }
    }

    public String getPostBody(){
        String postBody = "";
        if (ContentType.Form_MediaType.equals(contentType())) {
            StringBuffer formBuffer = new StringBuffer();
            if (body() instanceof HashMap) {
                HashMap<String, Object> params = (HashMap<String, Object>) body();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (param.getValue() == null) {
                        continue;
                    }
                    formBuffer.append(param.getKey()).append("=").append(param.getValue()).append("&");
                }
                postBody = formBuffer.deleteCharAt(formBuffer.lastIndexOf("&")).toString();
            }
        } else if (ContentType.Json_MediaType.equals(contentType())) {
            postBody = MHttpClient.getInstance().getJsonConvert().toJsonString(body());
        } else {
            postBody = (String) body();
        }
        return postBody;
    }

    @Override
    public String toString() {
        return super.toString() +  "Content-Type:" +  contentType()
                + "\nPostBody:" + getPostBody();
    }

    public String contentType() {
        return contentType;
    }

    public Object body(){
        if(mParams != null){
           return mParams;
        }
        return body;
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
        Object body;
        String contentType;

        public Builder(Method method) {
            super(method);
            contentType = ContentType.Form_MediaType;
        }

        public Builder body(Object body){
            this.body = body;
            return this;
        }

        public Builder body(String body){
            this.body = body;
            return this;
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
            return new PostRequest(this);
        }
    }
}
