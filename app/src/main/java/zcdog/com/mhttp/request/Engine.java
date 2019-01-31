package zcdog.com.mhttp.request;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface Engine {
    ICallback NULL_CALLBACK = new ICallback() {
        @Override
        public void onSuccess(String result) {
        }

        @Override
        public void onError(ServerException e) {
        }
    };

    void initConfig(HttpConfig httpConfig);
    void get(GetRequest request, ICallback callback);
    void post(PostRequest request, ICallback callback);

    String get(GetRequest request) throws ServerException;
    String post(PostRequest request) throws ServerException;

    void enqueue(BaseRequest request, ICallback callBack);
    String execute(BaseRequest request) throws ServerException;
}
