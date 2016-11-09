package ru.kackbip.impactMapping.api.projections.repository;

import org.junit.Before;
import org.junit.Test;

import ru.kackbip.impactMapping.api.TestProjection;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionNotFoundException;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionRepository;
import ru.kackbip.infrastructure.storage.NotFoundException;
import ru.kackbip.infrastructure.storage.pojo.IPojoStorage;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.*;

/**
 * Created by ryashentsev on 07.11.2016.
 */

public class ProjectionRepositoryTest {
    private class SomeNoneExistentProjection{}
    private static final String ACTUAL_KEY = TestProjection.class.getCanonicalName();
    private static final String EMPTY_KEY = SomeNoneExistentProjection.class.getCanonicalName();
    private static final TestProjection STORED_PROJECTION = new TestProjection("strstr", 123123, new TestProjection("qweqwe", 321321, null));

    private ProjectionRepository projectionRepository;
    private IPojoStorage pojoStorage;

    @Before
    public void init(){
        pojoStorage = mock(IPojoStorage.class);
        when(pojoStorage.store(ACTUAL_KEY, STORED_PROJECTION)).thenReturn(Observable.empty());
        when(pojoStorage.restore(ACTUAL_KEY, TestProjection.class)).thenReturn(Observable.just(STORED_PROJECTION));
        when(pojoStorage.restore(EMPTY_KEY, SomeNoneExistentProjection.class)).thenReturn(Observable.error(new NotFoundException("Projection not found.")));

        projectionRepository = new ProjectionRepository(pojoStorage);
    }

    @Test
    public void restoreNonExistentProjection(){
        TestSubscriber<SomeNoneExistentProjection> subscriber = new TestSubscriber<>();
        projectionRepository.restore(SomeNoneExistentProjection.class).subscribe(subscriber);

        verify(pojoStorage).restore(EMPTY_KEY, SomeNoneExistentProjection.class);

        assertTrue(subscriber.getOnNextEvents().isEmpty());
        subscriber.assertError(ProjectionNotFoundException.class);
    }

    @Test
    public void restoreProjection(){
        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        projectionRepository.restore(TestProjection.class).subscribe(subscriber);

        verify(pojoStorage).restore(ACTUAL_KEY, TestProjection.class);

        subscriber.assertNoErrors();
        subscriber.assertValue(STORED_PROJECTION);
        subscriber.assertCompleted();
    }

    @Test
    public void storeProjection(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        projectionRepository.store(STORED_PROJECTION).subscribe(subscriber);

        verify(pojoStorage).store(ACTUAL_KEY, STORED_PROJECTION);

        subscriber.assertNoErrors();
        assertTrue(subscriber.getOnNextEvents().isEmpty());
        subscriber.assertCompleted();
    }
}
