package de.terrestris.shogun.lib.util;

import de.terrestris.shogun.lib.model.BaseEntity;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.apache.logging.log4j.LogManager.getLogger;

public class IdHelper {

    private static final Logger logger = getLogger(IdHelper.class);

    /**
     * Helper method that uses reflection to set the (inaccessible) id field of
     * the given {@link BaseEntity}.
     *
     * @param baseEntity The entity with the inaccessible id field
     * @param id The id to set
     * @throws NoSuchFieldException no such field exception
     */
    public static void setIdForEntity(BaseEntity baseEntity, Long id) throws NoSuchFieldException {
        // use reflection to get the inaccessible final field 'id'
        Field idField = BaseEntity.class.getDeclaredField("id");

        AccessController.doPrivileged((PrivilegedAction<BaseEntity>) () -> {
            idField.setAccessible(true);
            try {
                idField.set(baseEntity, id);
            } catch (IllegalAccessException e) {
                logger.error("Could not set ID field for persistent object", e);
            }
            idField.setAccessible(false);
            return null;
        });
    }

}
