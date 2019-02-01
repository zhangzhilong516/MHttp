package zcdog.com.mhttp.engine;

import android.text.TextUtils;

import java.util.concurrent.ExecutorService;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.FileCallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.request.BaseRequest;
import zcdog.com.mhttp.request.DownloadRequest;
import zcdog.com.mhttp.request.GetRequest;
import zcdog.com.mhttp.request.PostRequest;
import zcdog.com.mhttp.utils.HttpUtils;
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
        }
    }

    @Override
    public String execute(BaseRequest request) throws ServerException {
        return execute(request,NULL_CALLBACK);
    }

    /**
     * 执行http請求
     */
    public String execute(BaseRequest request,ICallback callBack) throws ServerException{

        if(!HttpUtils.isNetworkConnected()){ // 无网络，读缓存
            if(hasCache(request)){
                return getCache(request,callBack);
            }
            throw new ServerException(ServerException.NO_NET_WORK,"no network");
        }

        if(!HttpUtils.hasPermissions()){  // 无权限，直接请求网络
            return executeMethod(request,callBack);
        }

        request.addHeaders(httpConfig.getCommonHeaders());
        LogUtils.print(request.toString());
        String response = null;
        switch (request.cacheMode()) {
            case ONLY_CACHE:
                response = getCache(request, callBack);
                break;
            case NO_CACHE:
                response = executeMethod(request, callBack);
                break;
            case FORCE_NET_WORK:
            case DEFAULT:
                if (hasCache(request)) {
                    response = getCache(request, callBack);
                    break;
                }
                response = executeMethod(request, callBack);
                break;
        }
        return response;
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
            case DOWNLOAD:
                if(callBack == NULL_CALLBACK){
                }else{
                    downloadFile((DownloadRequest) request, (FileCallback) callBack);
                }
                break;
        }
        return null;
    }

    /**
     * 校验缓存是否存在
     */
    private boolean hasCache(BaseRequest request) {
        return HttpUtils.hasPermissions() && httpConfig.cache() != null && httpConfig.cache().hasCache(request.toCacheKey());
    }

    /**
     * 获取缓存
     */
    private String getCache(final BaseRequest request, final ICallback callBack) {
        if (httpConfig.cache() == null) {
            throw new NullPointerException("Cache == null");
        }
        if(callBack == NULL_CALLBACK){
            String cacheResponse = httpConfig.cache().get(request.toCacheKey());
            LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "CacheResponse==" + cacheResponse);
            return cacheResponse;
        }else{
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String cacheResponse = httpConfig.cache().get(request.toCacheKey());
                    if (!TextUtils.isEmpty(cacheResponse)) {
                        callBack.onSuccess(cacheResponse);
                        LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "CacheResponse==" + cacheResponse);
                    }
                }
            });
            return null;
        }
    }

    protected void setCache(final BaseRequest request, String response) {
        LogUtils.print("RequestUrl ==" + request.toCacheKey() + "\n" + "NetworkResponse==" + response);
        if (httpConfig.cache() != null && request.isNeedCache()) {
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
