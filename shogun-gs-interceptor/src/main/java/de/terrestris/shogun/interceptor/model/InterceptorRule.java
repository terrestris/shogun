package de.terrestris.shogun.interceptor.model;

import de.terrestris.shogun.interceptor.enumeration.HttpEnum;
import de.terrestris.shogun.interceptor.enumeration.InterceptorEnum;
import de.terrestris.shogun.interceptor.enumeration.OgcEnum;
import de.terrestris.shogun.lib.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(schema = "interceptor")
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
