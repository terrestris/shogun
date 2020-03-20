package de.terrestris.shogun.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "imagefiles")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ImageFile extends File {

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @JsonIgnore
    @Column(length = Integer.MAX_VALUE)
    private byte[] thumbnail;
}
