package de.terrestris.shogun.lib.model.security.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity(name = "publicentities")
@Table(schema = "shogun")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class PublicEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    @Getter @Setter
    @Schema(
        description = "The timestamp of creation.",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private OffsetDateTime created;

}
