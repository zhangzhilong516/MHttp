package zcdog.com.mhttp.callback;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface ICallback {
    ICallback NULL_CALLBACK = new ICallback() {
        @Override
        public void onSuccess(String response) {
        }

        @Override
        public void onError(ServerException e) {
        }
    };
    void onSuccess(String response);
    void onError(ServerException e);
}
