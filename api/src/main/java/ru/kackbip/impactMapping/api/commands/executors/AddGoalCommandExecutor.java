package ru.kackbip.impactMapping.api.commands.executors;

import java.util.ArrayList;

import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import rx.Observable;

/**
 * Created by ryashentsev on 04.11.2016.
 */

public class AddGoalCommandExecutor implements ICommandExecutor<AddGoalCommand> {

    private IProjectionRepository projectionRepository;

    public AddGoalCommandExecutor(IProjectionRepository projectionRepository) {
        this.projectionRepository = projectionRepository;
    }

    @Override
    public Observable<Void> process(AddGoalCommand command) {
        return projectionRepository.get(Goals.class)
                .onErrorResumeNext(throwable -> Observable.just(new Goals(new ArrayList<>())))
                .map(goals -> {
                    goals.getGoals().add(goalFromCommand(command));
                    return goals;
                })
                .flatMap(projectionRepository::store);
    }

    private Goals.Goal goalFromCommand(AddGoalCommand command) {
        return new Goals.Goal(command.getId(), command.getTitle(), command.getDate());
    }
}
