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
package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.model.File;
import de.terrestris.shogun.lib.service.FileService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class FileGraphQLController extends BaseGraphQLController<File, FileService> {

    @QueryMapping
    public List<File> allFiles() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<File> fileById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public List<File> allFilesByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

}
