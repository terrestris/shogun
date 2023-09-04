/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2023-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
