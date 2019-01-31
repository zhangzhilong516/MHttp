package zcdog.com.mhttp.request;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/30
 * @des:
 */
public abstract class BaseEngine implements Engine {
    ExecutorService executorService;
    HttpConfig httpConfig;

    @Override
    public void enqueue(final BaseRequest request, ICallback callBack) {
        try {
            execute(request,callBack);
        } catch (ServerException e) {
            callBack.onError(e);
            e.printStackTrace();
        }
    }

    @Override
    public String execute(BaseRequest request) throws ServerException {
        return execute(request,NULL_CALLBACK);
    }

    /**
     * 執行http請求
     */
    public String execute(BaseRequest request,ICallback callBack) throws ServerException{
        request.addHeaders(httpConfig.getCommonHeaders());
        LogUtils.print(request.toString());
        switch (request.cacheMode()) {
            case ONLY_CACHE:
                getCache(request, callBack);
            case FORCE_NET_WORK:
            case NO_CACHE:
            case DEFAULT:
                if (hasCache(request)) {
                    getCache(request, callBack);
                    break;
                }
                executeMethod(request, callBack);
                break;
        }
        return null;
    }

    private String executeMethod(final BaseRequest request,ICallback callBack) throws ServerException{
        switch (request.method()) {
            case GET:
                if(callBack == NULL_CALLBACK){
                    return get((GetRequest) request);
                }else{
                     get((GetRequest) request,callBack);
                }
                break;
            case POST:
                if(callBack == NULL_CALLBACK){
                    return post((PostRequest) request);
                }else{
                    post((PostRequest) request,callBack);
                }
                break;
        }
        return null;
    }

    /**
     * 校验缓存是否存在
     */
    private boolean hasCache(BaseRequest request) {
        return httpConfig.cache() != null && httpConfig.cache().hasCache(request.toCacheKey());
    }

    /**
     * 获取缓存
     */
    private String getCache(final BaseRequest request, final ICallback callBack) {
        if (httpConfig.cache() == null) {
            throw new NullPointerException("Cache == null");
        }
        if(callBack == NULL_CALLBACK){
            return httpConfig.cache().get(request.toCacheKey());
        }else{
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String cacheResult = httpConfig.cache().get(request.toCacheKey());
                    if (!TextUtils.isEmpty(cacheResult)) {
                        callBack.onSuccess(cacheResult);
                        LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "CacheResponse==" + cacheResult);
                    }
                }
            });
            return null;
        }
    }

    protected void setCache(final BaseRequest request, String response) {
        LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "NetworkResponse==" + response);
        if (httpConfig.cache() != null && request.isCache()) {
            httpConfig.cache().put(request.toCacheKey(), response);
        }
    }

    protected void onError(final ICallback callback, final ServerException e) {
        MHttpClient.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onError(e);
            }
        });
    }
}
