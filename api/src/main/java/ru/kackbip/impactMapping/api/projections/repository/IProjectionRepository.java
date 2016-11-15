package ru.kackbip.impactMapping.api.projections.repository;

import rx.Observable;

/**
 * Created by ryashentsev on 10.10.2016.
 */

public interface IProjectionRepository {
    Observable<Void> store(Object projection);
    <T> Observable<T> observe(Class<T> clazz);
    <T> Observable<T> get(Class<T> clazz);
}
