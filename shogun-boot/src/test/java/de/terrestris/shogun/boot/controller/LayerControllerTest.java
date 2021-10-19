/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2021-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.boot.controller;

import de.terrestris.shogun.lib.controller.LayerController;
import de.terrestris.shogun.lib.enumeration.LayerType;
import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.repository.LayerRepository;

import java.util.ArrayList;
import java.util.List;

public class LayerControllerTest extends BaseControllerTest<LayerController, LayerRepository, Layer> {

    public void setBaseEntity() {
        entityClass = Layer.class;
    }

    public void setBasePath() {
        basePath = "/layers";
    }

    public void insertTestData() {
        Layer entity1 = new Layer();
        Layer entity2 = new Layer();
        Layer entity3 = new Layer();

        entity1.setName("Layer 1");
        entity1.setType(LayerType.WMS);
        entity2.setName("Layer 2");
        entity2.setType(LayerType.WFS);
        entity3.setName("Layer 3");
        entity3.setType(LayerType.TILEWMS);

        ArrayList<Layer> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        List<Layer> persistedEntities = (List<Layer>) repository.saveAll(entities);

        testData = persistedEntities;
    }

}
