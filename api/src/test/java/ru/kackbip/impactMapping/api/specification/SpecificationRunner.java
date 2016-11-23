package ru.kackbip.impactMapping.api.specification;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import ru.kackbip.impactMapping.api.specification.goalsManagement.Steps;

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
        parameterConverters.addConverters(new ParameterConverters.DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ParameterConverters.ExamplesTableConverter(examplesTableFactory));
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
        return new InstanceStepsFactory(configuration(),
                new Steps());
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(SPECIFICATION_LOCATION, "**/*.story", "");
    }

    public static class MyStoryLoader extends LoadFromRelativeFile {
        public MyStoryLoader() {
            super(SPECIFICATION_LOCATION);
        }
    }
}
