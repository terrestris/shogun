package de.terrestris.shogun.lib.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "entityoperation")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EntityOperation extends BaseEntity {

    @Column(nullable = false)
    private String name;
}
