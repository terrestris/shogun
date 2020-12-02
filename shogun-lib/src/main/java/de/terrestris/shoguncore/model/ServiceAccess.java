package de.terrestris.shoguncore.model;

import de.terrestris.shoguncore.enumeration.EventType;
import de.terrestris.shoguncore.enumeration.OperationType;
import de.terrestris.shoguncore.enumeration.RuleType;
import de.terrestris.shoguncore.enumeration.ServiceType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "serviceaccess")
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
