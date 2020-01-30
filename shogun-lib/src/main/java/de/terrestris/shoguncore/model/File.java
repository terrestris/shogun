package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

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

    public UUID getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(UUID fileUuid) {
        this.fileUuid = fileUuid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
