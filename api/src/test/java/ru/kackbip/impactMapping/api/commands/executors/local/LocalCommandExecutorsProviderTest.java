package ru.kackbip.impactMapping.api.commands.executors.local;

import org.junit.Before;
import org.junit.Test;

import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by ryashentsev on 25.11.2016.
 */
public class LocalCommandExecutorsProviderTest {

    private LocalCommandExecutorsProvider executorsProvider;

    @Before
    public void setUp() throws Exception {
        IProjectionRepository projectionRepository = mock(IProjectionRepository.class);
        executorsProvider = new LocalCommandExecutorsProvider(projectionRepository);
    }

    @Test
    public void produceAddGoalExecutor() throws Exception {
        ICommandExecutor executor = executorsProvider.produceAddGoalExecutor();
        assertNotNull(executor);
        assertTrue(executor instanceof AddGoalCommandExecutor);
    }

    @Test
    public void produceRemoveGoalExecutor() throws Exception {
        ICommandExecutor executor = executorsProvider.produceRemoveGoalExecutor();
        assertNotNull(executor);
        assertTrue(executor instanceof RemoveGoalCommandExecutor);
    }

}