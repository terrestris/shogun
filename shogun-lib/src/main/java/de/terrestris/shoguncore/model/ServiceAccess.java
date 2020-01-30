package de.terrestris.shoguncore.model;

import de.terrestris.shoguncore.enumeration.EventType;
import de.terrestris.shoguncore.enumeration.OperationType;
import de.terrestris.shoguncore.enumeration.RuleType;
import de.terrestris.shoguncore.enumeration.ServiceType;
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

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public RuleType getRule() {
        return rule;
    }

    public void setRule(RuleType rule) {
        this.rule = rule;
    }
}
