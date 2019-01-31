package zcdog.com.mhttp.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.CacheMode;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class GetRequest extends BaseRequest {

    public GetRequest() {
        super(Method.GET);
    }

    public void callBack(ICallback callback) {
        MHttpClient.getInstance().engine().enqueue(this, callback);
    }

    @Override
    public void execute() throws ServerException {
        MHttpClient.getInstance().engine().execute(this);
    }


    @Override
    protected String url() {
        return toCacheKey();
    }
}
