package zcdog.com.mhttp.request;

import android.util.Base64;

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
public abstract class BaseRequest {
    protected HashMap<String, String> mHeaders;
    protected HashMap<String, Object> mParams;
    protected String url;
    protected final Method method;

    protected CacheMode cacheMode = CacheMode.DEFAULT;

    public BaseRequest(Method method) {
        this.method = method;
    }

    protected Method method() {
        return method;
    }
    public BaseRequest url(String url) {
        this.url = url;
        return this;
    }

    public BaseRequest cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    public BaseRequest addParam(String key, Object value) {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);
        return this;
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

    public BaseRequest addHeader(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }
        mHeaders.put(key, value);
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

    protected HashMap<String, String> headers() {
        if (mHeaders != null) {
            return mHeaders;
        }
        return null;
    }

    protected HashMap<String, Object> params() {
        if (mParams != null) {
            return mParams;
        }
        return null;
    }

    protected String url() {
        return url;
    }

    protected String toCacheKey() {
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
    protected CacheMode cacheMode() {
        return cacheMode;
    }

    /**
     * 是否需要缓存
     */
    protected boolean isCache() {
        if(!checkCacheMethod(method)){
            return false;
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

    private boolean checkCacheMethod(Method method){
        if(method == Method.GET || (method == Method.POST && mParams != null)){
            return true;
        }
        return false;
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

    public abstract void callBack(ICallback callback);
    public abstract void execute() throws ServerException;
}
