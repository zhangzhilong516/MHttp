package zcdog.com.mhttp.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import zcdog.com.mhttp.MHttpClient;

/**
 * @author: zhangzhilong
 * @date: 2018/5/10
 * @des: Log打印工具类
 */
public final class LogUtils {
    private static final String TAG = "MHttpClient";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final char MIDDLE_CORNER = '╟';
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────────────────────────────";
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════════════════════════════";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER
            + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER
            + SINGLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER
            + DOUBLE_DIVIDER + DOUBLE_DIVIDER;

    private static final int JSON_INDENT = 4;

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static boolean mIsDebug = true;

    public static void init(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public static void print(Object message) {
        if (mIsDebug)
            print(Log.DEBUG, TAG, message);
    }

    public static void d(String tag, Object message) {
        if (mIsDebug)
            print(Log.DEBUG, tag, message);
    }

    public static void e(String tag, Object message) {
        if (mIsDebug)
            print(Log.ERROR, tag, message);
    }

    public static void w(String tag, Object message) {
        if (mIsDebug)
            print(Log.WARN, tag, message);
    }

    public static void i(String tag, Object message) {
        if (mIsDebug)
            print(Log.INFO, tag, message);
    }

    public static void v(String tag, Object message) {
        if (mIsDebug)
            print(Log.VERBOSE, tag, message);
    }

    public static void wtf(String tag, Object message) {
        if (mIsDebug)
            print(Log.ASSERT, tag, message);
    }

    public static void json(String tag, Object obj) {
        printJson(Log.DEBUG, tag, MHttpClient.getInstance().getJsonConvert().toJsonString(obj));
    }

    public static void json(String tag, String jsonStr) {
        printJson(Log.DEBUG, tag, jsonStr);
    }

    public static void xml(String tag, String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(tag, xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(tag, e.getCause().getMessage() + "\n" + xml);
        }
    }

    private static void printJson(int logType, String tag, String msg) {
        printThread(logType, tag);
        printClassInfo(logType, tag);
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = msg;
            }
            String[] lines = message.split(LINE_SEPARATOR);
            for (String line : lines) {
                log(Log.DEBUG, tag, HORIZONTAL_DOUBLE_LINE + line);
            }
        } catch (JSONException e) {
            message = msg;
        }
        log(logType, tag, BOTTOM_BORDER);
    }

    private static void print(int logType, String tag, Object obj) {
//        printThread(logType, tag);
//        printClassInfo(logType, tag);
        printContent(logType, tag, obj);
    }

    /**
     * 打印Thread信息
     *
     * @param logType
     * @param tag
     */
    private static void printThread(int logType, String tag) {
        log(logType, tag, TOP_BORDER);
        log(logType, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
        log(logType, tag, MIDDLE_BORDER);
    }

    /**
     * 打印堆栈信息
     *
     * @param logType
     * @param tag
     */
    private static void printClassInfo(int logType, String tag) {
        log(logType, tag, generateFileTag());
        log(logType, tag, MIDDLE_BORDER);
    }

    /**
     * 打印内容
     *
     * @param logType
     * @param tag
     */
    private static void printContent(int logType, String tag, Object obj) {
        log(logType, tag, TOP_BORDER);
        String[] lines = obj.toString().split(LINE_SEPARATOR);
        for (String line : lines) {
            log(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
        log(logType, tag, BOTTOM_BORDER);
    }

    /**
     * 获取调用LogUtils的堆栈游标
     *
     * @return
     */
    private static String generateFileTag() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraces[getStackOffset(stackTraces)];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        StringBuilder builder = new StringBuilder();
        builder.append("║ ").append(callerClazzName).append(".")
                .append(caller.getMethodName()).append(" ").append(" (")
                .append(caller.getFileName()).append(":")
                .append(caller.getLineNumber()).append(")");
        return builder.toString();
    }


    /**
     * @param trace the stack trace
     * @return the stack offset
     */
    private static int getStackOffset(StackTraceElement[] trace) {
        for (int i = 0; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (name.equals(LogUtils.class.getName())) {
                return i + 4;
            }
        }
        return -1;
    }


    /**
     * 输出Log到控制台
     *
     * @param logType
     * @param tag
     * @param msg
     */
    private static void log(int logType, String tag, String msg) {
        switch (logType) {
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.ASSERT:
                Log.wtf(tag, msg);
                break;
            default:
                Log.d(tag, msg);
                break;
        }
    }
}
