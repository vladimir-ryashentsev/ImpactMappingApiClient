package ru.kackbip.impactMapping.api.specification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import ru.kackbip.impactMapping.api.Api;
import ru.kackbip.impactMapping.api.IApi;
import ru.kackbip.impactMapping.api.commands.executors.CommandExecutorsProvider;
import ru.kackbip.impactMapping.api.commands.executors.local.LocalCommandExecutorsProvider;
import ru.kackbip.impactMapping.api.projections.repository.ProjectionRepository;
import ru.kackbip.impactMapping.api.specification.goalsManagement.GivenSteps;
import ru.kackbip.impactMapping.api.specification.goalsManagement.ThenSteps;
import ru.kackbip.impactMapping.api.specification.goalsManagement.WhenSteps;
import ru.kackbip.infrastructure.storage.pojo.GsonPojoStorage;
import ru.kackbip.infrastructure.storage.pojo.GsonStringifier;
import ru.kackbip.infrastructure.storage.pojo.IStringifier;
import ru.kackbip.infrastructure.storage.string.IStringStorage;
import ru.kackbip.infrastructure.storage.string.local.InMemoryStringStorage;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML_TEMPLATE;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML_TEMPLATE;

/**
 * Created by ryashentsev on 23.11.2016.
 */

public class SpecificationRunner extends JUnitStories {

    private static URL SPECIFICATION_LOCATION;

    static {
        try {
            SPECIFICATION_LOCATION = new URL("file://".concat(System.getProperty("user.dir")).concat(File.separator).concat("specification"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private final CrossReference xref = new CrossReference();

    public SpecificationRunner() {
        configuredEmbedder().embedderControls().doGenerateViewAfterStories(true).doIgnoreFailureInStories(false)
                .doIgnoreFailureInView(true).doVerboseFailures(true).useThreads(2).useStoryTimeouts("10");
    }

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();
        Properties viewResources = new Properties();
        viewResources.put("decorateNonHtml", "true");
        ParameterConverters parameterConverters = new ParameterConverters();
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromClasspath(embeddableClass), parameterConverters);
        parameterConverters.addConverters(
                new ParameterConverters.DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ParameterConverters.ExamplesTableConverter(examplesTableFactory),
                new UUIDConverter()
        );
        return new MostUsefulConfiguration()
                .useStoryLoader(new MyStoryLoader())
                .useStoryParser(new GherkinStoryParser())
                .useStoryReporterBuilder(new StoryReporterBuilder()
                        .withCodeLocation(codeLocationFromClass(embeddableClass))
                        .withDefaultFormats()
                        .withViewResources(viewResources)
                        .withFormats(CONSOLE, TXT, HTML_TEMPLATE, XML_TEMPLATE)
                        .withFailureTrace(true)
                        .withFailureTraceCompression(true)
                        .withCrossReference(xref))
                .useParameterConverters(parameterConverters)
                .useStepPatternParser(new RegexPrefixCapturingPatternParser());
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        IApi api = createApi();
        return new InstanceStepsFactory(configuration(),
                new GivenSteps(api),
                new WhenSteps(api),
                new ThenSteps(api));
    }

    private IApi createApi() {
        IStringStorage stringStorage = new InMemoryStringStorage();
        Gson gson = new GsonBuilder().create();
        IStringifier stringifier = new GsonStringifier(gson);
        GsonPojoStorage pojoStorage = new GsonPojoStorage(stringStorage, stringifier);
        ProjectionRepository projectionRepository = new ProjectionRepository(pojoStorage);
        CommandExecutorsProvider executorsProvider = new LocalCommandExecutorsProvider(projectionRepository);
        return new Api(projectionRepository, executorsProvider);
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(SPECIFICATION_LOCATION, "**/*.story", "");
    }

    private static class MyStoryLoader extends LoadFromRelativeFile {
        public MyStoryLoader() {
            super(SPECIFICATION_LOCATION);
        }
    }

    public class UUIDConverter implements ParameterConverters.ParameterConverter {
        @Override
        public boolean accept(Type type) {
            if (type instanceof Class<?>) {
                return UUID.class.isAssignableFrom((Class<?>) type);
            }
            return false;
        }

        @Override
        public Object convertValue(String value, Type type) {
            return UUID.fromString(value);
        }
    }
}
