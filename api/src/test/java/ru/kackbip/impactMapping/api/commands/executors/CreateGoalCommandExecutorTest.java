package ru.kackbip.impactMapping.api.commands.executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.kackbip.impactMapping.api.commands.CreateGoalCommand;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionNotFoundException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryashentsev on 05.11.2016.
 */
public class CreateGoalCommandExecutorTest {

    private static final Goals.Goal GOAL_1 = new Goals.Goal("Цель номер один!", new Date(1000));
    private static final Goals.Goal GOAL_2 = new Goals.Goal("Цель номер два!", new Date(2000));

    private static final String NEW_GOAL_TITLE = "Новая цель";
    private static final Date NEW_GOAL_DATE = new Date(3000);

    private static final Goals STORED_PROJECTION = new Goals(Arrays.asList(GOAL_1, GOAL_2));

    private CreateGoalCommandExecutor processor;
    private IProjectionRepository projectionRepository;

    @Before
    public void init() {
        projectionRepository = mock(IProjectionRepository.class);
        when(projectionRepository.store(STORED_PROJECTION)).thenReturn(Observable.empty());

        processor = new CreateGoalCommandExecutor(projectionRepository);
    }

    @Test
    public void processCommandWhenProjectionAlreadyExists() {
        when(projectionRepository.restore(Goals.class)).thenReturn(Observable.just(STORED_PROJECTION));
        verifySubscriber(createGoal());
        verifyCommunicationWithProjectionRepository(3);
    }

    @Test
    public void processCommandWhenProjectionDoesntExists(){
        when(projectionRepository.restore(Goals.class)).thenReturn(Observable.error(new ProjectionNotFoundException()));
        verifySubscriber(createGoal());
        verifyCommunicationWithProjectionRepository(1);
    }

    private TestSubscriber<Void> createGoal(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        CreateGoalCommand command = new CreateGoalCommand(NEW_GOAL_TITLE, NEW_GOAL_DATE);
        processor.process(command).subscribe(subscriber);
        return subscriber;
    }

    private void verifySubscriber(TestSubscriber<Void> subscriber){
        assertTrue(subscriber.getOnNextEvents().isEmpty());
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    private void verifyCommunicationWithProjectionRepository(int wantedGoalsCount){
        verify(projectionRepository).restore(Goals.class);
        ArgumentCaptor<Goals> captor = ArgumentCaptor.forClass(Goals.class);
        verify(projectionRepository).store(captor.capture());
        Goals resultProjection = captor.getValue();
        assertNotNull(resultProjection);

        List<Goals.Goal> goals = resultProjection.getGoals();
        assertTrue(goals.size()==wantedGoalsCount);

        Goals.Goal goal = goals.get(wantedGoalsCount-1);
        assertEquals(goal.getTitle(), NEW_GOAL_TITLE);
        assertEquals(goal.getDate(), NEW_GOAL_DATE);
    }
}