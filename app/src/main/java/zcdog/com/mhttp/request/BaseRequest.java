package zcdog.com.mhttp.request;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public abstract class BaseRequest implements Request{
    final String url;
    final Method method;
    CacheMode cacheMode;
    HashMap<String, String> mHeaders;
    HashMap<String, Object> mParams;

    public BaseRequest(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        if(builder.cacheMode == null){
            this.cacheMode = CacheMode.DEFAULT;
        }else{
            this.cacheMode = builder.cacheMode;
        }
        if(builder.mParams != null){
            this.mParams = builder.mParams;
        }
        if(builder.mHeaders != null){
            this.mHeaders = builder.mHeaders;
        }
    }
    public Method method() {
        return method;
    }
    public HashMap<String, String> headers() {
        return mHeaders;
    }

    public HashMap<String, Object> params() {
        return mParams;
    }

    public BaseRequest addParams(HashMap<String, Object> params) {
        if (params != null) {
            if (mParams == null) {
                mParams = new HashMap<>();
            }
            mParams.putAll(params);
        }
        return this;
    }

    public BaseRequest addHeaders(HashMap<String, String> headers) {
        if (headers != null) {
            if (mHeaders == null) {
                mHeaders = new HashMap<>();
            }
            mHeaders.putAll(headers);
        }
        return this;
    }

    public String url() {
        return url;
    }

    public String toCacheKey() {
        String cacheKey = url;
        try {
            if(params() != null){
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
                else sb.append("?");
                for (Map.Entry<String, Object> urlParams : mParams.entrySet()) {
                    Object value = urlParams.getValue();
                    String urlValue = URLEncoder.encode((String) value, "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cacheKey;
    }
    public CacheMode cacheMode() {
        return cacheMode;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Method:").append(method().toString()).append("\n");
        stringBuilder.append("CacheMode:").append(cacheMode().toString()).append("\n");
        stringBuilder.append("Url:").append(url).append("\n");
        stringBuilder.append("FullUrl:").append(toCacheKey()).append("\n");
        if(headers() != null){
            stringBuilder.append("Headers:").append(MHttpClient.getInstance().getJsonConvert().toJsonString(headers())).append("\n");
        }
        if(params() != null){
            stringBuilder.append("Params:").append(MHttpClient.getInstance().getJsonConvert().toJsonString(params())).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 是否需要缓存
     */
    public boolean isNeedCache() {
        if(method == Method.GET || (method == Method.POST && mParams != null)){
            return true;
        }
        boolean isCache = false;
        switch (cacheMode){
            case NO_CACHE:
                isCache = false;
                break;
            case DEFAULT:
            case ONLY_CACHE:
            case FORCE_NET_WORK:
                isCache = true;
                break;
        }
        return isCache;
    }

    public abstract static class Builder implements Request.Builder{
        CacheMode cacheMode;
        HashMap<String, String> mHeaders;
        HashMap<String, Object> mParams;
        String url;
        Method method;
        public Builder(Method method){
            this.method = method;
        }

        abstract <T extends BaseRequest> T build();

        @Override
        public <T> T execute() throws ServerException{
            return build().execute();
        }
        @Override
        public void callBack(ICallback callback){
            build().callBack(callback);
        }

    }
}
