package de.terrestris.shoguncore.graphql;

import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

@Component
public class GraphQLProvider {

    protected final Logger LOG = LogManager.getLogger(getClass());

    private GraphQL graphQL;

    private GraphQLSchema graphQLSchema;

    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.info("Initializing Graph QL");
        URL url = Resources.getResource("graphql/root.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    @Bean
    public GraphQLSchema getSchema() {
        return this.graphQLSchema;
    }

    private void buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        this.graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
            .scalar(ExtendedScalars.Json)
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("applicationById", graphQLDataFetchers.getApplicationById()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allApplications", graphQLDataFetchers.getAllApplications()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("layerById", graphQLDataFetchers.getLayerById()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allLayers", graphQLDataFetchers.getAllLayers()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("userById", graphQLDataFetchers.getUserById()))
            .type(TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allUsers", graphQLDataFetchers.getAllUsers()))
            .build();
    }
}
