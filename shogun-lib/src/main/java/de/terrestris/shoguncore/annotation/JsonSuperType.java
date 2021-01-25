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
 * If the super type that should get replaced is not an interface, than `override` needs to be set to `true`.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonSuperType {
    Class<? extends Serializable> type();
    boolean override() default false;
}
