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
package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.annotation.GraphQLQuery;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.service.ApplicationService;
import de.terrestris.shogun.lib.service.entityidcheck.ApplicationIdCheckService;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationGraphQLDataFetcher extends BaseGraphQLDataFetcher<Application, ApplicationService> {

    @Autowired
    protected ApplicationIdCheckService applicationIdCheckService;

    @GraphQLQuery(name = "applicationIdStartsWith")
    public DataFetcher<List<Long>> applicationIdStartsWith() {
        return dataFetchingEnvironment -> {
            Long searchId = dataFetchingEnvironment.getArgument("id");
            return searchId == null ? List.of() : applicationIdCheckService.idStartsWith(searchId);
        };
    }
}
