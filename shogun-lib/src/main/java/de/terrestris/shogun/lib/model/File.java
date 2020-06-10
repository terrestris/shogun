package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "files")
@Table(schema = "shogun")
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
