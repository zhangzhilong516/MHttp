package zcdog.com.mhttp.callback;

import java.io.File;

/**
 * @author: zhangzhilong
 * @date: 2019/1/31
 * @des:
 */
public abstract class FileCallback extends HttpCallback<File>{
    public void onProgress(long total,long curr){
    }
}
