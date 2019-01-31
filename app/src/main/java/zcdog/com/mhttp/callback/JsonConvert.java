package zcdog.com.mhttp.callback;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface JsonConvert {

    JsonConvert DEFAULT_CONVERTER = new JsonConvert() {
        @Override
        public <T> T convert(String value, Class<T> type){
            return (T) value;
        }

        @Override
        public String toJsonString(Object obj) {
            if(obj instanceof Map){
                JSONObject jsonObject =  new JSONObject((Map) obj);
                return jsonObject.toString();
            }else if(obj instanceof String){
                return (String) obj;
            }
            throw new IllegalStateException("please set JsonConvert");
        }
    };

    <T> T convert(String value, Class<T> type);

    String toJsonString(Object obj);
}
