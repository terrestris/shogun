package de.terrestris.shogun.lib.graphql;

import com.google.common.io.Resources;
import de.terrestris.shogun.lib.annotation.GraphQLQuery;
import de.terrestris.shogun.lib.graphql.resolver.BaseEntityTypeResolver;
import de.terrestris.shogun.lib.graphql.resolver.BaseGraphQLDataFetcher;
import de.terrestris.shogun.lib.graphql.scalar.DateTimeScalar;
import de.terrestris.shogun.lib.graphql.scalar.GeometryScalar;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import lombok.extern.log4j.Log4j2;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class GraphQLProvider {

    protected GraphQL graphQL;

    protected GraphQLSchema graphQLSchema;

    @Autowired
    private List<BaseGraphQLDataFetcher> dataFetchers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @Bean
    public GraphQLSchema getSchema() {
        return this.graphQLSchema;
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("Initializing Graph QL");
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

            resourceFiles.add(Resources.toString(resource.getURL(), StandardCharsets.UTF_8));
        }

        String sdl = String.join("\n", resourceFiles);

        log.trace("Built the following GraphQL SDL:\n{}", sdl);

        return sdl;
    }

    protected List<GraphQLScalarType> gatherScalars() {
        List<GraphQLScalarType> scalars = new ArrayList<>();
        scalars.add(ExtendedScalars.Json);
        scalars.add(new GeometryScalar());
        scalars.add(new DateTimeScalar());
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
        String entityName = Character.toLowerCase(simpleClassName.charAt(0)) + simpleClassName.substring(1);

        String queryAllName = String.format("all%s", English.plural(simpleClassName));
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryAllName, dataFetcher.findAll()));
        log.debug("Added GraphQL query {}", queryAllName);

        String queryRevisionsName = String.format("%sRevisionsById", entityName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryRevisionsName, dataFetcher.findRevisions()));
        log.debug("Added GraphQL query {}", queryRevisionsName);

        String queryByIdName = String.format("%sById", entityName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryByIdName, dataFetcher.findOne()));
        log.debug("Added GraphQL query {}", queryByIdName);

        String queryByRevisionName = String.format("%sByIdAndRevision", entityName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryByRevisionName, dataFetcher.findRevision()));
        log.debug("Added GraphQL query {}", queryByRevisionName);

        String queryByTimeName = String.format("%sByIdAndTime", entityName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryByTimeName, dataFetcher.findOneForTime()));
        log.debug("Added GraphQL query {}", queryByTimeName);

        String queryAllByIdsName = String.format("all%sByIds", English.plural(simpleClassName));
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Query")
            .dataFetcher(queryAllByIdsName, dataFetcher.findAllByIds()));
        log.debug("Added GraphQL query {}", queryAllByIdsName);

        String createName = String.format("create%s", simpleClassName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Mutation")
            .dataFetcher(createName, dataFetcher.create()));
        log.debug("Added GraphQL mutation {}", createName);

        String updateName = String.format("update%s", simpleClassName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Mutation")
            .dataFetcher(updateName, dataFetcher.update()));
        log.debug("Added GraphQL mutation {}", updateName);

        String deleteName = String.format("delete%s", simpleClassName);
        typeBuilders.add(TypeRuntimeWiring.newTypeWiring("Mutation")
            .dataFetcher(deleteName, dataFetcher.delete()));
        log.debug("Added GraphQL mutation {}", deleteName);
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
        for (GraphQLScalarType scalar : scalars) {
            runtimeWiring = runtimeWiring.scalar(scalar);
        }
        for (TypeRuntimeWiring.Builder type : types) {
            runtimeWiring = runtimeWiring.type(type);
        }
        // TODO: can this be automated?
        runtimeWiring.type("BaseEntity", typeWiring -> typeWiring.typeResolver(new BaseEntityTypeResolver()));
        return runtimeWiring.build();
    }
}
