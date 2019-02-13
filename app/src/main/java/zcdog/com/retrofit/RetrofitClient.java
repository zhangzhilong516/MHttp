package zcdog.com.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author: zhangzhilong
 * @date: 2019/2/13
 * @des:
 */
public class RetrofitClient {
    private static final ServiceApi serviceApi;

    static {
        OkHttpClient okHttpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.zcdog.com:50183/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        serviceApi = retrofit.create(ServiceApi.class);
    }

    public static ServiceApi getServiceApi() {
        return serviceApi;
    }


    public static <T> Observable.Transformer<T, T> transformer(){
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> jsonBeanObservable) {

                return jsonBeanObservable.flatMap(new Func1<T, Observable<T>>() {
                    @Override
                    public Observable<T> call(final T jsonBean) {
                        return Observable.create(new Action1<Emitter<T>>() {
                            @Override
                            public void call(Emitter<T> jsonBeanEmitter) {
                                jsonBeanEmitter.onNext(jsonBean);
                                jsonBeanEmitter.onCompleted();
                            }
                        }, Emitter.BackpressureMode.NONE);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
        };
    }
}
