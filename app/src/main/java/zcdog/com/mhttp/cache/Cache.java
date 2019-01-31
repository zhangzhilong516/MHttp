package zcdog.com.mhttp.cache;

import java.io.InputStream;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public interface Cache {
    void remove(String key);
    String get(String key);
    <T> T getObj(String key,Class<T> clazz);
    boolean hasCache(String key);
    void put(String key,String value);
    void put(String key,byte[] value);
    void put(String key,InputStream is);
}
