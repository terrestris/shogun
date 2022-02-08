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

import de.terrestris.shogun.lib.controller.GroupController;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupControllerTest extends BaseControllerTest<GroupController, GroupRepository, Group> {

    public void setBaseEntity() {
        entityClass = Group.class;
    }

    public void setBasePath() {
        basePath = "/groups";
    }

    public void insertTestData() {
        Group entity1 = new Group();
        Group entity2 = new Group();
        Group entity3 = new Group();

        entity1.setKeycloakId(UUID.randomUUID().toString());
        entity2.setKeycloakId(UUID.randomUUID().toString());
        entity3.setKeycloakId(UUID.randomUUID().toString());

        ArrayList<Group> entities = new ArrayList<>();

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        testData = (List<Group>) repository.saveAll(entities);
    }

}
