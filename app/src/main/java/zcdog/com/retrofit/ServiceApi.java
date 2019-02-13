package zcdog.com.retrofit;

import java.util.HashMap;

import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import rx.Observable;

/**
 * @author: zhangzhilong
 * @date: 2019/2/13
 * @des:
 */
public interface ServiceApi {
    @GET("user/userMgr/user/getHomePageContentV3")
    Observable<JsonBean> getHomePageContentV3(@HeaderMap HashMap<String,String> hashMap);
}
