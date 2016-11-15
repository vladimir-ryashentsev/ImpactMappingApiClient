package ru.kackbip.impactMapping.api.projections.repository;

import org.junit.Before;
import org.junit.Test;

import ru.kackbip.impactMapping.api.TestProjection;
import ru.kackbip.infrastructure.storage.pojo.IPojoStorage;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ryashentsev on 07.11.2016.
 */

public class ProjectionRepositoryTest {
    private class SomeNonExistentProjection {}
    private static final String ACTUAL_KEY = TestProjection.class.getCanonicalName();
    private static final String EMPTY_KEY = SomeNonExistentProjection.class.getCanonicalName();
    private static final TestProjection STORED_PROJECTION = new TestProjection("strstr", 123123, new TestProjection("qweqwe", 321321, null));
    private static final TestProjection STORED_PROJECTION2 = new TestProjection("strstr2", 1231232, new TestProjection("qweqwe2", 3213212, null));

    private ProjectionRepository projectionRepository;
    private IPojoStorage pojoStorage;

    @Before
    public void init(){
        pojoStorage = mock(IPojoStorage.class);
        when(pojoStorage.store(ACTUAL_KEY, STORED_PROJECTION)).thenReturn(Observable.empty());
        when(pojoStorage.observe(ACTUAL_KEY, TestProjection.class)).thenReturn(Observable.create(subscriber -> subscriber.onNext(STORED_PROJECTION)));
        when(pojoStorage.get(ACTUAL_KEY, TestProjection.class)).thenReturn(Observable.just(STORED_PROJECTION));
        when(pojoStorage.observe(EMPTY_KEY, SomeNonExistentProjection.class)).thenReturn(Observable.never());
        when(pojoStorage.get(EMPTY_KEY, SomeNonExistentProjection.class)).thenReturn(Observable.error(new ProjectionNotFoundException()));

        projectionRepository = new ProjectionRepository(pojoStorage);
    }

    @Test
    public void callStoreOfInnerRepositoryWithRightKeyAndValue_whenStore(){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        projectionRepository.store(STORED_PROJECTION).subscribe(subscriber);
        verify(pojoStorage).store(ACTUAL_KEY, STORED_PROJECTION);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void getProjectionFromPojoStorageWithRightKeyAndClass_whenGet(){
        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        projectionRepository.get(TestProjection.class).subscribe(subscriber);
        verify(pojoStorage).get(ACTUAL_KEY, TestProjection.class);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertValue(STORED_PROJECTION);
    }

    @Test
    public void getProjectionFromPojoStorageWithRightKeyAndClass_whenObserve(){
        TestSubscriber<TestProjection> subscriber = new TestSubscriber<>();
        projectionRepository.observe(TestProjection.class).subscribe(subscriber);
        verify(pojoStorage).observe(ACTUAL_KEY, TestProjection.class);
        subscriber.assertNoErrors();
        subscriber.assertNotCompleted();
        subscriber.assertValue(STORED_PROJECTION);
    }
}
