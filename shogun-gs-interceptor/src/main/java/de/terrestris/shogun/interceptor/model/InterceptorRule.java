package de.terrestris.shogun.interceptor.model;

import de.terrestris.shoguncore.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class InterceptorRule extends BaseEntity {
    private String test;
}
