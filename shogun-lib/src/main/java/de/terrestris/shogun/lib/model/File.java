package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "files")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class File extends BaseEntity {

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @Type(type="pg-uuid")
    private UUID fileUuid = UUID.randomUUID();

    @Column
    private Boolean active;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @JsonIgnore
    @Column(length = Integer.MAX_VALUE)
    private byte[] file;
}
