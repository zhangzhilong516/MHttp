package zcdog.com.mhttp.cache;

/**
 * @author: zhangzhilong
 * @date: 2019/1/29
 * @des:
 */
public enum CacheMode {
    DEFAULT,   // 缓存有效读缓存，无效请求网络
    FORCE_NET_WORK,  // 不读缓存，但是缓存数据
    NO_CACHE,       // 无缓存
    ONLY_CACHE       // 只读缓存
}
