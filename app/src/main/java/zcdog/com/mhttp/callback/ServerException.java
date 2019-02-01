package zcdog.com.mhttp.callback;

import android.util.Log;

import zcdog.com.mhttp.utils.LogUtils;

/**
 * @author: zhangzhilong
 * @date: 2019/1/30
 * @des:
 */
public class ServerException extends Exception {
    private static final String TAG = "ServerException";
    public static final int NO_NET_WORK = -2;
    private int errorCode = -1;
    private String errorMsg = " ";

    public ServerException(Exception e){
        super(e);
        this.errorMsg = e.getMessage();
    }
    public ServerException(int errorCode){
        super();
        this.errorCode = errorCode;
    }
    public ServerException(int errorCode,String errorMsg){
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ServerException(String message,Throwable cause){
        this(-1,message,cause);
        this.errorMsg = cause.getMessage();
    }

    public ServerException(int errorCode,Throwable cause){
        this(errorCode,cause.getMessage(),cause);
    }

    public ServerException(int errorCode,String errorMsg,Throwable cause){
        super(errorMsg,cause);
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    @Override
    public void printStackTrace() {
        LogUtils.print("errorCode == " + errorCode + " ,   errorMsg == " + errorMsg);
        super.printStackTrace();
    }
}
