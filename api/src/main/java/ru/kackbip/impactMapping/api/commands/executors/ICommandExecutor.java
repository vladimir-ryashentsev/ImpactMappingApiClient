package ru.kackbip.impactMapping.api.commands.executors;

import rx.Observable;

/**
 * Created by ryashentsev on 10.10.2016.
 */

public interface ICommandExecutor<T> {
    Observable<Void> process(T command);
}
