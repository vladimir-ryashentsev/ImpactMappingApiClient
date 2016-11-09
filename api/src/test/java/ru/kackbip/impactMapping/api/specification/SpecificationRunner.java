package ru.kackbip.impactMapping.api.specification;

import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.UsingSteps;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.AnnotatedEmbedderRunner;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.parsers.gherkin.GherkinStoryParser;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ParameterConverters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import ru.kackbip.impactMapping.api.specification.goalsManagement.Steps;

/**
 * Created by ryashentsev on 01.11.2016.
 */

@RunWith(AnnotatedEmbedderRunner.class)
@Configure(storyControls = SpecificationRunner.MyStoryControls.class, storyLoader = SpecificationRunner.MyStoryLoader.class, storyReporterBuilder = SpecificationRunner.MyReportBuilder.class,
        parameterConverters = { SpecificationRunner.MyDateConverter.class }, storyParser = GherkinStoryParser.class)
@UsingEmbedder(embedder = Embedder.class, generateViewAfterStories = true, ignoreFailureInStories = true, ignoreFailureInView = true, verboseFailures = true,
        storyTimeoutInSecs = 100, threads = 2, metaFilters = "-skip")
@UsingSteps(instances = { Steps.class })
public class SpecificationRunner extends InjectableEmbedder {

    private static URL SPECIFICATION_LOCATION;

    static {
        try {
            SPECIFICATION_LOCATION = new URL("file://".concat(System.getProperty("user.dir")).concat(File.separator).concat("specification"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void run() throws MalformedURLException {
        List<String> storyPaths = new StoryFinder().findPaths(SPECIFICATION_LOCATION, "**/*.story", "");
        injectedEmbedder().runStoriesAsPaths(storyPaths);
    }

    public static class MyStoryControls extends StoryControls {
        public MyStoryControls() {
            doDryRun(false);
            doSkipScenariosAfterFailure(false);
        }
    }

    public static class MyStoryLoader extends LoadFromRelativeFile {

        public MyStoryLoader() {
            super(SPECIFICATION_LOCATION);
        }
    }

    public static class MyReportBuilder extends StoryReporterBuilder {
        public MyReportBuilder() {
            this.withFormats(org.jbehave.core.reporters.Format.CONSOLE, org.jbehave.core.reporters.Format.TXT, org.jbehave.core.reporters.Format.HTML, org.jbehave.core.reporters.Format.XML).withDefaultFormats();
        }
    }

    public static class MyRegexPrefixCapturingPatternParser extends RegexPrefixCapturingPatternParser {
        public MyRegexPrefixCapturingPatternParser() {
            super("%");
        }
    }

    public static class MyDateConverter extends ParameterConverters.DateConverter {
        public MyDateConverter() {
            super(new SimpleDateFormat("yyyy-MM-dd"));
        }
    }

}
