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
package de.terrestris.shogun.interceptor.model;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.lib.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="interceptorrules")
public class InterceptorRule extends BaseEntity {

    /**
     * The event for this rule, possible values are:
     * * REQUEST
     * * RESPONSE
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpEnum.EventType event;

    /**
     * The rule type for this rule, possible rules are:
     * * ALLOW
     * * DENY
     * * MODIFY
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InterceptorEnum.RuleType rule;

    /**
     * The OGC service type, possible rules are:
     * * WMS
     * * WFS
     * * WCS
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OgcEnum.ServiceType service;

    /**
     * The OGC operation type, e.g. GetMap.
     */
    @Enumerated(EnumType.STRING)
    private OgcEnum.OperationType operation;

    /**
     * The OGC/GeoServer endPoint (a generalization for layer, featureType,
     * coverage or namespace), e.g. SHOGUN:SHINJI.
     */
    private String endPoint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InterceptorRule that = (InterceptorRule) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
