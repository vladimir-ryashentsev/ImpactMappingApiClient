package ru.kackbip.impactMapping.api.specification.goalsManagement;

import org.jbehave.core.annotations.When;

import java.util.Date;
import java.util.UUID;

import ru.kackbip.impactMapping.api.IApi;
import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import ru.kackbip.impactMapping.api.commands.RemoveGoalCommand;
import rx.observers.TestSubscriber;

/**
 * Created by ryashentsev on 23.11.2016.
 */

public class WhenSteps {

    private final IApi api;

    public WhenSteps(IApi api){
        this.api = api;
    }

    @When("I add a goal(title=$title, date=$date)")
    public void iAddAGoal(String title, Date date) {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        AddGoalCommand command = new AddGoalCommand(UUID.randomUUID(), title, date);
        api.execute(command).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertValue(null);
    }

    @When("I remove the goal(id=$id)")
    public void iRemoveTheGoal(UUID id){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        RemoveGoalCommand command = new RemoveGoalCommand(id);
        api.execute(command).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertValue(null);
    }
}
