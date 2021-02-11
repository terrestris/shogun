package de.terrestris.shoguncore.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column
    @Getter @Setter
    private OffsetDateTime modified;

}
