package ru.kackbip.impactMapping.api;

import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
import ru.kackbip.impactMapping.api.commands.executors.CommandExecutorsProvider;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import rx.Observable;

/**
 * Created by ryashentsev on 10.10.2016.
 */

public class Api implements IApi {

    private IProjectionRepository projectionRepository;
    private CommandExecutorsProvider commandExecutorsProvider;

    public Api(IProjectionRepository projectionRepository, CommandExecutorsProvider commandExecutorsProvider) {
        this.projectionRepository = projectionRepository;
        this.commandExecutorsProvider = commandExecutorsProvider;
    }

    public <T> Observable<T> observe(Class<T> clazz) {
        return projectionRepository.observe(clazz);
    }

    public <T> Observable<Void> execute(T command) {
        if (command == null) return Observable.error(new IllegalArgumentException("Can't execute null command"));
        @SuppressWarnings("unchecked") ICommandExecutor<T> commandExecutor = commandExecutorsProvider.getCommandExecutorForCommandClass(command.getClass());
        if (commandExecutor == null) return Observable.error(new CommandExecutorNotFound(command));
        return commandExecutor.process(command);
    }
}
