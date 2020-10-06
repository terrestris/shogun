package de.terrestris.shogun.lib.listener;

import lombok.extern.log4j.Log4j2;
import org.ehcache.event.*;

@Log4j2
public class CacheLogger implements CacheEventListener<Object, Object> {

    /**
     * Invoked on {@link CacheEvent CacheEvent} firing.
     * <p>
     * This method is invoked according to the {@link EventOrdering}, {@link EventFiring} and
     * {@link EventType} requirements provided at listener registration time.
     * <p>
     * Any exception thrown from this listener will be swallowed and logged but will not prevent
     * other listeners to run.
     *
     * @param event the actual {@code CacheEvent}
     */
    @Override
    public void onEvent(CacheEvent<?, ?> event) {
        log.info("Key: {} | EventType: {} | Old value: {} | New value: {}",
            event.getKey(), event.getType(), event.getOldValue(),
            event.getNewValue());
    }
}
