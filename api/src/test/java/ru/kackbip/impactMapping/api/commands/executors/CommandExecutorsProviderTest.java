package ru.kackbip.impactMapping.api.commands.executors;

import org.junit.Before;
import org.junit.Test;

import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;
import ru.kackbip.impactMapping.api.commands.executors.local.AddGoalCommandExecutor;
import ru.kackbip.impactMapping.api.commands.executors.local.RemoveGoalCommandExecutor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by ryashentsev on 25.11.2016.
 */
public class CommandExecutorsProviderTest {

    private CommandExecutorsProvider executorsProvider;

    @Before
    public void setUp() throws Exception {
        executorsProvider = new CommandExecutorsProvider() {
            @Override
            protected ICommandExecutor<AddGoalCommand> produceAddGoalExecutor() {
                return new AddGoalCommandExecutor(null);
            }

            @Override
            protected ICommandExecutor<RemoveGoalCommand> produceRemoveGoalExecutor() {
                return new RemoveGoalCommandExecutor(null);
            }
        };
    }

    @Test
    public void getCommandExecutorForCommandClass() throws Exception {
        ICommandExecutor executor = executorsProvider.getCommandExecutorForCommandClass(AddGoalCommand.class);
        assertNotNull(executor);
        assertTrue(executor instanceof AddGoalCommandExecutor);
    }

}