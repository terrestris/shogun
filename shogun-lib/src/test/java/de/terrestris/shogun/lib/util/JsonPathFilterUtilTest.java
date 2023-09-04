package de.terrestris.shogun.lib.util;

import com.jayway.jsonpath.Filter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonPathFilterUtilTest {

    @Test
    public void writeFilter_returns_a_placeholder_without_any_given_filter() {
        String filter = JsonPathFilterUtil.writeFilter(null);

        assertEquals("$", filter);
    }

    @Test
    public void writeFilter_returns_the_filter_without_surrounding_brackets() {
        Filter testFilter = Filter.parse("[?($.name == \"Countries\")]");
        String filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.name == \"Countries\"", filter);

        testFilter = Filter.parse("[?($.min > 1909)]");
        filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.min > 1909", filter);
    }

//    // TODO Not supported right now.
//    @Test
//    public void writeFilter_returns_the_negate_filter() {
//        Filter testFilter = Filter.parse("[?(!($.min > 1909))]");
//        String filter = JsonPathFilterUtil.writeFilter(testFilter);
//
//        assertEquals("$.min > 1909", filter);
//    }

    @Test
    public void writeFilter_returns_the_regex_filter() {
        Filter testFilter = Filter.parse("[?($.name =~ /^ab.*c/)]");
        String filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.name like_regex \"^ab.*c\"", filter);
    }

    @Test
    public void writeFilter_replaces_camel_case_attributes_with_snake_case() {
        Filter testFilter = Filter.parse("[?($.clientConfig.minResolution > 1909)]");
        String filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.client_config.minResolution > 1909", filter);

        testFilter = Filter.parse("[?($.clientConfig.minResolution > 1909 && $.sourceConfig.maxResolution < 1909)]");
        filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.client_config.minResolution > 1909 && $.source_config.maxResolution < 1909", filter);

        testFilter = Filter.parse("[?($.clientConfig.minResolution > 1909 && $.clientConfig.maxResolution < 1909)]");
        filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.client_config.minResolution > 1909 && $.client_config.maxResolution < 1909", filter);

        testFilter = Filter.parse("[?($.clientConfig.clientConfig.minResolution > 1909 && $.clientConfig.maxResolution < 1909)]");
        filter = JsonPathFilterUtil.writeFilter(testFilter);

        assertEquals("$.client_config.clientConfig.minResolution > 1909 && $.client_config.maxResolution < 1909", filter);
    }

}
