package de.terrestris.shogun.lib.model;

import de.terrestris.shogun.lib.enumeration.EventType;
import de.terrestris.shogun.lib.enumeration.OperationType;
import de.terrestris.shogun.lib.enumeration.RuleType;
import de.terrestris.shogun.lib.enumeration.ServiceType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity(name = "serviceaccess")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServiceAccess extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType event;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType operation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceType service;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleType rule;
}
