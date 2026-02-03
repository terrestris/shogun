/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2026-present terrestris GmbH & Co. KG
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

import de.terrestris.shogun.lib.graphql.dto.MutateTextualContent;
import de.terrestris.shogun.lib.model.TextualContent;
import de.terrestris.shogun.lib.service.TextualContentService;
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
public class TextualContentGraphQLController extends BaseGraphQLController<TextualContent, TextualContentService> {

    @QueryMapping
    public List<TextualContent> allTextualContents() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<TextualContent> textualContentById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<TextualContent> textualContentByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, TextualContent>> textualContentByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, TextualContent> textualContentRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<TextualContent> allTextualContentsByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public TextualContent createTextualContent(@Argument("entity") MutateTextualContent mutateTextualContent) {
        return super.create(mutateTextualContent);
    }

    @MutationMapping
    public TextualContent updateTextualContent(@Argument("id") Long id, @Argument("entity") MutateTextualContent mutateTextualContent) {
        return super.update(id, mutateTextualContent);
    }

    @MutationMapping
    public Boolean deleteTextualContent(@Argument("id") Long id) {
        return super.delete(id);
    }

}
