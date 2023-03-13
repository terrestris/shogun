package de.terrestris.shogun.lib.dto;

import graphql.PublicApi;
import graphql.relay.DefaultConnection;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@PublicApi
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class DefaultGraphQLConnection<T> extends DefaultConnection<T> {

    private Long totalCount;

    /**
     * A connection consists of a list of edges and page info
     *
     * @param edges    a non null list of edges
     * @param pageInfo a non null page info
     * @throws IllegalArgumentException if edges or page info is null. use {@link Collections#emptyList()} for empty edges.
     */
    public DefaultGraphQLConnection(Long totalCount, List<Edge<T>> edges, PageInfo pageInfo) {
        super(edges, pageInfo);
        this.totalCount = totalCount;
    }

}
