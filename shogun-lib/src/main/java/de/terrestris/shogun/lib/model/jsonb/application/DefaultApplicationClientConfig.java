/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.model.jsonb.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.terrestris.shogun.lib.annotation.JsonSuperType;
import de.terrestris.shogun.lib.model.jsonb.ApplicationClientConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@JsonDeserialize(as = DefaultApplicationClientConfig.class)
@JsonSuperType(type = ApplicationClientConfig.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class DefaultApplicationClientConfig implements ApplicationClientConfig {

    @Schema(
        description = "The configuration of the map view.",
        required = true
    )
    private DefaultMapView mapView;

    @Schema(
        description = "The description of the application."
    )
    private String description;

    @Schema(
        description = "The links to legal information."
    )
    private DefaultLegalInformation legal;

    @Schema(
        description = "The configuration of the applications theme."
    )
    private DefaultApplicationTheme theme;

}
