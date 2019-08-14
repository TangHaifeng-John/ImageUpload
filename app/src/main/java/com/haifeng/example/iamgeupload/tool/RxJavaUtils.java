package com.haifeng.example.iamgeupload.tool;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava工具
 */
public final class RxJavaUtils {

    /**
     * 处理在IO线程，订阅也在IO线程
     *
     * @return
     */
    public static <T> ObservableTransformer<T, T> io() {
        final ObservableTransformer schedulersTransformer = new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
        return (ObservableTransformer<T, T>) schedulersTransformer;
    }


}
