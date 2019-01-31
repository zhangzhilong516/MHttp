package zcdog.com.mhttp.cache.diskcache;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import zcdog.com.mhttp.MHttpClient;
import zcdog.com.mhttp.cache.Cache;


/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public class DiskCache implements Cache{
    private static final int VALUE_COUNT = 1;
    private static final int MAX_SIZE = 10 * 1024 * 1024;
    private static final int APP_VERSION = 1;
    private static final long BE_OVERDUE_TIME = 30*1000;
    private static final int BUFFER_SIZE = 8*1024;
    private DiskLruCache cache;

    public DiskCache(File directory) {
        this(directory, MAX_SIZE);
    }

    public DiskCache(File directory, long maxSize) {
        this(directory, APP_VERSION, maxSize);
    }

    public DiskCache(File directory, int appVersion, long maxSize) {
        try {
            cache = DiskLruCache.open(directory, appVersion, VALUE_COUNT, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String url) {
        try {
            String cacheKey = md5Url(url);
            DiskLruCache.Snapshot snapshot = cache.get(cacheKey);
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                if(isBeOverDue(inputStream)){
                    cache.remove(cacheKey);
                    return null;
                }
                String cacheData = DiskLruCache.inputStreamToString(inputStream);
                snapshot.close();
                return cacheData;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T getObj(String key, Class<T> clazz) {
        String cacheData = get(key);
        if(!TextUtils.isEmpty(cacheData)){
            return MHttpClient.getInstance().getJsonConvert().convert(cacheData,clazz);
        }
        return null;
    }

    @Override
    public boolean hasCache(String url) {
        String cacheKey = md5Url(url);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = cache.get(cacheKey);
            if (snapshot != null) {
                boolean hasCache = !isBeOverDue(snapshot.getInputStream(0));
                snapshot.getInputStream(0).close();
                return hasCache;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * put数据流
     */
    public void put(String url, InputStream is) {
        DiskLruCache.Editor edit = null;
        try {
            edit = cache.edit(md5Url(url));
            BufferedOutputStream outputStream = new BufferedOutputStream(edit.newOutputStream(0));
            outputStream.write(long2Bytes(System.currentTimeMillis()));
            byte[] buf = new byte[BUFFER_SIZE];
            while (is.read(buf) != -1) {
                outputStream.write(buf,0,buf.length);
            }
            is.close();
            outputStream.flush();
            outputStream.close();
            edit.commit();
        } catch (IOException e) {
            e.printStackTrace();
            abortQuietly(edit);
        }
    }

    /**
     * put字符串数据
     */
    public void put(String key, String value) {
        put(key,value.getBytes());
    }
    /**
     * put字节数据
     */
    public void put(String url, byte[] value) {
        DiskLruCache.Editor edit = null;
        try {
            edit = cache.edit(md5Url(url));
            BufferedOutputStream outputStream = new BufferedOutputStream(edit.newOutputStream(0));
            outputStream.write(long2Bytes(System.currentTimeMillis()));
            outputStream.write(value);
            outputStream.flush();
            outputStream.close();
            edit.commit();
        } catch (IOException e) {
            e.printStackTrace();
            abortQuietly(edit);
        }
    }

    /**
     * 删除缓存
     */
    public void remove(String url){
        try {
            cache.remove(md5Url(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abortQuietly(@Nullable DiskLruCache.Editor editor) {
        // Give up because the cache cannot be written.
        try {
            if (editor != null) {
                editor.abort();
            }
        } catch (IOException ignored) {
        }
    }

    public static String md5Url(String url) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(url.getBytes());
            byte[] cipher = messageDigest.digest();
            for (byte b : cipher) {
                // 转成了 16 机制
                String hexStr = Integer.toHexString(b & 0xff);
                // 不足还补 0
                sb.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static boolean isBeOverDue(InputStream inputStream) {
        byte[] time = new byte[8];
        try {
            inputStream.read(time);
            long cacheTime = bytes2Long(time);
            long overDueTime = System.currentTimeMillis() - cacheTime;
            if(overDueTime < 0 || overDueTime > BE_OVERDUE_TIME){ // overDueTime 小于0,非正常
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

}
