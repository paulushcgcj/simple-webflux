package io.github.paulushcgcj.devopsdemo.configuration;

import io.cucumber.core.options.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("bdd")
@ConfigurationParameter(key = Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@book or @author")
@DisplayName("Behaviour Test")
public class CucumberConfiguration {
}
