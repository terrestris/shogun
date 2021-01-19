package de.terrestris.shoguncore.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The `JsonSuperType` annotation helps to identify a type that should be deserialized instead of a parent type.
 * The `type` property specifies the parent type. This annotation needs to go along with a `JsonDeserialize` pointing
 * to the own type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonSuperType {
    Class<? extends Serializable> type();
}
