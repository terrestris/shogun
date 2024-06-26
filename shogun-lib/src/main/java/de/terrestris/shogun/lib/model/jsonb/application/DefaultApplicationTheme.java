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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class DefaultApplicationTheme implements Serializable {

    @Schema(
        description = "The primary color."
    )
    private String primaryColor;

    @Schema(
        description = "The secondary color."
    )
    private String secondaryColor;

    @Schema(
        description = "The complementary color (e.g. text color, icon color on buttons, …)."
    )
    private String complementaryColor;

    @Schema(
        description = "The path to the logo."
    )
    private String logoPath;

    @Schema(
        description = "The path to the favicon."
    )
    private String faviconPath;

}
