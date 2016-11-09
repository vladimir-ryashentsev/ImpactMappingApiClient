package ru.kackbip.impactMapping.api.specification.goalsManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kackbip.impactMapping.api.Api;
import ru.kackbip.impactMapping.api.IApi;
import ru.kackbip.impactMapping.api.commands.CreateGoalCommand;
import ru.kackbip.impactMapping.api.commands.executors.CreateGoalCommandExecutor;
import ru.kackbip.impactMapping.api.commands.executors.ICommandExecutor;
import ru.kackbip.impactMapping.api.projections.Goals;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionRepository;
import ru.kackbip.infrastructure.storage.pojo.GsonPojoStorage;
import ru.kackbip.infrastructure.storage.pojo.GsonStringifier;
import ru.kackbip.infrastructure.storage.pojo.IStringifier;
import ru.kackbip.infrastructure.storage.string.IStringStorage;
import ru.kackbip.infrastructure.storage.string.local.InMemoryStringStorage;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by ryashentsev on 01.11.2016.
 */

public class Steps {

    private IApi api;

    @BeforeScenario
    public void someBeforeMethod() {
        IStringStorage stringStorage = new InMemoryStringStorage();
        Gson gson = new GsonBuilder().create();
        IStringifier stringifier = new GsonStringifier(gson);
        GsonPojoStorage pojoStorage = new GsonPojoStorage(stringStorage, stringifier);
        ProjectionRepository projectionRepository = new ProjectionRepository(pojoStorage);
        Map<Class, ICommandExecutor> commandExecutors = new HashMap<>();
        commandExecutors.put(CreateGoalCommand.class, new CreateGoalCommandExecutor(projectionRepository));
        api = new Api(projectionRepository, commandExecutors);
    }

    @When("I add a goal(title=$title, date=$date)")
    public void iAddAGoal(String title, Date date) {
        TestSubscriber<Void> subscriber = new TestSubscriber<>();
        CreateGoalCommand command = new CreateGoalCommand(title, date);
        api.execute(command).subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
        assertTrue(subscriber.getOnNextEvents().isEmpty());
    }

    @Then("the goal(title=$title, date=$date) appears in the goals list")
    public void theGoalAppearsInTheGoalsList(String title, Date date) {
        TestSubscriber<Goals> subscriber = new TestSubscriber<>();
        api.get(Goals.class).subscribe(subscriber);

        subscriber.assertCompleted();
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

}
