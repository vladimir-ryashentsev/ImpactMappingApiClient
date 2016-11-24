package ru.kackbip.impactMapping.api.commands.executors;

import java.util.ArrayList;

import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import rx.Observable;

/**
 * Created by ryashentsev on 04.11.2016.
 */

public class RemoveGoalCommandExecutor implements ICommandExecutor<RemoveGoalCommand> {

    private IProjectionRepository projectionRepository;

    public RemoveGoalCommandExecutor(IProjectionRepository projectionRepository) {
        this.projectionRepository = projectionRepository;
    }

    @Override
    public Observable<Void> process(RemoveGoalCommand command) {
        return projectionRepository.get(Goals.class)
                .onErrorResumeNext(throwable -> Observable.just(new Goals(new ArrayList<>())))
                .flatMap(goals -> Observable.from(goals.getGoals()))
                .filter(goal -> !goal.getId().equals(command.getId()))
                .toList()
                .flatMap(goals -> projectionRepository.store(new Goals(goals)));
    }
}
