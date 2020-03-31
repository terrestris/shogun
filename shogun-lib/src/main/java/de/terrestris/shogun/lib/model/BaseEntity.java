package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public abstract class BaseEntity implements Serializable {

    // TODO Replace with @GeneratedValue(strategy = GenerationType.IDENTITY) and remove hibernate_sequence from flyway migrations
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    private Date created;

    @UpdateTimestamp
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    private Date modified;

}
