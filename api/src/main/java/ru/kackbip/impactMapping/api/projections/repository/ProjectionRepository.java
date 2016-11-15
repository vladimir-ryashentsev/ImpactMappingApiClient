package ru.kackbip.impactMapping.api.projections.repository;

import ru.kackbip.infrastructure.storage.pojo.IPojoStorage;
import rx.AsyncEmitter;
import rx.Observable;

/**
 * Created by ryashentsev on 04.11.2016.
 */

public class ProjectionRepository implements IProjectionRepository {
    private IPojoStorage storage;

    public ProjectionRepository(IPojoStorage storage){
        this.storage = storage;
    }

    @Override
    public Observable<Void> store(Object projection) {
        return canonicalNameOfObjectClass(projection)
                .flatMap(key -> storage.store(key, projection));
    }

    public <T> Observable<T> get(Class<T> clazz){
        return canonicalNameOfClass(clazz)
                .flatMap(key -> storage.get(key, clazz))
                .onErrorResumeNext(throwable -> Observable.error(new ProjectionNotFoundException()));
    }

    public <T> Observable<T> observe(Class<T> clazz){
        return canonicalNameOfClass(clazz)
                .flatMap(key -> storage.observe(key, clazz))
                .onErrorResumeNext(throwable -> Observable.error(new ProjectionNotFoundException()));
    }

    private Observable<String> canonicalNameOfClass(Class clazz){
        return Observable.fromEmitter(
                emitter -> {
                    emitter.onNext(clazz.getCanonicalName());
                    emitter.onCompleted();
                },
                AsyncEmitter.BackpressureMode.ERROR
        );
    }

    private Observable<String> canonicalNameOfObjectClass(Object object){
        return Observable.fromEmitter(
                emitter -> {
                    emitter.onNext(object.getClass().getCanonicalName());
                    emitter.onCompleted();
                },
                AsyncEmitter.BackpressureMode.ERROR
        );
    }
}