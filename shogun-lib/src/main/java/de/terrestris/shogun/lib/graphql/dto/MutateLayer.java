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
package de.terrestris.shogun.lib.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MutateLayer implements Serializable {
    private String name;
    private HashMap<String, Object> clientConfig;
    private HashMap<String, Object> sourceConfig;
    private HashMap<String, Object> features;
    private String type;
}
