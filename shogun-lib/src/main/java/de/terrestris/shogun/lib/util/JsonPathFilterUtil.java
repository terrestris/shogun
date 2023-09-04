/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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
import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class JsonPathFilterUtil {

    /**
     * Receives a {@link com.jayway.jsonpath.Filter} and returns the PostgreSQL compatible
     * <a href="https://www.postgresql.org/docs/current/functions-json.html">JSON path predicate</a>.
     *
     * @param filter The filter to write as string.
     * @return {@link String} The JSON path predicate.
     */
    public static String writeFilter(Filter filter) {
        String placeholder = "$";

//        try {
//            Field predicateField = filter.getClass().getDeclaredField("predicate");
//            predicateField.setAccessible(true);
//            Object predicate = predicateField.get(filter);
//
//            if (predicate.getClass().isAssignableFrom(LogicalExpressionNode.class)) {
//                LogicalExpressionNode prd = ((LogicalExpressionNode) predicate);
//                System.out.println(prd);
//            }
//
//            if (predicate.getClass().isAssignableFrom(RelationalExpressionNode.class)) {
//                RelationalExpressionNode prd = ((RelationalExpressionNode) predicate);
//                System.out.println(prd);
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }

        if (filter == null) {
            return placeholder;
        }

        String filterString = filter.toString();

        Pattern pathPattern = Pattern.compile("\\[\\?\\((.+)\\)\\]", Pattern.CASE_INSENSITIVE);
        Matcher pathPatternMatcher = pathPattern.matcher(filterString);

        if (!pathPatternMatcher.find()) {
            return placeholder;
        }

        String extractedFilterString = pathPatternMatcher.group(1);

        Pattern camelCasePattern = Pattern.compile("(?<=\\$\\[')(\\w+)(?='\\])", Pattern.CASE_INSENSITIVE);
        Matcher camelCasePatternMatcher = camelCasePattern.matcher(extractedFilterString);

        String replacedFilterString = camelCasePatternMatcher
            .replaceAll(match -> match.group()
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase()
            );

        Pattern regeExPattern = Pattern.compile("=~ \\/(.+)\\/");
        Matcher regeExPatternMatcher = regeExPattern.matcher(replacedFilterString);

        replacedFilterString = regeExPatternMatcher
            .replaceAll(match -> match.group()
                .replace("/", "\"")
                .replace("=~", "like_regex")
            );

        return replacedFilterString
            .replace("['", ".")
            .replace("']", "")
            .replace("'", "\"");
    }

}
