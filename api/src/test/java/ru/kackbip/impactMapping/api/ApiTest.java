package ru.kackbip.impactMapping.api;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
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
 * Created by ryashentsev on 08.11.2016.
 */
public class ApiTest {

    private class SomeCommand{}
    private class SomeUnknownCommand{}

    private static final TestProjection STORED_INNER = new TestProjection("qweqwe", 321321, null);
    private static final TestProjection STORED_PROJECTION = new TestProjection("strstr", 123123, STORED_INNER);

    private IApi api;
    private IProjectionRepository projectionRepository;
    private ICommandExecutor<SomeCommand> commandExecutor;

    @Before
    public void init(){
        projectionRepository = mock(IProjectionRepository.class);

        commandExecutor = mock(ICommandExecutor.class);
        Map<Class, ICommandExecutor> commandExecutors = new HashMap<>();
        commandExecutors.put(SomeCommand.class, commandExecutor);

        api = new Api(projectionRepository, commandExecutors);
    }

    @Test
    public void executeCommandForUnknownExecutor(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        SomeUnknownCommand command = new SomeUnknownCommand();
        api.execute(command).subscribe(subscriber);

        subscriber.assertError(CommandExecutorNotFound.class);
        subscriber.assertNotCompleted();
        assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @Test
    public void executeNullCommand(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        api.execute(null).subscribe(subscriber);

        subscriber.assertError(IllegalArgumentException.class);
        subscriber.assertNotCompleted();
        assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @Test
    public void executeCommand(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        SomeCommand command = new SomeCommand();
        when(commandExecutor.process(command)).thenReturn(Observable.empty());
        api.execute(command).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        assertTrue(subscriber.getOnNextEvents().isEmpty());

        verify(commandExecutor).process(command);
    }

    @Test
    public void getNonExistentProjection(){
        when(projectionRepository.restore(TestProjection.class)).thenReturn(Observable.error(new ProjectionNotFoundException()));

        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        api.get(TestProjection.class).subscribe(subscriber);

        subscriber.assertError(ProjectionNotFoundException.class);
        assertTrue(subscriber.getOnNextEvents().isEmpty());

        verify(projectionRepository).restore(TestProjection.class);
    }

    @Test
    public void getProjection(){
        when(projectionRepository.restore(TestProjection.class)).thenReturn(Observable.just(STORED_PROJECTION));

        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        api.get(TestProjection.class).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        verify(projectionRepository).restore(TestProjection.class);

        assertTrue(subscriber.getOnNextEvents().size()==1);
        TestProjection projection = subscriber.getOnNextEvents().get(0);
        assertEquals(STORED_PROJECTION.getNum(), projection.getNum());
        assertEquals(STORED_PROJECTION.getStr(), projection.getStr());

        TestProjection inner = projection.getInner();
        assertNotNull(inner);
        assertEquals(STORED_INNER.getNum(), inner.getNum());
        assertEquals(STORED_INNER.getStr(), inner.getStr());
        assertEquals(STORED_INNER.getInner(), inner.getInner());
    }

}