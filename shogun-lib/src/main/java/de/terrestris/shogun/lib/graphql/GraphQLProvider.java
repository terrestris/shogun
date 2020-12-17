package de.terrestris.shogun.lib.graphql;

import com.google.common.io.Resources;
import de.terrestris.shogun.lib.graphql.scalar.GeometryScalar;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class GraphQLProvider {

    protected final Logger LOG = LogManager.getLogger(getClass());

    protected GraphQL graphQL;

    protected GraphQLSchema graphQLSchema;

    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;

    @Autowired
    private ApplicationContext appContext;

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
        String sdl = Resources.toString(url, StandardCharsets.UTF_8);
        return sdl;
    }

    protected List<GraphQLScalarType> gatherScalars() {
        List<GraphQLScalarType> scalars = new ArrayList<>();
        scalars.add(ExtendedScalars.Json);
        scalars.add(GeometryScalar.GEOMETRY);
        return scalars;
    }

    @SneakyThrows
    protected List<TypeRuntimeWiring.Builder> gatherTypes() {

        Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forJavaClassPath())
            .setScanners(new MethodAnnotationsScanner()));
        Set<Method> methods = reflections.getMethodsAnnotatedWith(GraphQLQuery.class);

        List<TypeRuntimeWiring.Builder> typeBuilders = new ArrayList<>();

        for (Method m: methods) {
            var name = m.getAnnotation(GraphQLQuery.class).name();
            if (name.isBlank()) {
                name = m.getName();
            }

            var bean = appContext.getBean(m.getDeclaringClass());
            typeBuilders.add(
                TypeRuntimeWiring.newTypeWiring("Query")
                    .dataFetcher(name, (DataFetcher) m.invoke(bean))
            );
        }

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
