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

    @Column
    private Integer width;

    @Column
    private Integer height;

    @JsonIgnore
    @ToString.Exclude
    @Column(length = Integer.MAX_VALUE)
    private byte[] thumbnail;
}
