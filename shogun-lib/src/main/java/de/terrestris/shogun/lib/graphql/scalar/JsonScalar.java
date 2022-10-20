package de.terrestris.shogun.lib.graphql.scalar;

import graphql.Internal;
import graphql.schema.GraphQLScalarType;

import static de.terrestris.shogun.lib.graphql.scalar.ObjectScalar.OBJECT_COERCING;

@Internal
public class JsonScalar {
    private JsonScalar() {}

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
        .name("JSON")
        .description("A JSON scalar")
        .coercing(OBJECT_COERCING)
        .build();
}
