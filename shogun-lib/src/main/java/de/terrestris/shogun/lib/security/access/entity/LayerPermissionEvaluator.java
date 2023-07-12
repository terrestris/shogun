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
package de.terrestris.shogun.lib.security.access.entity;

import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.model.User;
import lombok.extern.log4j.Log4j2;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class LayerPermissionEvaluator extends BaseEntityPermissionEvaluator<Layer> {

    @Autowired
    HibernateTemplate hibernateTemplate;

    @Autowired
    ApplicationPermissionEvaluator applicationPermissionEvaluator;

    @Override
    public boolean hasPermission(User user, Layer entity, PermissionType permission) {
        // Get all applications the layer is loaded in.
        String sql =
            "SELECT " +
            "  * " +
            "FROM " +
            "  applications a " +
            "WHERE " +
            "  jsonb_path_exists(" +
            "    a.layer_tree, " +
            "    '$.** ? (@.\"layerId\" == $id)', " +
            "    jsonb_build_object('id', ?1)" +
            "  )";

        hibernateTemplate.setCacheQueries(true);
        List<Application> applications = hibernateTemplate.execute(session -> {
            Query<Application> query = session.createNativeQuery(sql, Application.class);
            query.setParameter(1, entity.getId());
            return query.list();
        });

        // Check if the user is allowed to read at least a single application.
        boolean isAllowed = false;
        if (applications != null) {
            isAllowed = applications.stream()
                .anyMatch(application -> applicationPermissionEvaluator.hasPermission(user, application, permission));
        }

        return isAllowed;
    }
}
