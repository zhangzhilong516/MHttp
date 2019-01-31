package zcdog.com.mhttp.request;


import java.util.HashMap;
import java.util.Map;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class PostRequest extends BaseRequest {
    Object body;
    String contentType = ContentType.Form_MediaType;

    public PostRequest() {
        super(Method.POST);
    }

    public PostRequest body(Object body){
        this.body = body;
        return this;
    }

    public PostRequest body(String body){
        this.body = body;
        return this;
    }

    @Override
    public void callBack(ICallback callback) {
        MHttpClient.getInstance().engine().enqueue(this,callback);
    }

    @Override
    public void execute() throws ServerException {
        MHttpClient.getInstance().engine().execute(this);
    }

    String getPostBody(){
        String postBody = "";
        if (ContentType.Form_MediaType.equals(contentType())) {
            StringBuffer formBuffer = new StringBuffer();
            if (body() instanceof HashMap) {
                HashMap<String, Object> params = (HashMap<String, Object>) body();
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (param.getValue() == null) {
                        continue;
                    }
                    formBuffer.append(param.getKey()).append("=").append(param.getValue()).append("&");
                }
                postBody = formBuffer.deleteCharAt(formBuffer.lastIndexOf("&")).toString();
            }
        } else if (ContentType.Json_MediaType.equals(contentType())) {
            postBody = MHttpClient.getInstance().getJsonConvert().toJsonString(body());
        } else {
            postBody = (String) body();
        }
        return postBody;
    }

    @Override
    public String toString() {
        return super.toString() +  "Content-Type:" +  contentType()
                + "\nPostBody:" + getPostBody();
    }

    String contentType() {
        return contentType;
    }

    Object body(){
        if(mParams != null){
           body = mParams;
        }
        return body;
    }
}
