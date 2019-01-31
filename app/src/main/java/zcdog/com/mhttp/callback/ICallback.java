package zcdog.com.mhttp.callback;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface ICallback {
    void onSuccess(String result);
    void onError(ServerException e);
}
