package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.*;
import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BaseEntityTypeResolver implements TypeResolver {

    @Override
    public GraphQLObjectType getType(TypeResolutionEnvironment env) {
        Object javaObject = env.getObject();
        if (javaObject instanceof Application) {
            return env.getSchema().getObjectType("Application");
        } else if (javaObject instanceof User) {
            return env.getSchema().getObjectType("User");
        } else if (javaObject instanceof ImageFile) {
            return env.getSchema().getObjectType("ImageFile");
        } else if (javaObject instanceof File) {
            return env.getSchema().getObjectType("File");
        } else if (javaObject instanceof Group) {
            return env.getSchema().getObjectType("Group");
        } else if (javaObject instanceof Layer) {
            return env.getSchema().getObjectType("Layer");
        } else {
            log.warn("Getting object type from class name {}", javaObject.getClass().getSimpleName());
            return env.getSchema().getObjectType(javaObject.getClass().getSimpleName());
        }
    }

}
