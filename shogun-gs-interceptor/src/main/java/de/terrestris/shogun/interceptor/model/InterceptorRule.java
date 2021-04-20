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
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
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
}
