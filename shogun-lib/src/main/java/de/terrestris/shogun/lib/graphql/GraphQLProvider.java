package de.terrestris.shogun.lib.graphql;

import com.google.common.io.Resources;
import de.terrestris.shoguncore.graphql.scalar.GeometryScalar;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class GraphQLProvider {

    protected final Logger LOG = LogManager.getLogger(getClass());

    protected GraphQL graphQL;

    protected GraphQLSchema graphQLSchema;

    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    @ConditionalOnProperty(
        value="shogun.graphql.skipBean",
        havingValue = "false",
        matchIfMissing = true
    )
    public GraphQL graphQL() {
        return graphQL;
    }

    @Bean
    @ConditionalOnProperty(
        value="shogun.graphql.skipBean",
        havingValue = "false",
        matchIfMissing = true
    )
    public GraphQLSchema getSchema() {
        return this.graphQLSchema;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.info("Initializing Graph QL");
        this.buildSchema();
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    protected void buildSchema() throws IOException {
        String sdl = this.gatherResources();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        this.graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    protected String gatherResources() throws IOException {
        URL url = Resources.getResource("graphql/shogun.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        return sdl;
    }

    protected List<GraphQLScalarType> gatherScalars() {
        List<GraphQLScalarType> scalars = new ArrayList<>();
        scalars.add(ExtendedScalars.Json);
        scalars.add(GeometryScalar.GEOMETRY);
        return scalars;
    }

    protected List<TypeRuntimeWiring.Builder> gatherTypes() {
        List<TypeRuntimeWiring.Builder> typeBuilders = new ArrayList<>();

        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("applicationById", graphQLDataFetchers.getApplicationById())
        );
        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allApplications", graphQLDataFetchers.getAllApplications())
        );
        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("layerById", graphQLDataFetchers.getLayerById())
        );
        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allLayers", graphQLDataFetchers.getAllLayers())
        );
        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("userById", graphQLDataFetchers.getUserById())
        );
        typeBuilders.add(
            TypeRuntimeWiring.newTypeWiring("Query")
                .dataFetcher("allUsers", graphQLDataFetchers.getAllUsers())
        );

        return typeBuilders;
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder runtimeWiring = RuntimeWiring.newRuntimeWiring();
        List<GraphQLScalarType> scalars = this.gatherScalars();
        List<TypeRuntimeWiring.Builder> types = gatherTypes();
        for (int i = 0; i < scalars.size(); i++) {
            runtimeWiring = runtimeWiring.scalar(scalars.get(i));
        }
        for (int i = 0; i < types.size(); i++) {
            runtimeWiring = runtimeWiring.type(types.get(i));
        }
        return runtimeWiring.build();
    }
}
