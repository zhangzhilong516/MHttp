package zcdog.com.mhttp;

import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import zcdog.com.mhttp.cache.Cache;
import zcdog.com.mhttp.utils.HttpUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public class HttpConfig {
    final int connectTimeout;
    final int readTimeout;
    final SSLSocketFactory sslSocketFactory;
    final HostnameVerifier hostnameVerifier;
    HashMap<String,String> commonHeaders;
    Cache cache;
    final boolean isDebug;
    public HttpConfig(){
        this(new Builder());
    }
    public HttpConfig(Builder builder) {
        this.isDebug = builder.isDebug;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.sslSocketFactory = builder.sslSocketFactory;
        this.hostnameVerifier = builder.hostnameVerifier;
        if(builder.commonHeaders != null){
            commonHeaders = builder.commonHeaders;
        }
        if(builder.cache != null){
            cache = builder.cache;
        }
    }
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public HashMap<String, String> getCommonHeaders() {
        return commonHeaders;
    }

    public Cache cache() {
        return cache;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public static class Builder{
        private int connectTimeout;
        private int readTimeout;
        private SSLSocketFactory sslSocketFactory;
        private HostnameVerifier hostnameVerifier;
        private HashMap<String,String> commonHeaders;
        private Cache cache;
        private boolean isDebug = BuildConfig.DEBUG;

        public Builder(){
            readTimeout = 15000;
            connectTimeout = 15000;
            hostnameVerifier = HttpUtils.UnSafeHostnameVerifier;
            sslSocketFactory = HttpUtils.getSslSocketFactory();
        }

        public Builder connectTimeout(int connectTimeout){
            this.connectTimeout = connectTimeout;
            return this;
        }
        public Builder readTimeout(int readTimeout){
            this.readTimeout = readTimeout;
            return this;
        }
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier){
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory){
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder header(String key,String value){
            if(commonHeaders == null){
                commonHeaders = new HashMap<>();
            }
            commonHeaders.put(key,value);
            return this;
        }
        public Builder headers(HashMap<String,String> headers){
            if(commonHeaders == null){
                commonHeaders = new HashMap<>();
            }
            commonHeaders.putAll(headers);
            return this;
        }
        public Builder cache(Cache cache){
            this.cache = cache;
            return this;
        }
        public Builder isDebug(boolean isDebug){
            this.isDebug = isDebug;
            return this;
        }
        public HttpConfig build(){
            return new HttpConfig(this);
        }
    }
}
