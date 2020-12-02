package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "imagefiles")
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
