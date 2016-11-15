package ru.kackbip.impactMapping.api;

import rx.Observable;

/**
 * Created by ryashentsev on 08.11.2016.
 */

public interface IApi {
    <T> Observable<T> observe(Class<T> clazz);
    <T> Observable<Void> execute(T command);
}
