package ru.kackbip.impactMapping.api.commands.executors;

import java.util.HashMap;
import java.util.Map;

import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;

/**
 * Created by ryashentsev on 25.11.2016.
 */

public abstract class CommandExecutorsProvider {
    private Map<Class, ICommandExecutor> commandExecutors;

    protected CommandExecutorsProvider(){
    }

    public <T extends ICommandExecutor> ICommandExecutor getCommandExecutorForCommandClass(Class clazz){
        if(commandExecutors==null) {
            commandExecutors = new HashMap<>();
            commandExecutors.put(AddGoalCommand.class, produceAddGoalExecutor());
            commandExecutors.put(RemoveGoalCommand.class, produceRemoveGoalExecutor());
        }
        return commandExecutors.get(clazz);
    }

    protected abstract ICommandExecutor<AddGoalCommand> produceAddGoalExecutor();
    protected abstract ICommandExecutor<RemoveGoalCommand> produceRemoveGoalExecutor();
}
