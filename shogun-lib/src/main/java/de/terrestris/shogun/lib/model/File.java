package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

@Entity(name = "files")
@Table(schema = "shogun")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class File extends BaseEntity {

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @Type(type="pg-uuid")
    @Getter
    private UUID fileUuid = UUID.randomUUID();

    @Column
    @Getter @Setter
    private Boolean active;

    @Column(nullable = false)
    @Getter @Setter
    private String fileName;

    @Column(nullable = false)
    @Getter @Setter
    private String fileType;

    @JsonIgnore
    @ToString.Exclude
    @Column(length = Integer.MAX_VALUE)
    @Getter @Setter
    private byte[] file;
}
