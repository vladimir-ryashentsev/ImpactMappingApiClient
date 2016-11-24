package ru.kackbip.impactMapping.api.specification.goalsManagement;

import org.jbehave.core.annotations.Then;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.kackbip.impactMapping.api.IApi;
import ru.kackbip.impactMapping.api.projections.Goals;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by ryashentsev on 01.11.2016.
 */

public class ThenSteps {

    private final IApi api;

    public ThenSteps(IApi api){
        this.api = api;
    }

    @Then("the goal(title=$title, date=$date) appears in the goals list")
    public void theGoalAppearsInTheGoalsList(String title, Date date) {
        TestSubscriber<Goals> subscriber = new TestSubscriber<>();
        api.observe(Goals.class).subscribe(subscriber);

        subscriber.assertNotCompleted();
        subscriber.assertNoErrors();

        List<Goals> projections = subscriber.getOnNextEvents();
        assertTrue(projections.size()==1);

        List<Goals.Goal> goals = projections.get(0).getGoals();
        assertNotNull(goals);
        assertTrue(goals.size()==1);

        Goals.Goal goal = goals.get(0);
        assertEquals(title, goal.getTitle());
        assertEquals(date, goal.getDate());
    }

    @Then("the goal(id=$id) dissapears from goals list")
    public void theGoalDissapearsFromGoalsList(UUID id){
        TestSubscriber<Goals> subscriber = new TestSubscriber<>();
        api.observe(Goals.class).subscribe(subscriber);

        subscriber.assertNotCompleted();
        subscriber.assertNoErrors();

        List<Goals> projections = subscriber.getOnNextEvents();
        assertTrue(projections.size()==1);

        List<Goals.Goal> goals = projections.get(0).getGoals();
        assertNotNull(goals);

        TestSubscriber<Goals.Goal> filterSubscriber = new TestSubscriber<>();
        Observable.from(goals)
                .filter(goal -> goal.getId().equals(id))
                .subscribe(filterSubscriber);

        filterSubscriber.assertNoValues();
    }
}
