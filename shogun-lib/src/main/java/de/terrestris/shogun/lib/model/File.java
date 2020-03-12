package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "files")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class File extends BaseEntity {

    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
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
