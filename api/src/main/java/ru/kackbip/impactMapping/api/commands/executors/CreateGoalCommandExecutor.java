package ru.kackbip.impactMapping.api.commands.executors;

import java.util.ArrayList;

import ru.kackbip.impactMapping.api.commands.CreateGoalCommand;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import rx.Observable;

/**
 * Created by ryashentsev on 04.11.2016.
 */

public class CreateGoalCommandExecutor implements ICommandExecutor<CreateGoalCommand> {

    private IProjectionRepository projectionRepository;

    public CreateGoalCommandExecutor(IProjectionRepository projectionRepository) {
        this.projectionRepository = projectionRepository;
    }

    @Override
    public Observable<Void> process(CreateGoalCommand command) {
        return projectionRepository.restore(Goals.class)
                .onErrorResumeNext(throwable -> Observable.just(new Goals(new ArrayList<>())))
                .map(goals -> {
                    goals.getGoals().add(goalFromCommand(command));
                    return goals;
                })
                .flatMap(projectionRepository::store);
    }

    private Goals.Goal goalFromCommand(CreateGoalCommand command) {
        return new Goals.Goal(command.getTitle(), command.getDate());
    }
}
