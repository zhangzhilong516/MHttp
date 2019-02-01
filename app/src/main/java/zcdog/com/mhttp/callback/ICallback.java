package zcdog.com.mhttp.callback;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface ICallback {
    ICallback NULL_CALLBACK = new ICallback() {
        @Override
        public void onSuccess(String result) {
        }

        @Override
        public void onError(ServerException e) {
        }
    };
    void onSuccess(String result);
    void onError(ServerException e);
}
