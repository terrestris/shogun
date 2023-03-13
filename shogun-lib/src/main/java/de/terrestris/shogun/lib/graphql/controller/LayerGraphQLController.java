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
import de.terrestris.shogun.lib.graphql.dto.MutateLayer;
import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.service.LayerService;
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
public class LayerGraphQLController extends BaseGraphQLController<Layer, LayerService> {

    @QueryMapping
    public DefaultGraphQLConnection<Layer> allLayers(@Argument("page") Integer page, @Argument("size") Integer size) {
        return super.findAll(page, size);
    }

    @QueryMapping
    public Optional<Layer> layerById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<Layer> layerByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, Layer>> layerByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, Layer> layerRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<Layer> allLayersByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public Layer createLayer(@Argument("entity") MutateLayer mutateLayer) {
        return super.create(mutateLayer);
    }

    @MutationMapping
    public Layer updateLayer(@Argument("id") Long id, @Argument("entity") MutateLayer mutateLayer) {
        return super.update(id, mutateLayer);
    }

    @MutationMapping
    public Boolean deleteLayer(@Argument("id") Long id) {
        return super.delete(id);
    }

}
