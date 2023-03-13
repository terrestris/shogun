/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.dto.DefaultGraphQLConnection;
import de.terrestris.shogun.lib.graphql.dto.MutateApplication;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.service.ApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ApplicationGraphQLController extends BaseGraphQLController<Application, ApplicationService> {

    @QueryMapping
    public DefaultGraphQLConnection<Application> allApplications(@Argument("first") int first, @Argument("offset") int offset) {
        return super.findAll(first, offset);
    }

    @QueryMapping
    public Optional<Application> applicationById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<Application> applicationByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, Application>> applicationByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, Application> applicationRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<Application> allApplicationsByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public Application createApplication(@Argument("entity") MutateApplication mutateApplication) {
        return super.create(mutateApplication);
    }

    @MutationMapping
    public Application updateApplication(@Argument("id") Long id, @Argument("entity") MutateApplication mutateApplication) {
        return super.update(id, mutateApplication);
    }

    @MutationMapping
    public Boolean deleteApplication(@Argument("id") Long id) {
        return super.delete(id);
    }

}
