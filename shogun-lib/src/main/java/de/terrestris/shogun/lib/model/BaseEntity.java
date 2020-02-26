package de.terrestris.shogun.lib.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class BaseEntity implements Serializable {

    // TODO Don't create setter for ID?!
    @Id
    // TODO Replace with @GeneratedValue(strategy = GenerationType.IDENTITY) and remove hibernate_sequence from flyway migrations
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @UpdateTimestamp
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
