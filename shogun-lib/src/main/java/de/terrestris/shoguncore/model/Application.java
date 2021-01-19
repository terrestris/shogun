package de.terrestris.shoguncore.model;

import de.terrestris.shoguncore.model.jsonb.ApplicationClientConfig;
import de.terrestris.shoguncore.model.jsonb.Locale;
import de.terrestris.shoguncore.model.jsonb.ApplicationLayerConfig;
import de.terrestris.shoguncore.model.jsonb.ApplicationToolConfig;
import de.terrestris.shoguncore.model.jsonb.ApplicationLayerTree;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity(name = "applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Application extends BaseEntity {

    @Column
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Locale i18n;

    @Column
    private Boolean stateOnly;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private ApplicationClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private ApplicationLayerTree layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private ApplicationLayerConfig layerConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private ApplicationToolConfig toolConfig;
}
