package zcdog.com.mhttp.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.InputStream;

import zcdog.com.mhttp.MHttpClient;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public class SpCache implements Cache {
    private static final String SP_CACHE_NAME = "sp_cache_name";

    @Override
    public void remove(String key) {
        SharedPreferences sharedPreferences = MHttpClient.getInstance().getContext()
                .getSharedPreferences(SP_CACHE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).commit();
    }

    @Override
    public String get(String key) {
        SharedPreferences sharedPreferences = MHttpClient.getInstance().getContext()
                .getSharedPreferences(SP_CACHE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }

    @Override
    public <T> T getObj(String key, Class<T> clazz) {
        return null;
    }

    @Override
    public boolean hasCache(String key) {
        return false;
    }

    @Override
    public void put(String key, String value) {
        SharedPreferences sharedPreferences = MHttpClient.getInstance().getContext()
                .getSharedPreferences(SP_CACHE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).commit();
    }

    @Override
    public void put(String key, byte[] value) {
    }

    @Override
    public void put(String key, InputStream is) {
    }
}
