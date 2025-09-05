/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2025-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.model.jsonb.application.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.terrestris.shogun.lib.annotation.ToolSubType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode(callSuper = false)
public class FeatureInfo extends DefaultTool {

    @Schema(
        description = "Whether the tool is visible or not."
    )
    private ArrayList<String> activeCopyTools;

}
