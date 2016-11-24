package ru.kackbip.impactMapping.api.specification.goalsManagement;

import org.jbehave.core.annotations.Given;

import java.util.Date;
import java.util.UUID;

import ru.kackbip.impactMapping.api.IApi;
import ru.kackbip.impactMapping.api.commands.AddGoalCommand;
import rx.observers.TestSubscriber;

/**
 * Created by ryashentsev on 23.11.2016.
 */

public class GivenSteps {

    private final IApi api;

    public GivenSteps(IApi api){
        this.api = api;
    }

    @Given("I have the goal(id=$id)")
    public void iHasTheGoal(UUID id){
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        AddGoalCommand command = new AddGoalCommand(id, "", new Date());
        api.execute(command).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
        subscriber.assertValue(null);
    }
}
