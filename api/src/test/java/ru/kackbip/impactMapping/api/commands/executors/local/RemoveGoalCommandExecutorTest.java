package ru.kackbip.impactMapping.api.commands.executors.local;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;
import ru.kackbip.impactMapping.api.commands.executors.local.RemoveGoalCommandExecutor;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionNotFoundException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryashentsev on 05.11.2016.
 */
public class RemoveGoalCommandExecutorTest {

    private static final UUID removingId = UUID.randomUUID();
    private static final Goals.Goal GOAL_1 = new Goals.Goal(removingId, "Цель номер один!", new Date(1000));
    private static final Goals.Goal GOAL_2 = new Goals.Goal(UUID.randomUUID(), "Цель номер два!", new Date(2000));

    private static final Goals STORED_PROJECTION = new Goals(Arrays.asList(GOAL_1, GOAL_2));
    private static final Goals STORED_PROJECTION2 = new Goals(new ArrayList<>());

    private RemoveGoalCommandExecutor executor;
    private IProjectionRepository projectionRepository;

    @Before
    public void init() {
        projectionRepository = mock(IProjectionRepository.class);
        when(projectionRepository.store(any(Goals.class))).thenReturn(Observable.just(null));

        executor = new RemoveGoalCommandExecutor(projectionRepository);
    }

    @Test
    public void processCommandWhenProjectionAlreadyExists() {
        when(projectionRepository.get(Goals.class)).thenReturn(Observable.just(STORED_PROJECTION));
        verifyRemoveSubscriber(removeGoal());
        verifyCommunicationWithProjectionRepository(1);
    }

    @Test
    public void processCommandWhenProjectionDoesntExists() {
        when(projectionRepository.get(Goals.class)).thenReturn(Observable.error(new ProjectionNotFoundException()));
        verifyRemoveSubscriber(removeGoal());
        verifyCommunicationWithProjectionRepository(0);
    }

    private TestSubscriber<Void> removeGoal() {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        RemoveGoalCommand command = new RemoveGoalCommand(removingId);
        executor.process(command).subscribe(subscriber);
        return subscriber;
    }

    private void verifyRemoveSubscriber(TestSubscriber<Void> subscriber) {
        subscriber.assertValue(null);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    private void verifyCommunicationWithProjectionRepository(int wantedGoalsCount) {
        verify(projectionRepository).get(Goals.class);
        ArgumentCaptor<Goals> captor = ArgumentCaptor.forClass(Goals.class);
        verify(projectionRepository).store(captor.capture());
        Goals resultProjection = captor.getValue();
        assertNotNull(resultProjection);

        List<Goals.Goal> goals = resultProjection.getGoals();
        assertTrue(goals.size() == wantedGoalsCount);

        TestSubscriber<Goals.Goal> subscriber = new TestSubscriber<>();
        Observable.from(goals)
                .filter(goal -> goal.getId().equals(removingId))
                .subscribe(subscriber);
        subscriber.assertNoValues();
    }
}