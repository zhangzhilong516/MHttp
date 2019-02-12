package zcdog.com.mhttp.engine;

import java.io.File;
import java.io.InputStream;

import zcdog.com.mhttp.HttpConfig;
import zcdog.com.mhttp.callback.FileCallback;
import zcdog.com.mhttp.callback.ICallback;
import zcdog.com.mhttp.callback.ServerException;
import zcdog.com.mhttp.request.BaseRequest;
import zcdog.com.mhttp.request.DownloadRequest;
import zcdog.com.mhttp.request.GetRequest;
import zcdog.com.mhttp.request.PostRequest;
import zcdog.com.mhttp.request.UploadRequest;

/**
 * @author: zhangzhilong
 * @date: 2019/1/28
 * @des:
 */
public interface Engine {
    void initConfig(HttpConfig httpConfig);

    void get(GetRequest request, ICallback callback);
    void post(PostRequest request, ICallback callback);
    void downloadFile(DownloadRequest request, FileCallback callback);
    void uploadFile(UploadRequest request, ICallback callback);

    String get(GetRequest request) throws ServerException;
    String post(PostRequest request) throws ServerException;
    File downloadFile(DownloadRequest request) throws ServerException;
    InputStream download(DownloadRequest request) throws ServerException;

    String uploadFile(UploadRequest request) throws ServerException;

    void enqueue(BaseRequest request, ICallback callBack);
    String execute(BaseRequest request) throws ServerException;
}
