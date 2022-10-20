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

import de.terrestris.shogun.lib.graphql.dto.MutateGroup;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.service.GroupService;
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
public class GroupGraphQLController extends BaseGraphQLController<Group, GroupService> {

    @QueryMapping
    public List<Group> allGroups() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<Group> groupById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<Group> groupByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, Group>> groupByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, Group> groupRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<Group> allGroupsByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public Group createGroup(@Argument("entity") MutateGroup mutateGroup) {
        return super.create(mutateGroup);
    }

    @MutationMapping
    public Group updateGroup(@Argument("id") Long id, @Argument("entity") MutateGroup mutateGroup) {
        return super.update(id, mutateGroup);
    }

    @MutationMapping
    public Boolean deleteGroup(@Argument("id") Long id) {
        return super.delete(id);
    }

}
