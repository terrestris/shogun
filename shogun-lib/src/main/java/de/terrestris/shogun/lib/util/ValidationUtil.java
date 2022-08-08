/* SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2020-present terrestris GmbH & Co. KG
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
package de.terrestris.shogun.lib.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Log4j2
public class ValidationUtil {

    /**
     * Validates a BindingResult.
     *
     * @param bindingResult The BindingResult to be validated.
     * @return A ResponseEntity with status 422 and an array of validation issues
     * @throws ResponseStatusException If there are validation errors a ResponseStatusException is thrown. These are handled
     *                                 by the Spring ResponseStatusExceptionResolver which allows the validation errors to be included in the HTTP response.
     */
    public static ResponseEntity<Map<String, Object>> validateBindingResult(BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", String.format("Invalid %s", bindingResult.getObjectName()));
        List<String> errors = new ArrayList<>();

        StringBuilder message = new StringBuilder(String.format("Invalid %s input. Errors:", bindingResult.getObjectName()));
        for (ObjectError err : bindingResult.getAllErrors()) {
            message.append(String.format(" %s", err.getDefaultMessage()));
            errors.add(err.getDefaultMessage());
        }
        response.put("message", errors);
        response.put("status", 422);
        response.put("timestamp", new Date().toString());

        log.error("Found validation errors in bindingResult for entity {}: {}", bindingResult.getObjectName(),
            message.toString());

        return ResponseEntity.unprocessableEntity().body(response);
    }

}
