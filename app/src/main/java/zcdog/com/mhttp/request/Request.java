package zcdog.com.mhttp.request;

import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public interface Request {
    void callBack(ICallback callback);

    <T> T execute() throws ServerException;

    interface Builder{

        void callBack(ICallback callback);

        <T> T execute() throws ServerException;
    }
}
