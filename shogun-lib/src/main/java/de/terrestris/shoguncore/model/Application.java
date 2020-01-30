package de.terrestris.shoguncore.model;

import de.terrestris.shoguncore.model.jsonb.ApplicationClientConfig;
import de.terrestris.shoguncore.model.jsonb.Locale;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.Map;

@Entity(name = "applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Application extends BaseEntity {

    @Column()
    private String name;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Locale i18n;

    @Column()
    private Boolean stateOnly;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private ApplicationClientConfig clientConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> layerTree;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> layerConfig;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> toolConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getI18n() {
        return i18n;
    }

    public void setI18n(Locale i18n) {
        this.i18n = i18n;
    }

    public Boolean getStateOnly() {
        return stateOnly;
    }

    public void setStateOnly(Boolean stateOnly) {
        this.stateOnly = stateOnly;
    }

    public ApplicationClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ApplicationClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public Map<String, Object> getLayerTree() {
        return layerTree;
    }

    public void setLayerTree(Map<String, Object> layerTree) {
        this.layerTree = layerTree;
    }

    public Map<String, Object> getLayerConfig() {
        return layerConfig;
    }

    public void setLayerConfig(Map<String, Object> layerConfig) {
        this.layerConfig = layerConfig;
    }

    public Map<String, Object> getToolConfig() {
        return toolConfig;
    }

    public void setToolConfig(Map<String, Object> toolConfig) {
        this.toolConfig = toolConfig;
    }
}

