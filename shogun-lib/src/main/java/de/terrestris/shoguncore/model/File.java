package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
