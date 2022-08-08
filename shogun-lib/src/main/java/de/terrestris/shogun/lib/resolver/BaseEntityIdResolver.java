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
package de.terrestris.shogun.lib.resolver;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.sun.jdi.InvalidTypeException;
import de.terrestris.shogun.lib.model.BaseEntity;
import de.terrestris.shogun.lib.service.BaseService;
import de.terrestris.shogun.lib.util.ApplicationContextProvider;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Log4j2
public abstract class BaseEntityIdResolver<E extends BaseEntity, S extends BaseService> extends SimpleObjectIdResolver {

    @Autowired
    protected S service;

    @Override
    public E resolveId(IdKey idKey) {
        try {
            if (idKey.key instanceof Long) {
                final Long id = (Long) idKey.key;
                Optional<E> entity = service.findOne(id);

                if (entity.isPresent()) {
                    return entity.get();
                } else {
                    throw new NotFoundException("Could not find entity with ID " + id);
                }
            } else {
                throw new InvalidTypeException("ID is not of type Long.");
            }
        } catch (Exception e) {
            log.error("Could not resolve object: {}", e.getMessage());
            log.trace("Full stack trace: ", e);
            return null;
        }
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        try {
            BaseEntityIdResolver instance = getClass().getDeclaredConstructor().newInstance();
            ApplicationContext applicationContext = ApplicationContextProvider.getContext();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(instance);

            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("Error instantiating a BaseEntityIdResolver: " + e.getMessage());
            log.trace("Full stack trace: ", e);
        }
        return null;
    }

}
