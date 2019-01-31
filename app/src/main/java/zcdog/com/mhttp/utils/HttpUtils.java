package zcdog.com.mhttp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.internal.Util;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public class HttpUtils {
    public static X509TrustManager UnSafeTrustManager = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
    public static HostnameVerifier UnSafeHostnameVerifier = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static SSLSocketFactory getSslSocketFactory(){
        try {
            SSLContext ssLContext = SSLContext.getInstance("TLS");
            ssLContext.init(null, new TrustManager[]{UnSafeTrustManager}, new SecureRandom());
            return ssLContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        }
    }


    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }
    public static String inputStreamToString(InputStream in) throws IOException {
        return readFully(new InputStreamReader(in, Util.UTF_8));
    }
}
