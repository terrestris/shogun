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
package de.terrestris.shogun.interceptor.security.access.entity;

import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Permission evaluator for {@link InterceptorRule}s
 */
@Component
public class InterceptorRulePermissionEvaluator extends BaseEntityPermissionEvaluator<InterceptorRule> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Override
    public boolean hasPermission(User user, InterceptorRule entity, PermissionType permission) {

        if (securityContextUtil.isInterceptorAdmin()) {
            return true;
        }

        return super.hasPermission(user, entity, permission);
    }

}


