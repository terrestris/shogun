package de.terrestris.shoguncore.graphql;

import com.google.common.io.Resources;
import de.terrestris.shoguncore.annotation.GraphQLQuery;
import de.terrestris.shoguncore.graphql.fetcher.BaseGraphQLDataFetcher;
import de.terrestris.shoguncore.graphql.scalar.GeometryScalar;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class GraphQLProvider {

    protected final Logger LOG = LogManager.getLogger(getClass());

    protected GraphQL graphQL;

    protected GraphQLSchema graphQLSchema;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @Bean
    public GraphQLSchema getSchema() {
        return this.graphQLSchema;
    }

    @Autowired
    private List<BaseGraphQLDataFetcher> dataFetchers;

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
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:graphql/*.graphqls");

        List<String> resourceFiles = new ArrayList<>();

        log.debug("Found {} GraphQL schema files", resources.length);

        for (Resource resource : resources) {
            log.debug("Found a GraphQL schema file in {}", resource.getURL());

            resourceFiles.add(Resources.toString(resource.getURL(), Charsets.UTF_8));
        }

        String sdl = String.join("\n", resourceFiles);

        log.trace("Built the following GraphQL SDL:\n{}", sdl);

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

        dataFetchers.forEach(dataFetcher -> {
            this.addBaseTypes(typeBuilders, dataFetcher);
            this.addCustomTypes(typeBuilders, dataFetcher);
        });

        return typeBuilders;
    }

    private void addBaseTypes(List<TypeRuntimeWiring.Builder> typeBuilders, BaseGraphQLDataFetcher dataFetcher) {
        String simpleClassName = dataFetcher.getGenericSimpleClassName();

        String queryAllName = String.format("all%s", (English.plural(simpleClassName)));
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryAllName, dataFetcher.findAll()));

        log.debug("Added GraphQL query {}", queryAllName);

        String queryByIdName = String.format("%sById",
            Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1));
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryByIdName, dataFetcher.findOne()));

        log.debug("Added GraphQL query {}", queryByIdName);
    }

    private void addCustomTypes(List<TypeRuntimeWiring.Builder> typeBuilders, BaseGraphQLDataFetcher dataFetcher) {
        for (Method method : dataFetcher.getClass().getDeclaredMethods()) {
            GraphQLQuery annotation = method.getAnnotation(GraphQLQuery.class);
            if (annotation != null) {
                String name = annotation.name();

                try {
                    typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
                        .dataFetcher(name, (DataFetcher) method.invoke(dataFetcher)));

                    log.debug("Added GraphQL query {}", name);
                } catch (Exception e) {
                    log.error("Error while adding GraphQL query {}", name);
                    log.trace("Full stack trace ", e);
                }
            }
        }
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
