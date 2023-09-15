/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2023-present terrestris GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.terrestris.shogun.lib.model.jsonb.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.terrestris.shogun.lib.enumeration.EditFormComponentType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "component",
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    visible = true,
    defaultImpl = PropertyFormItemEditDefaultConfig.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = PropertyFormItemEditReferenceTableConfig.class,
        name = "REFERENCE_TABLE"
    ),
    @JsonSubTypes.Type(
        value = PropertyFormItemEditDefaultConfig.class,
        names = {
            "CHECKBOX",
            "DATE",
            "DISPLAY",
            "INPUT",
            "NUMBER",
            "SELECT",
            "SWITCH",
            "TEXTAREA",
            "UPLOAD"
        }
    )
})
@Schema(
    discriminatorMapping = {
        @DiscriminatorMapping(
            value = "REFERENCE_TABLE",
            schema = PropertyFormItemEditReferenceTableConfig.class
        ),
        @DiscriminatorMapping(
            value = "CHECKBOX",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "DATE",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "DISPLAY",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "INPUT",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "NUMBER",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "SELECT",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "SWITCH",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "TEXTAREA",
            schema = PropertyFormItemEditDefaultConfig.class
        ),
        @DiscriminatorMapping(
            value = "UPLOAD",
            schema = PropertyFormItemEditDefaultConfig.class
        )
    }
)
public abstract class PropertyFormItemEditConfig extends PropertyFormItemReadConfig {

    @Schema(
        description = "The identifier of the component to render for this property.",
        example = "TEXTAREA",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private EditFormComponentType component;

    @Schema(
        description = "Whether the property is read only or not.",
        example = "true"
    )
    private Boolean readOnly;

    @Schema(
        description = "Whether the property is required or not.",
        example = "true"
    )
    private Boolean required;

}
