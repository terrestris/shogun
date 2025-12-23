package de.terrestris.shogun.lib.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

public abstract class BaseEntityDto {

    public interface ReadOnlyFields {
        Long getId();
        void setId(Long id);
        OffsetDateTime getCreated();
        void setCreated(OffsetDateTime created);
        OffsetDateTime getModified();
        void setModified(OffsetDateTime modified);
    }

    /**
     * Base class with read-only fields that will be hidden in Create/Update operations.
     */
    @Data
    public abstract static class ReadOnlyFieldsBase implements ReadOnlyFields {
        @Schema(
            description = "The ID of the entity.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Long id;

        @Schema(
            description = "The timestamp of creation.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private OffsetDateTime created;

        @Schema(
            description = "The timestamp of the last modification.",
            accessMode = Schema.AccessMode.READ_ONLY
        )
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private OffsetDateTime modified;
    }

//    public static class Read {}
//    public static class Create {}
//    public static class Update {}
}
