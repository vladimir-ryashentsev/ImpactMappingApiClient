package ru.kackbip.impactMapping.api.commands.executors.local;

import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;
import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
import ru.kackbip.impactMapping.api.commands.executors.CommandExecutorsProvider;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;

/**
 * Created by ryashentsev on 25.11.2016.
 */

public class LocalCommandExecutorsProvider extends CommandExecutorsProvider {

    private IProjectionRepository projectionRepository;

    public LocalCommandExecutorsProvider(IProjectionRepository projectionRepository){
        this.projectionRepository = projectionRepository;
    }

    @Override
    public ICommandExecutor<AddGoalCommand> produceAddGoalExecutor() {
        return new AddGoalCommandExecutor(projectionRepository);
    }

    @Override
    public ICommandExecutor<RemoveGoalCommand> produceRemoveGoalExecutor() {
        return new RemoveGoalCommandExecutor(projectionRepository);
    }
}
