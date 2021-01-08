package de.terrestris.shoguncore.graphql.fetcher;

import de.terrestris.shoguncore.model.BaseEntity;
import de.terrestris.shoguncore.service.BaseService;
import graphql.schema.DataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

public abstract class BaseGraphQLDataFetcher<E extends BaseEntity, S extends BaseService> {

    @Autowired
    private S service;

    public DataFetcher findAll() {
        return dataFetchingEnvironment ->  this.service.findAll();
    }

    public DataFetcher findOne() {
        return dataFetchingEnvironment -> {
            Integer projectId = dataFetchingEnvironment.getArgument("id");
            return this.service.findOne(projectId.longValue());
        };
    }

    /**
     * Returns the simple class name of the {@link BaseEntity} this abstract class
     * has been declared with, e.g. 'Application'.
     *
     * @return The simple class name.
     */
    public String getGenericSimpleClassName() {
        Class<?>[] resolvedTypeArguments = GenericTypeResolver.resolveTypeArguments(getClass(),
            BaseGraphQLDataFetcher.class);

        if (resolvedTypeArguments != null && resolvedTypeArguments.length == 2) {
            return resolvedTypeArguments[0].getSimpleName();
        } else {
            return null;
        }
    }

}
