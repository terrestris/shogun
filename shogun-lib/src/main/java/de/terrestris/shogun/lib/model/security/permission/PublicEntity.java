package de.terrestris.shogun.lib.model.security.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity(name = "publicentities")
@Table(schema = "shogun")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
public class PublicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(unique = true, nullable = false)
    @Getter
    @Schema(
        description = "The ID.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Column(unique = true, nullable = false)
    private Long entityId;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    @Schema(
        description = "The timestamp of creation.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private OffsetDateTime created;

    @UpdateTimestamp
    @Column
    @Getter @Setter
    @Schema(
        description = "The timestamp of the last modification.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private OffsetDateTime modified;

}
