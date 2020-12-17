package de.terrestris.shogun.lib.graphql;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GraphQLQuery {
    public String name() default "";
}
