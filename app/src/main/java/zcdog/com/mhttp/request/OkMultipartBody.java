package zcdog.com.mhttp.request;


import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import zcdog.com.mhttp.callback.FileCallback;

/**
 * @author: zhangzhilong
 * @date: 2019/2/1
 * @des:
 */
public class OkMultipartBody extends RequestBody{
    private MultipartBody mMultipartBody;
    private FileCallback fileCallback;
    private int mCurrentLength;
    public OkMultipartBody(MultipartBody multipartBody){
        this.mMultipartBody = multipartBody;
    }

    public OkMultipartBody(MultipartBody multipartBody , FileCallback callback){
        this.mMultipartBody = multipartBody;
        this.fileCallback = callback;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mMultipartBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mMultipartBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        final long contentLength = contentLength();
        ForwardingSink forwardingSink = new ForwardingSink(sink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                mCurrentLength += byteCount;
                if(fileCallback!=null){
                    fileCallback.onProgress(contentLength,mCurrentLength);
                }
                super.write(source, byteCount);
            }
        };
        BufferedSink bufferedSink = Okio.buffer(forwardingSink);
        mMultipartBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }
}
