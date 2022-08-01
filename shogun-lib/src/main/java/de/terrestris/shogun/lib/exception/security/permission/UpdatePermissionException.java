/*
 * SHOGun, https://terrestris.github.io/shogun/
 *
 * Copyright © 2022-present terrestris GmbH & Co. KG
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package de.terrestris.shogun.lib.exception.security.permission;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Log4j2
// TODO Not needed?!
public final class UpdatePermissionException extends ResponseStatusException {

    public UpdatePermissionException(Exception e, String message) {
        super(HttpStatus.NOT_FOUND, message);

        log.error("Error while updating the permission: {}", e.getMessage());
        log.trace("Full stack trace: ", e);
    }

    public UpdatePermissionException(Exception e, MessageSource messageSource) {
        this(
            e,
            messageSource.getMessage(
                "BaseController.INTERNAL_SERVER_ERROR",
                null,
                LocaleContextHolder.getLocale()
            )
        );
    }
}

