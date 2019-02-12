package zcdog.com.mhttp.callback;

import java.lang.reflect.ParameterizedType;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.engine.BaseEngine;
import zcdog.com.mhttp.engine.Engine;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public abstract class HttpCallback<T> implements ICallback {

    public void onSuccess(String result){
        try {

            NULL_CALLBACK.onSuccess(result);

            final T convert = MHttpClient.getInstance().getJsonConvert().convert(result, getType(this));
            MHttpClient.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onSuccess(convert);
                }
            });

        }catch (final Exception e){
            MHttpClient.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onError(new ServerException("数据解析异常",e));
                }
            });
        }
    }

    public final Class<T> getType(Object object) {
        ParameterizedType parameterizedType = (ParameterizedType) object.getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
    public abstract void onSuccess(T t);
    public void onProgress(long total,long curr){
    }
}
