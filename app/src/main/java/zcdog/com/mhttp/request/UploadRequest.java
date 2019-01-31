package zcdog.com.mhttp.request;


import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public class UploadRequest extends BaseRequest {

    public UploadRequest(Builder builder) {
        super(builder);
    }

    @Override
    public void callBack(ICallback callback) {

    }

    @Override
    public <T> T execute() throws ServerException {
        return null;
    }
}
