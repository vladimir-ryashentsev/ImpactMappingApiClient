package ru.kackbip.impactMapping.api;

import org.junit.Before;
import org.junit.Test;

import ru.kackbip.impactMapping.api.commands.executors.CommandExecutorsProvider;
import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
import ru.kackbip.impactMapping.api.projections.repository.IProjectionRepository;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryashentsev on 08.11.2016.
 */
public class ApiTest {

    private class SomeCommand {
    }

    private class SomeUnknownCommand {
    }

    private static final TestProjection STORED_INNER = new TestProjection("qweqwe", 321321, null);
    private static final TestProjection STORED_PROJECTION = new TestProjection("strstr", 123123, STORED_INNER);

    private Api api;
    private IProjectionRepository projectionRepository;
    private ICommandExecutor<SomeCommand> commandExecutor;
    private CommandExecutorsProvider commandExecutorsProvider;

    @Before
    public void init() {
        projectionRepository = mock(IProjectionRepository.class);
        commandExecutor = mock(ICommandExecutor.class);
        commandExecutorsProvider = mock(CommandExecutorsProvider.class);
        when(commandExecutorsProvider.getCommandExecutorForCommandClass(SomeCommand.class)).thenReturn(commandExecutor);
        api = new Api(projectionRepository, commandExecutorsProvider);
    }

    @Test
    public void executeCommandForUnknownExecutor() {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        SomeUnknownCommand command = new SomeUnknownCommand();
        api.execute(command).subscribe(subscriber);

        subscriber.assertError(CommandExecutorNotFound.class);
        assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @Test
    public void executeNullCommand() {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        api.execute(null).subscribe(subscriber);

        subscriber.assertError(IllegalArgumentException.class);
        assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @Test
    public void executeCommand() {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();

        SomeCommand command = new SomeCommand();
        when(commandExecutor.process(command)).thenReturn(Observable.just(null));
        api.execute(command).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertValue(null);

        verify(commandExecutor).process(command);
    }

    @Test
    public void getNonExistentProjection() {
        when(projectionRepository.observe(TestProjection.class)).thenReturn(Observable.never());

        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        api.observe(TestProjection.class).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertNoValues();
        subscriber.assertNotCompleted();

        verify(projectionRepository).observe(TestProjection.class);
    }

    @Test
    public void getProjection() {
        when(projectionRepository.observe(TestProjection.class)).thenReturn(Observable.create(subscriber -> subscriber.onNext(STORED_PROJECTION)));

        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        api.observe(TestProjection.class).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();

        verify(projectionRepository).observe(TestProjection.class);

        assertTrue(subscriber.getOnNextEvents().size() == 1);
        TestProjection projection = subscriber.getOnNextEvents().get(0);
        assertTrue(STORED_PROJECTION == projection);
    }

}