package de.terrestris.shogun.lib.graphql.resolver;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BaseEntityTypeResolver implements TypeResolver {

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        Object javaObject = env.getObject();
        log.trace("Getting object type from class name {}", javaObject.getClass().getSimpleName());
        return env.getSchema().getObjectType(javaObject.getClass().getSimpleName());
    }

}
